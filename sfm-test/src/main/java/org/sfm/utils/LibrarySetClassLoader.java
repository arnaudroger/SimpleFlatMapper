package org.sfm.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibrarySetClassLoader extends URLClassLoader {
    private final ClassLoader classLoader;
    private final String[] libraries;

    public LibrarySetClassLoader(ClassLoader classLoader, String[] libraries, Class<?>[] includes) throws IOException {
        super(getUrls(libraries, includes), Integer.class.getClassLoader());
        this.classLoader = classLoader;
        this.libraries = libraries;
    }

    private static URL[] getUrls(String[] libraries, Class<?>[] includes) throws IOException {
        List<URL> urls = new ArrayList<URL>();

        for(int i = 0; i < libraries.length; i++) {
            File f = LibraryClassLoaderUtil.downloadIfNotThere(libraries[i]);
            urls.add(f.toURI().toURL());
        }

        for(Class<?> includeClass : includes) {
            URLClassLoader urlClassLoader = getURLClassLoader(includeClass);
            if (urlClassLoader == null) {
                throw new IllegalArgumentException("No URL class loader for " + includeClass);
            }

            for(URL url : urlClassLoader.getURLs()) {
                if (urlContains(url, includeClass)) {
                    if (!urls.contains(url)) {
                        urls.add(url);
                    }
                    break;
                }
            }
            throw new IllegalArgumentException("Could not find url for " + includeClass);
        }

        return urls.toArray(new URL[0]);
    }

    private static boolean urlContains(URL url, Class<?> includeClass) {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url});
        return urlClassLoader.findResource(includeClass.getName().replace(".", "/") + ".class") != null;
    }

    private static URLClassLoader getURLClassLoader(Class<?> includeClass) {
        ClassLoader cl = includeClass.getClassLoader();
        while(cl != null && !(cl instanceof URLClassLoader)) {
            cl = cl.getParent();
        }
        return (URLClassLoader) cl;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            return classLoader.loadClass(name);
        }
    }

    @Override
    public String toString() {
        return "LibrarySetClassLoader{" +
                "libraries=" + Arrays.toString(libraries) +
                '}';
    }
}
