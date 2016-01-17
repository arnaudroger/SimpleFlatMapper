package org.sfm.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.DefaultCrud;
import org.sfm.test.jdbc.DbHelper;
import org.sfm.test.jdbc.MysqlDbHelper;
import org.sfm.utils.ListCollectorHandler;
import org.sfm.utils.RowHandler;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

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
                    objectCrud.create(connection, object, new RowHandler<Long>() {
                Long key;
                @Override
                public void handle(Long aLong) throws Exception {
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
            objectCrud.create(connection, values, new RowHandler<Long>() {
                @Override
                public void handle(Long aLong) throws Exception {
                    values.get(keys.size()).setId(aLong);
                    keys.add(aLong);
                }
            });

            assertCollectionEquals(values, objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());


            int i = 333;
            for(DbObject value : values) {
                value.setEmail(Integer.toHexString(i));
                i++;
            }

            objectCrud.update(connection, values);

            assertCollectionEquals(values, objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());


            objectCrud.delete(connection, keys);

            assertCollectionEquals(Collections.<DbObject>emptyList(), objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());

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

    @Table(name = "TEST_DB_OBJECT")
    public static class DbObjectTable extends DbObject {
    }

    public static class TestDbObject extends DbObject {
    }

    private <T extends DbObject> void checkCrudDbObject(Connection connection, Crud<T, Long> objectCrud, T object) throws SQLException {
        assertNull(objectCrud.read(connection, object.getId()));

        // create
        Long key =
                objectCrud.create(connection, object, new RowHandler<Long>() {
                    Long key;
                    @Override
                    public void handle(Long aLong) throws Exception {
                        key = aLong;
                    }
                }).key;

        assertNull(key);


        key = object.getId();
        // read
        assertEquals(object, objectCrud.read(connection, key));

        object.setName("Udpdated");

        // update
        objectCrud.update(connection, object);
        assertEquals(object, objectCrud.read(connection, key));

        // delete
        objectCrud.delete(connection, key);
        assertNull(objectCrud.read(connection, key));

        objectCrud.create(connection, object);
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
                    objectCrud.create(connection, object, new RowHandler<CKEY>() {
                        CKEY key;
                        @Override
                        public void handle(CKEY aLong) throws Exception {
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

            assertCollectionEquals(values, objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());


            int i = 333;
            for(DbObject value : values) {
                value.setEmail(Integer.toHexString(i));
                i++;
            }

            objectCrud.update(connection, values);

            assertCollectionEquals(values, objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());


            objectCrud.delete(connection, keys);

            assertCollectionEquals(Collections.<DbObject>emptyList(), objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());
        } finally {
            connection.close();
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