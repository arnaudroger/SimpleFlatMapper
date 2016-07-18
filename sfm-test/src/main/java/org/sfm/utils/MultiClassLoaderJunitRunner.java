package org.sfm.utils;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class MultiClassLoaderJunitRunner extends Suite {

    private static final List<Runner> NO_RUNNERS = Collections.emptyList();
    private final List<Runner> runners;

    public MultiClassLoaderJunitRunner(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);

        LibrarySet librarySet = klass.getAnnotation(LibrarySet.class);
        if (librarySet == null) throw new IllegalArgumentException("Class " + klass + " is missing LibrarySet annotation");

        List<Runner> runners = new ArrayList<Runner>();
        int i = 0;
        String[] names = librarySet.names();
        Pattern[] excludes = toPattern(librarySet.excludes());
        for(final String urlsList : librarySet.libraryGroups()) {
            String[] urls = urlsList.split(",");
            final String suffix = getName(names, i);
            ClassLoader classLoader = new LibrarySetClassLoader(getClass().getClassLoader(), urls, librarySet.includes(), excludes);
            Class<?> testClass = classLoader.loadClass(klass.getName());
            runners.add(new ClassLoaderChangerRunner(classLoader, new BlockJUnit4ClassRunner(testClass) {
                @Override
                protected String getName() {
                    return super.getName() +  suffix;
                }
            }));
            i++;
        }
        this.runners = runners;
    }

    private Pattern[] toPattern(String[] excludes) {
        Pattern[] ps = new Pattern[excludes.length];

        for(int i = 0; i < ps.length; i++) {
            ps[i] = Pattern.compile(excludes[i]);
        }

        return ps;
    }

    private String getName(String[] names, int i) {
        if (i < names.length) {
            return names[i];
        }
        return "" + i;
    }


    protected List<Runner> getChildren() {
        return this.runners;
    }

}
