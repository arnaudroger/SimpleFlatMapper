package org.sfm.datastax;

import com.datastax.driver.core.*;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("ALL")
public class AbstractDatastaxTest  {

    static volatile boolean isStarted = false;

    static Cluster cluster = null;

    @BeforeClass
    public static void startCassandra() throws Exception {
        try {
            if (!isStarted) {

                fixTypeCodec();

                // cassandra does some check on the java version
                // expect a dot not present in java 9 ea 126
                String vmversion = System.getProperty("java.vm.version");
                if (vmversion.indexOf('.') == -1) {
                    System.out.println("override java version prop");
                    System.setProperty("java.vm.version", "25.51-b03");
                }

                File configFile = new File("target/embeddedCassandra/cu-cassandra.yaml");

                configFile.getParentFile().mkdirs();

                InputStream is = EmbeddedCassandraServerHelper.class.getResourceAsStream("/cu-cassandra.yaml");
                try {
                    OutputStream os = new FileOutputStream(configFile);

                    byte[] buffer = new byte[4096];
                    try {
                        int l;
                        while((l = is.read(buffer)) != -1) {
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
                System.out.println("Starting Cassandra " + cassandraConfig);
                EmbeddedCassandraServerHelper.startEmbeddedCassandra(300_000L);
                isStarted = true;
                System.out.println("Started Cassandra");

                cluster =
                        Cluster
                                .builder()
                                .addContactPointsWithPorts(
                                        Arrays.asList(new InetSocketAddress("localhost", 9142)))
                                .build();

                Metadata metadata = cluster.getMetadata();

                assertEquals("Test Cluster", metadata.getClusterName());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void testInSession(Callback callback) throws Exception {
        Session session =  null;

        try {

            if (cluster.getMetadata().getKeyspace("sfm") == null) {
                cluster.newSession().execute("create schema sfm WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }");
            }

            session = cluster.connect("sfm");

            session.init();

            KeyspaceMetadata sfm = cluster.getMetadata().getKeyspace("sfm");
            if (sfm.getTable("dbobjects") == null) {
                session.execute("create table dbobjects (" +
                        "id bigint primary key, " +
                        "name varchar, " +
                        "email varchar," +
                        "creation_time timestamp," +
                        "type_ordinal int," +
                        "type_name varchar)");

            }
            session.execute("truncate dbobjects");
            session.execute("insert into dbobjects(id, name, email, creation_time, type_ordinal, type_name) values(1, 'Arnaud Roger', 'arnaud.roger@gmail.com', '2012-10-2 12:10:10', 1, 'type3')");

            if (sfm.getTable("dbobjects_set") == null) {
                session.execute("create table dbobjects_set(id bigint primary key, emails set<text>)");
                session.execute("insert into dbobjects_set(id, emails) values(1, {'a@a', 'b@b'})");
            }

            if (sfm.getTable("dbobjects_list") == null) {
                session.execute("create table dbobjects_list(id bigint primary key, emails list<text>)");
                session.execute("insert into dbobjects_list(id, emails) values(1, ['a@a', 'b@b'])");
            }

            if (sfm.getTable("dbobjects_map") == null) {
                session.execute("create table dbobjects_map(id bigint primary key, emails map<int, text>)");
                session.execute("insert into dbobjects_map(id, emails) values(1, { 10 : 'a@a', 23 : 'b@b'})");
            }

            if (sfm.getTable("dbobjects_mapll") == null) {
                session.execute("create table dbobjects_mapll(id bigint primary key, ll map<int, int>)");
                session.execute("insert into dbobjects_mapll(id, ll) values(1, { 10 : 100, 20 : 200})");
            }

            if (sfm.getTable("dbobjects_tuple") == null) {
                session.execute("create table dbobjects_tuple(id bigint primary key, t frozen <tuple<text, bigint, int>>)");
                session.execute("insert into dbobjects_tuple(id, t) values(1, ('t1', 12, 13))");
            }

            if (sfm.getTable("dbobjects_udt") == null) {
                session.execute("create type mytype ( str text, l bigint )");
                session.execute("create table dbobjects_udt(id bigint primary key, t frozen <mytype>)");
                session.execute("insert into dbobjects_udt(id, t) values(1, {str : 't1', l : 12})");
            }

            if (sfm.getTable("dbobjects_udttyplelist") == null) {
                session.execute("create type mytype2 ( str text, t frozen <tuple<bigint, list<int>>> )");
                session.execute("create table dbobjects_udttyplelist(id bigint primary key, t frozen <mytype2>)");
                session.execute("insert into dbobjects_udttyplelist(id, t) values(1, {str : 't1', t : (12, [13, 14])})");
            }

            if (sfm.getTable("dbobjects_listint") == null) {
                session.execute("create table dbobjects_listint(id bigint primary key, l list<int>)");
                session.execute("insert into dbobjects_listint(id, l) values(1,  [13, 14])");
            }

            if (sfm.getTable("dbobjects_setint") == null) {
                session.execute("create table dbobjects_setint(id bigint primary key, l set<int>)");
                session.execute("insert into dbobjects_setint(id, l) values(1,  {13, 14})");
            }

            if (sfm.getTable("dbobjects_listudt") == null) {
                session.execute("create table dbobjects_listudt(id bigint primary key, l list<frozen <mytype>>)");
                session.execute("insert into dbobjects_listudt(id, l) values(1,  [ {str : 't1', l : 12}])");
            }

            if (sfm.getTable("dbobjects_listtuple") == null) {
                session.execute("create table dbobjects_listtuple(id bigint primary key, l list<frozen <tuple<text, bigint>>>)");
                session.execute("insert into dbobjects_listtuple(id, l) values(1,  [ ( 't1',  12)])");
            }

            if (sfm.getTable("dbobjects_setudt") == null) {
                session.execute("create table dbobjects_setudt(id bigint primary key, l set<frozen <mytype>>)");
                session.execute("insert into dbobjects_setudt(id, l) values(1,  {{str : 't1', l : 12}})");
            }

            if (sfm.getTable("dbobjects_mapudt") == null) {
                session.execute("create table dbobjects_mapudt(id bigint primary key, l map<int, frozen <mytype>>)");
                session.execute("insert into dbobjects_mapudt(id, l) values(1,  {2 : {str : 't1', l : 12}})");
            }

            callback.call(session);
        } finally {
            try {
                tearDown(session);
            } catch(Exception e) {
                e.printStackTrace();
            }
            if (session != null) session.close();
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

            Field instance = longCodec.getDeclaredField("instance");
            instance.setAccessible(true);
            Object longCodeInstance = instance.get(null);
            System.out.println("LongCodec.instance = " + longCodeInstance);

            if (o.get(DataType.Name.BIGINT) == null) {
                //noinspection unchecked,unchecked,unchecked
                o.put(DataType.Name.BIGINT, longCodeInstance);
                o.put(DataType.Name.COUNTER, longCodeInstance);

                o = (Map) f.get(null);
                System.out.println("primitiveCodecs = " + o);
            }


        } catch(Throwable e ) {
            System.err.println("Ooops ... " + e);
            e.printStackTrace();
        }
    }

    protected void tearDown(Session session) {
    }

    interface Callback {
        void call(Session session) throws Exception;
    }
}
