package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.test.jdbc.DbHelper;
import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class CrudTest {

    @Test
    public void testDbObjectCrudAutoInc() throws SQLException {
        Connection connection = DbHelper.objectDb();
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


        } finally {
            connection.close();
        }
    }

    @Test
    public void testDbObjectCrud() throws SQLException {
        Connection connection = DbHelper.objectDb();
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


        } finally {
            connection.close();
        }
    }
}