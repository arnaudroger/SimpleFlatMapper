package org.simpleflatmapper.test.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Created by aroger on 18/07/2016.
 */
public class ClassLoaderChangerRunner extends Runner {
    private final BlockJUnit4ClassRunner delegate;
    private final ClassLoader classLoader;

    public ClassLoaderChangerRunner(ClassLoader classLoader, BlockJUnit4ClassRunner delegate) {
        this.delegate = delegate;
        this.classLoader = classLoader;
    }

    @Override
    public Description getDescription() {
        return delegate.getDescription();
    }

    @Override
    public void run(RunNotifier runNotifier) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            delegate.run(runNotifier);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
}
