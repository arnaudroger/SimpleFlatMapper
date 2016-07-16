package org.sfm.datastax.utils;

import com.datastax.driver.core.DataType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Datastax3ClassLoaderUtil {

    private static final String DATASTAX_JAR = "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.0.3/cassandra-driver-core-3.0.3.jar";

    private static final File localJar = new File("target/downloaded-jar/cassandra-driver-core-3.0.3.jar");


    public static ClassLoader getDatastax3ClassLoader() throws IOException {
        downloadIfNotThere();
        return new Datastax3ClassLoader(Datastax3ClassLoaderUtil.class.getClassLoader(), localJar);
    }

    private static void downloadIfNotThere() throws IOException{
        if (!localJar.exists()) {
            downloadJar();
        }
    }
    private static void downloadJar() throws IOException {
        URL url = new URL(DATASTAX_JAR);
        localJar.getParentFile().mkdirs();
        InputStream inputStream = url.openStream();
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
    }
}
