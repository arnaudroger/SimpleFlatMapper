package org.simpleflatmapper.test.junit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class LibrarySetsClassLoader extends URLClassLoader {
    private final ClassLoader classLoader;
    private final String[] libraries;
    private final Pattern[] excludes;

    public LibrarySetsClassLoader(ClassLoader classLoader, String[] libraries, Class<?>[] includes, Pattern[] excludes) throws IOException {
        super(getUrls(libraries, includes), Integer.class.getClassLoader());
        this.classLoader = classLoader;
        this.libraries = libraries;
        this.excludes = excludes;
    }

    private static URL[] getUrls(String[] libraries, Class<?>[] includes) throws IOException {
        List<URL> urls = new ArrayList<URL>();

        for(int i = 0; i < libraries.length; i++) {
            urls.add(new URL(libraries[i]));
        }

        for(Class<?> includeClass : includes) {
            URL url = findUrl(includeClass, includeClass.getClassLoader());
            if (!urls.contains(url)) {
                urls.add(url);
            }
        }

        return urls.toArray(new URL[0]);


    }

    public static URL findUrl(Class<?> includeClass, ClassLoader classLoader) throws MalformedURLException {

        String classResource = includeClass.getName().replace(".", "/") + ".class";
        URL urlClass = classLoader.getResource(classResource);

        if (urlClass != null) {
            String url = urlClass.toString();

            if (url.startsWith("jar:")) {
                int bang = url.indexOf('!');
                if (bang != -1) {
                    String jarUrl = url.substring("jar:".length(), bang);
                    System.out.println(includeClass + " => "  + urlClass  + " => jarUrl = " + jarUrl);
                    return new URL(jarUrl);
                }
            } else if (url.startsWith("file:")) {
                if (url.endsWith(classResource)) {
                    String directoryUrl = url.substring(0, url.length() - classResource.length());
                    System.out.println(includeClass + " => directoryUrl = " + directoryUrl);
                    return new URL(directoryUrl);
                }
            }
        }

        throw new IllegalArgumentException("Could not find url for " + includeClass + " " + urlClass);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (isExcluded(name)) {
            return classLoader.loadClass(name);
        }
        try {
            return super.loadClass(name);
        } catch (ClassNotFoundException e) {
            return classLoader.loadClass(name);
        }
    }

    private boolean isExcluded(String name) {
        for(Pattern p : excludes) {
            if(p.matcher(name).find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "LibrarySetsClassLoader{" +
                "libraries=" + Arrays.toString(libraries) +
                '}';
    }
}
