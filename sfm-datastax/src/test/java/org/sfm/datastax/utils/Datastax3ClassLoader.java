package org.sfm.datastax.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Datastax3ClassLoader extends URLClassLoader {
    private final ClassLoader classLoader;

    public Datastax3ClassLoader(ClassLoader classLoader, File localJar) throws MalformedURLException {
        super(new URL[] { localJar.toURI().toURL() }, Integer.class.getClassLoader());
        this.classLoader = classLoader;


    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            return classLoader.loadClass(name);
        }
    }
}
