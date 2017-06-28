package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.*;
import org.junit.BeforeClass;

import java.net.InetSocketAddress;
import java.util.Arrays;


public class AbstractDatastaxTest  {


    static volatile Cluster cluster = null;


    @BeforeClass
    public static void initCassandraConnection() throws Exception {
        DatastaxHelper.startCassandra();
        if (cluster == null) {
            System.out.println("Open cassandra connection");
            cluster =
                    Cluster
                            .builder()
                            .addContactPointsWithPorts(
                                    Arrays.asList(new InetSocketAddress("localhost", 9142)))
                            .withProtocolVersion(ProtocolVersion.V3)
                            .build();
            System.out.println("opened " + cluster.getMetadata());
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected void testInSession(Callback callback) throws Exception {
        if (callback == null) throw new NullPointerException();
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

            if (session == null) throw new NullPointerException();
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


    @SuppressWarnings("WeakerAccess")
    protected void tearDown(Session session) {
    }

    interface Callback {
        void call(Session session) throws Exception;
    }
}
