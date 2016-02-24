package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PostgresqlCrudTest {

    @Test
    public void testBatchUpsertOnDb() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (connection == null) { System.err.println("Db Postgresql not available"); return; }
        System.out.println("connection = " + connection.getMetaData().getDatabaseMajorVersion()  + "." + connection.getMetaData().getDatabaseMinorVersion());
        if (connection.getMetaData().getDatabaseMajorVersion() != 9 || connection.getMetaData().getDatabaseMinorVersion() < 5)
            { System.err.println("Db Postgresql 9.5 not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            DbObject object1 = DbObject.newInstance();
            DbObject object2 = DbObject.newInstance();

            objectCrud.create(connection, object1);

            object1.setName("BatchUpdate");
            object2.setName("BatchUpdate");

            objectCrud.createOrUpdate(connection, Arrays.<DbObject>asList(object1, object2));

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT count(*) FROM TEST_DB_OBJECT WHERE name = 'BatchUpdate'");
            assertTrue(resultSet.next());
            assertEquals(2, resultSet.getInt(1));

            assertEquals(object1, objectCrud.read(connection, object1.getId()));
            assertEquals(object2, objectCrud.read(connection, object2.getId()));

        } finally {
            connection.close();
        }
    }

    @Test
    public void testUpsert() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (connection == null) { System.err.println("Db MySQL not available"); return; }
        if (connection.getMetaData().getDatabaseMajorVersion() != 9 || connection.getMetaData().getDatabaseMinorVersion() < 5)
        { System.err.println("Db Postgresql 9.5 not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            final DbObject object = DbObject.newInstance();
            objectCrud.createOrUpdate(connection, object);

            assertEquals(object, objectCrud.read(connection, object.getId()));

            object.setEmail("Updated Email");

            objectCrud.createOrUpdate(connection, object);

            assertEquals(object, objectCrud.read(connection, object.getId()));

        } finally {
            connection.close();
        }
    }
}