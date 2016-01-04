package org.sfm.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.sfm.beans.DbObject;
import org.sfm.test.jdbc.DbHelper;
import org.sfm.utils.ListCollectorHandler;
import org.sfm.utils.RowHandler;

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


    @Parameterized.Parameters
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

            assertEquals(values, objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());


            int i = 333;
            for(DbObject value : values) {
                value.setEmail(Integer.toHexString(i));
                i++;
            }

            objectCrud.update(connection, values);

            assertEquals(values, objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());


            objectCrud.delete(connection, keys);

            assertEquals(Collections.emptyList(), objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());

        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectCrud() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            DbObject object = DbObject.newInstance();


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

            objectCrud.create(connection, DbObject.newInstance());

        } finally {
            connection.close();
        }
    }

    public static class CKEY {
        public long id;
        public String name;

    }
    @Test
    public void testCompositeKey() throws SQLException {
        Connection connection = DbHelper.getDbConnection(targetDB);
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

            assertEquals(values, objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());


            int i = 333;
            for(DbObject value : values) {
                value.setEmail(Integer.toHexString(i));
                i++;
            }

            objectCrud.update(connection, values);

            assertEquals(values, objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());


            objectCrud.delete(connection, keys);

            assertEquals(Collections.emptyList(), objectCrud.read(connection, keys, new ListCollectorHandler<DbObject>()).getList());
        } finally {
            connection.close();
        }
    }

}