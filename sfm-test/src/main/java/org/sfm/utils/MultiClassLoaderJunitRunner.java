package org.sfm.utils;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiClassLoaderJunitRunner extends Suite {

    private static final List<Runner> NO_RUNNERS = Collections.emptyList();
    private final List<Runner> runners;

    public MultiClassLoaderJunitRunner(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);

        LibrarySet librarySet = klass.getAnnotation(LibrarySet.class);
        if (librarySet == null) throw new IllegalArgumentException("Class " + klass + " is missing LibrarySet annotation");

        List<Runner> runners = new ArrayList<Runner>();
        int i = 0;
        for(final String urlsList : librarySet.libraryGroups()) {
            String[] urls = urlsList.split(",");
            final int index = i;
            ClassLoader classLoader = new LibrarySetClassLoader(getClass().getClassLoader(), urls, librarySet.includes());
            Class<?> testClass = classLoader.loadClass(klass.getName());
            runners.add(new ClassLoaderChangerRunner(classLoader, new BlockJUnit4ClassRunner(testClass) {
                @Override
                protected String getName() {
                    return super.getName() + "-" + index;
                }
            }));
            i++;
        }
        this.runners = runners;
    }


    protected List<Runner> getChildren() {
        return this.runners;
    }

}
