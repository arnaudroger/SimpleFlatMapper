package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MysqlMultiRowsBatchInsertCrudTest {

    @Test
    public void testBatchInsertCreateOneQuery() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
        if (connection == null) { System.err.println("Db MySQL not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            Connection mockConnection = mock(Connection.class);

            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            ArgumentCaptor<String> queryCapture = ArgumentCaptor.forClass(String.class);

            when(mockConnection.prepareStatement(queryCapture.capture())).thenReturn(preparedStatement);

            objectCrud.create(mockConnection, Arrays.asList(DbObject.newInstance(), DbObject.newInstance()));


            assertEquals("INSERT INTO test_db_object(id, name, email, creation_Time, type_ordinal, type_name) VALUES(?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?)".toLowerCase(),
                    queryCapture.getValue().toLowerCase());
            verify(preparedStatement, never()).addBatch();
            verify(preparedStatement, never()).executeBatch();
            verify(preparedStatement).executeUpdate();
        } finally {
            connection.close();
        }
    }
    @Test
    public void testBatchUpsertCreateOneQuery() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
        if (connection == null) { System.err.println("Db MySQL not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

            Connection mockConnection = mock(Connection.class);

            PreparedStatement preparedStatement = mock(PreparedStatement.class);

            ArgumentCaptor<String> queryCapture = ArgumentCaptor.forClass(String.class);

            when(mockConnection.prepareStatement(queryCapture.capture())).thenReturn(preparedStatement);

            objectCrud.createOrUpdate(mockConnection, Arrays.asList(DbObject.newInstance(), DbObject.newInstance()));


            assertEquals(("INSERT INTO test_db_object(id, name, email, creation_Time, type_ordinal, type_name) " +
                            "VALUES(?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE name = VALUES(name), " +
                    "email = VALUES(email)," +
                    " creation_Time = VALUES(creation_Time), " +
                    "type_ordinal = VALUES(type_ordinal), " +
                    "type_name = VALUES(type_name)").toLowerCase(),
                    queryCapture.getValue().toLowerCase());

            verify(preparedStatement, never()).addBatch();
            verify(preparedStatement, never()).executeBatch();
            verify(preparedStatement).executeUpdate();
        } finally {
            connection.close();
        }
    }

    @Test
    public void testBatchUpsertOnDb() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
        if (connection == null) { System.err.println("Db MySQL not available"); return; }
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
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
        if (connection == null) { System.err.println("Db MySQL not available"); return; }
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

    @Test
    public void testUpsertAutoIncUniqueIndexOnIndex() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
        if (connection == null) { System.err.println("Db MySQL not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT_AUTOINC_NAMEINDEX");

            final DbObject object = DbObject.newInstance();

            Long key = objectCrud.createOrUpdate(connection, object, new KeyCapture<Long>()).getKey();

            assertNotNull(key);

            object.setId(key);

            assertEquals(object, objectCrud.read(connection, object.getId()));

            object.setEmail("Updated Email " + key);

            key = objectCrud.createOrUpdate(connection, object, new KeyCapture<Long>()).getKey();

            System.out.println("key = " + key + " current id " + object.getId());

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT count(*) FROM TEST_DB_OBJECT_AUTOINC_NAMEINDEX WHERE email = '" + object.getEmail() + "'");
            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
            assertEquals(object, objectCrud.read(connection, object.getId()));

        } finally {
            connection.close();
        }
    }

    @Test
    public void testBatchInsertSizeReducer() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
        if (connection == null) { System.err.println("Db MySQL not available"); return; }
        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");



            Connection mockConnection = mock(Connection.class);

            PreparedStatement preparedStatementFail = mock(PreparedStatement.class);
            PreparedStatement preparedStatementSucceed = mock(PreparedStatement.class);

            List<DbObject> values = new ArrayList<DbObject>();
            for(int i = 0; i < 101; i++) {
                values.add(DbObject.newInstance());
            }

            when(mockConnection.prepareStatement(anyString()))
                    .thenReturn(preparedStatementFail, preparedStatementFail, preparedStatementSucceed);

            when(preparedStatementFail.executeUpdate()).thenThrow(MysqlCrudTest.getPacketTooBigException());

            objectCrud.create(mockConnection, values);

            verify(preparedStatementFail, times(2)).executeUpdate();
            verify(preparedStatementSucceed, times(5)).executeUpdate();

        } finally {
            connection.close();
        }
    }


}