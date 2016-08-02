package org.simpleflatmapper.test.junit;

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

        LibrarySets librarySets = klass.getAnnotation(LibrarySets.class);
        List<Runner> runners = new ArrayList<Runner>();
        if (librarySets == null) throw new IllegalArgumentException("Class " + klass + " is missing LibrarySets annotation");

        int i = 0;
        String[] names = librarySets.names();
        Pattern[] excludes = toPattern(librarySets.excludes());
        for(final String urlsList : librarySets.librarySets()) {
            String[] urls = urlsList.split(",");
            final String suffix = getName(names, i);
            ClassLoader classLoader = new LibrarySetsClassLoader(getClass().getClassLoader(), urls, librarySets.includes(), excludes);

            Suite.SuiteClasses annotation = klass.getAnnotation(Suite.SuiteClasses.class);
            if (annotation != null) {
                for(Class cl : annotation.value()) {
                    Class<?> testClass = classLoader.loadClass(cl.getName());
                    runners.add(new ClassLoaderChangerRunner(classLoader, new BlockJUnit4ClassRunner(testClass) {
                        @Override
                        protected String getName() {
                            return super.getName() + suffix;
                        }
                    }));
                    i++;
                }
            } else {
                Class<?> testClass = classLoader.loadClass(klass.getName());
                runners.add(new ClassLoaderChangerRunner(classLoader, new BlockJUnit4ClassRunner(testClass) {
                    @Override
                    protected String getName() {
                        return super.getName() + suffix;
                    }
                }));
                i++;
            }
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
