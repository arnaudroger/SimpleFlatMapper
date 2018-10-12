package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.SelectQuery;
import org.simpleflatmapper.jdbc.property.IndexedSetterProperty;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.MysqlDbHelper;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.CheckedConsumer;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class CrudTest {


    @Parameterized.Parameters(name = "db:{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {DbHelper.TargetDB.HSQLDB}, {DbHelper.TargetDB.MYSQL}
                , {DbHelper.TargetDB.POSTGRESQL}
        });
    }

    @Parameterized.Parameter
    public DbHelper.TargetDB targetDB;
    @Test
    public void testDbObjectCrudAutoInc() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT_AUTOINC");

            DbObject object = DbObject.newInstance();
            object.setId(-22225);

            // create
            Long key =
                    objectCrud.create(connection, object, new CheckedConsumer<Long>() {
                Long key;
                @Override
                public void accept(Long aLong) throws Exception {
                    key = aLong;
                }
            }).key;

            assertFalse(key.equals(object.getId()));

            object.setId(key);

            // read
            assertEquals(object, objectCrud.read(connection, key));

            object.setName("Udpdated");

            // update
            objectCrud.update(connection, object);
            assertEquals(object, objectCrud.read(connection, key));

            // delete
            objectCrud.delete(connection, key);
            assertNull(objectCrud.read(connection, key));

            objectCrud.create(connection, DbObject.newInstance());

            // batch

            final List<Long> keys  = new ArrayList<Long>();
            final List<DbObject> values = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());
            objectCrud.create(connection, values, new CheckedConsumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    values.get(keys.size()).setId(aLong);
                    keys.add(aLong);
                }
            });

            assertCollectionEquals(values, objectCrud.read(connection, keys, new ListCollector<DbObject>()).getList());


            int i = 333;
            for(DbObject value : values) {
                value.setEmail(Integer.toHexString(i));
                i++;
            }

            objectCrud.update(connection, values);

            assertCollectionEquals(values, objectCrud.read(connection, keys, new ListCollector<DbObject>()).getList());


            objectCrud.delete(connection, keys);

            assertCollectionEquals(Collections.<DbObject>emptyList(), objectCrud.read(connection, keys, new ListCollector<DbObject>()).getList());

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectCrud() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            checkCrudDbObject(connection, objectCrud, DbObject.newInstance());

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectCrudTable() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            Crud<DbObjectTable, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObjectTable, Long>crud(DbObjectTable.class, Long.class).to(connection);

            checkCrudDbObject(connection, objectCrud, DbObject.newInstance(new DbObjectTable()));

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectLazyCrud() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table("TEST_DB_OBJECT");

            checkCrudDbObject(connection, objectCrud, DbObject.newInstance());

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectLazyCrudTable() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            Crud<DbObjectTable, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObjectTable, Long>crud(DbObjectTable.class, Long.class).crud();

            checkCrudDbObject(connection, objectCrud, DbObject.newInstance(new DbObjectTable()));

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectCrudTestDbObject() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            Crud<TestDbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<TestDbObject, Long>crud(TestDbObject.class, Long.class).to(connection);

            checkCrudDbObject(connection, objectCrud, DbObject.newInstance(new TestDbObject()));

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectCrudWithCustomGetter() throws Exception {

        JdbcMapperFactory mapperFactory =
                JdbcMapperFactory
                        .newInstance()
                        .addColumnProperty("name",
                                new GetterProperty(new Getter<ResultSet, String>() {
            @Override
            public String get(ResultSet target) throws Exception {
                return "customname";
            }
        }));

        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            Crud<TestDbObject, Long> objectCrud =
                    mapperFactory.<TestDbObject, Long>crud(TestDbObject.class, Long.class).to(connection);

            TestDbObject testDbObject = DbObject.newInstance(new TestDbObject());

            objectCrud.create(connection, testDbObject);

            TestDbObject read = objectCrud.read(connection, testDbObject.getId());

            assertEquals("customname", read.getName());
            assertEquals(testDbObject.getEmail(), read.getEmail());


        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectCrudWithCustomSetter() throws Exception {

        JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance().addColumnProperty("name", new IndexedSetterProperty(new IndexedSetter<PreparedStatement, Object>() {
            @Override
            public void set(PreparedStatement target, Object value, int index) throws Exception {
                target.setString(index, "customname");
            }
        }));

        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            Crud<TestDbObject, Long> objectCrud =
                    mapperFactory.<TestDbObject, Long>crud(TestDbObject.class, Long.class).to(connection);

            TestDbObject testDbObject = DbObject.newInstance(new TestDbObject());
            assertNotEquals("customname", testDbObject);

            objectCrud.create(connection, testDbObject);

            TestDbObject read = objectCrud.read(connection, testDbObject.getId());

            assertEquals("customname", read.getName());
            assertEquals(testDbObject.getEmail(), read.getEmail());


        } finally {
            connection.close();
        }
    }
    
    @Test
    public void testProtectedKeyWord() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);

        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }
        try {
            if (targetDB == DbHelper.TargetDB.MYSQL) {
                try {
                    connection.createStatement().execute("DROP TABLE  `select`;");
                } catch (Exception e) {
                }
                connection.createStatement().execute("CREATE TABLE  `select` ( v integer PRIMARY KEY);");
                
            } else {
                try {
                    connection.createStatement().execute("DROP TABLE  \"select\";");
                } catch (Exception e) {
                }
                connection.createStatement().execute("CREATE TABLE  \"select\" ( v integer PRIMARY KEY);");
            }
            

            Crud<Select, Integer> select = JdbcMapperFactory.newInstance().crud(Select.class, Integer.class).table("select");
            
            Select s = new Select();
            s.v = 3;
            select.create(connection, s);
            
            assertEquals(3, select.read(connection, 3).v);
            
            
            select.delete(connection, 3);


            select = JdbcMapperFactory.newInstance().crud(Select.class, Integer.class).table(targetDB == DbHelper.TargetDB.MYSQL ? "`select`" : "\"select\"");

            select.create(connection, s);

            assertEquals(3, select.read(connection, 3).v);

            select.delete(connection, 3);
            
        } finally {
            connection.close();
        }

    }
    public static class Select {
        public int v;
    }

    @Table(name = "TEST_DB_OBJECT")
    public static class DbObjectTable extends DbObject {
    }

    public static class TestDbObject extends DbObject {
    }

    public static <T extends DbObject> void checkCrudDbObject(Connection connection, Crud<T, Long> objectCrud, T object) throws SQLException {

        assertNull(objectCrud.where(" 1 = 2 ", Void.class).readFirst(connection, null));

        assertNull(objectCrud.read(connection, object.getId()));

        // create
        Long key =
                objectCrud.create(connection, object, new CheckedConsumer<Long>() {
                    Long key;
                    @Override
                    public void accept(Long aLong) throws Exception {
                        key = aLong;
                    }
                }).key;

        assertNull(key);


        key = object.getId();
        // read
        assertEquals(object, objectCrud.read(connection, key));
        assertEquals(Arrays.asList(object), objectCrud.read(connection, Arrays.asList(key), new ListCollector<T>()).getList());

        object.setName("Udpdated");

        // update
        objectCrud.update(connection, object);
        assertEquals(object, objectCrud.read(connection, key));

        // delete
        objectCrud.delete(connection, key);
        assertNull(objectCrud.read(connection, key));

        objectCrud.create(connection, Arrays.asList(object));


        SelectQuery<T, Object> selectQuery = objectCrud.where(" id = :id ", Long.class);
        assertSame(selectQuery, objectCrud.where(" id = :id ", Long.class));
        assertEquals(object, selectQuery.readFirst(connection, object.getId()));
        assertEquals(Arrays.asList(object), objectCrud.where(" email =:email and name = :name", object.getClass()).read(connection, object, new ListCollector<T>()).getList());
        assertEquals(Arrays.asList(object), objectCrud.where(" email =:email and name = :name",Object[].class).read(connection, new Object[] {object.getEmail(), object.getName()}, new ListCollector<T>()).getList());

        objectCrud.delete(connection, Arrays.asList(object.getId()));

        assertNull(selectQuery.readFirst(connection, object.getId()));

        assertNull(objectCrud.create(connection, Arrays.asList(object), new CheckedConsumer<Long>() {
            Long key;
            @Override
            public void accept(Long aLong) throws Exception {
                this.key = aLong;
            }
        }).key);


        assertEquals(object, selectQuery.readFirst(connection, object.getId()));

        object.setName("Udpdated 2");
        objectCrud.update(connection, Arrays.asList(object));

        assertEquals(object, selectQuery.readFirst(connection, object.getId()));

        objectCrud.delete(connection, object.getId());
        assertNull(selectQuery.readFirst(connection, object.getId()));

        objectCrud.create(connection, object);

        assertEquals(object, selectQuery.readFirst(connection, object.getId()));



    }


    public static class CKEY {
        public long id;
        public String name;

    }
    @Test
    public void testCompositeKey() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }

        try {
            Crud<DbObject, CKEY> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, CKEY>crud(DbObject.class, CKEY.class).table(connection, "TEST_DB_OBJECT_CKEY");

            DbObject object = DbObject.newInstance();

            CKEY ckey = new CKEY();
            ckey.id = object.getId();
            ckey.name = object.getName();

            assertNull(objectCrud.read(connection, ckey));

            // create
            CKEY key =
                    objectCrud.create(connection, object, new CheckedConsumer<CKEY>() {
                        CKEY key;
                        @Override
                        public void accept(CKEY aLong) throws Exception {
                            key = aLong;
                        }
                    }).key;

            assertNull(key);


            key = ckey;
            // read
            assertEquals(object, objectCrud.read(connection, key));

            object.setEmail("Udpdated");

            // update
            objectCrud.update(connection, object);
            assertEquals(object, objectCrud.read(connection, key));

            // delete
            objectCrud.delete(connection, key);
            assertNull(objectCrud.read(connection, key));


            List<DbObject> values = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());
            final List<CKEY> keys  = new ArrayList<CKEY>();
            for(DbObject value : values) {
                CKEY k = new CKEY();
                k.id = value.getId();
                k.name = value.getName();
                keys.add(k);
            }

            objectCrud.create(connection, values);

            assertCollectionEquals(values, objectCrud.read(connection, keys, new ListCollector<DbObject>()).getList());


            int i = 333;
            for(DbObject value : values) {
                value.setEmail(Integer.toHexString(i));
                i++;
            }

            objectCrud.update(connection, values);

            assertCollectionEquals(values, objectCrud.read(connection, keys, new ListCollector<DbObject>()).getList());


            objectCrud.delete(connection, keys);

            assertCollectionEquals(Collections.<DbObject>emptyList(), objectCrud.read(connection, keys, new ListCollector<DbObject>()).getList());
        } finally {
            connection.close();
        }
    }
    
    @Test
    public void testOnlyKey497() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        if (connection == null) { System.err.println("Db " + targetDB + " not available"); return; }

        try {
            Crud<OnlyKey, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<OnlyKey, Long>crud(OnlyKey.class, Long.class).table(connection, "TEST_ONLY_KEY");

            OnlyKey object = new OnlyKey(1);
            objectCrud.delete(connection, Arrays.asList(1l, 2l));

            assertNull(objectCrud.read(connection, 1l));

            // create
            objectCrud.create(connection, object);

            // read
            assertEquals(object, objectCrud.read(connection, 1l));

            // update
            objectCrud.update(connection, object);
            // delete
            objectCrud.delete(connection, 1l);
            assertNull(objectCrud.read(connection, 1l));

            objectCrud.create(connection, Arrays.asList(new OnlyKey(1), new OnlyKey(2)));
            try {
                objectCrud.createOrUpdate(connection, object);
                objectCrud.createOrUpdate(connection, Arrays.asList(new OnlyKey(1), new OnlyKey(2)));
            } catch (UnsupportedOperationException e) {
                // 
            }
        } finally {
            connection.close();
        }
    }
    
    public static class OnlyKey {
        public final long id;

        public OnlyKey(long id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OnlyKey onlyKey = (OnlyKey) o;

            return id == onlyKey.id;
        }

        @Override
        public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    }

    private void assertCollectionEquals(List<DbObject> objects, List<DbObject> list) {
        assertEquals(objects.size(), list.size());

        for(DbObject v: objects) {
            assertTrue(list.contains(v));
        }

    }

    public static void main(String[] args) throws SQLException {
        Connection connection = MysqlDbHelper.objectDb();

        Crud<DbObject, Long> crud = JdbcMapperFactory
                .newInstance()
                .crud(DbObject.class, Long.class)
                .table(connection, "TEST_DB_OBJECT");

        connection.createStatement().execute("TRUNCATE TABLE TEST_DB_OBJECT");
        List<DbObject> objects = new ArrayList<DbObject>(10000);
        for(int i = 0; i < 65001; i++) {
            DbObject e = DbObject.newInstance();
            e.setId(i + 1);
            objects.add(e);
        }

        crud.create(connection, objects);

    }

}