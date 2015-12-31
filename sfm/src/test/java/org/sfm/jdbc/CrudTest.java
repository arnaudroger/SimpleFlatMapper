package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class CrudTest {

    @Test
    public void testDbObjectCrud() throws SQLException {
        Connection connection = DbHelper.objectDb();
        try {
            Crud<DbObject, Long> objectCrud = JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            DbObject object = DbObject.newInstance();

            assertNull(objectCrud.read(connection, object.getId()));

            // create
            objectCrud.create(connection, object);

            // read
            assertEquals(object, objectCrud.read(connection, object.getId()));

            object.setName("Udpdated");

            // update
            objectCrud.update(connection, object);
            assertEquals(object, objectCrud.read(connection, object.getId()));

            // delete
            objectCrud.delete(connection, object.getId());
            assertNull(objectCrud.read(connection, object.getId()));


        } finally {
            connection.close();
        }
    }
}