package org.sfm.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class LibraryClassLoaderUtil {

    public static File downloadIfNotThere(String url) throws IOException{
        File localJar = getLocalJar(url);
        if (!localJar.exists()) {
            downloadJar(url, localJar);
        }
        return localJar;
    }

    private static File getLocalJar(String url) throws MalformedURLException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        URL u = new URL(url);
        return new File(tmpDir, u.getPath());
    }

    private static void downloadJar(String url, File localJar) throws IOException {
        System.out.println("download = " + url + " to " + localJar);
        URL u = new URL(url);
        localJar.getParentFile().mkdirs();
        InputStream inputStream = u.openStream();
        try {
            byte[] buffer = new byte[4 * 1024];
            OutputStream outputStream = new FileOutputStream(localJar);
            try {
                int l;
                while((l = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, l);
                }
            } finally {
                outputStream.close();
            }
        } finally {
            inputStream.close();
        }
        System.out.println("download finished");
    }
}
