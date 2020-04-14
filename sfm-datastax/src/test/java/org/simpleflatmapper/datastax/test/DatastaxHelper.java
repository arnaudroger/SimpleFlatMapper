package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.DataType;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Map;

public class DatastaxHelper {
    private static final String CASSANDRA_STARTED = "sfm.cassandra.started";

    public static void startCassandra() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(DatastaxHelper.class.getClassLoader());
            // deal with multiple classloader
            synchronized (CASSANDRA_STARTED) {
                if (System.getProperty(CASSANDRA_STARTED) == null) {
                    fixTypeCodec();
                    // cassandra does some check on the java version
                    // expect a dot not present in java 9 ea 126
                    String vmversion = System.getProperty("java.vm.version");
                    if (vmversion.startsWith("9-ea")) {
                        System.out.println("override java version prop");
                        System.setProperty("java.vm.version", "25.51-b03");
                    }

                    System.setProperty("disk_failure_policy", "ignore");

                    File configFile = new File("target/embeddedCassandra/cu-cassandra.yaml");

                    configFile.getParentFile().mkdirs();

                    InputStream is = EmbeddedCassandraServerHelper.class.getResourceAsStream("/cu-cassandra.yaml");
                    try {
                        OutputStream os = new FileOutputStream(configFile);

                        byte[] buffer = new byte[4096];
                        try {
                            int l;
                            while ((l = is.read(buffer)) != -1) {
                                os.write(buffer, 0, l);
                            }
                        } finally {
                            os.close();
                        }


                    } finally {
                        is.close();
                    }

                    String cassandraConfig = "file:" + configFile.getAbsolutePath();


                    System.setProperty("cassandra.config", cassandraConfig);
                    System.setProperty("cassandra.native.epoll.enabled", "false");
                    System.setProperty("cassandra.disk_failure_policy", "ignore");

                    System.out.println("Starting Cassandra " + cassandraConfig);
                    EmbeddedCassandraServerHelper.startEmbeddedCassandra(300000L);
                    System.out.println("Started Cassandra");

                    System.setProperty(CASSANDRA_STARTED, "true");
                } else {
                    System.out.println("CASSANDRA_STARTED = " + System.getProperty(CASSANDRA_STARTED));
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    @SuppressWarnings("unchecked")
    private static void fixTypeCodec() {
        try {
            System.out.println("PRINT CONNENDRUM");


            Field f = Class.forName("com.datastax.driver.core.TypeCodec").getDeclaredField("primitiveCodecs");
            f.setAccessible(true);

            Map o = (Map) f.get(null);
            System.out.println("primitiveCodecs = " + o);

            Class<?> longCodec = Class.forName("com.datastax.driver.core.TypeCodec$LongCodec");

            Field instance;
            try {
                instance = longCodec.getDeclaredField("newInstance");
            } catch (Exception e) {
                instance = longCodec.getDeclaredField("instance");
            }
            instance.setAccessible(true);
            Object longCodeInstance = instance.get(null);
            System.out.println("LongCodec.newInstance = " + longCodeInstance);

            if (o.get(DataType.Name.BIGINT) == null) {
                //noinspection unchecked,unchecked,unchecked
                o.put(DataType.Name.BIGINT, longCodeInstance);
                o.put(DataType.Name.COUNTER, longCodeInstance);

                o = (Map) f.get(null);
                System.out.println("fixed primitiveCodecs = " + o);
            }


        } catch(Throwable e ) {
            e.printStackTrace(System.out);
            // used only for 2.1 drivers
        }
    }
}
