package org.sfm.jdbc;

import com.mysql.jdbc.PacketTooBigException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.DefaultCrud;
import org.sfm.test.jdbc.DbHelper;
import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MysqlCrudTest {

    @Test
    public void testBatchInsert() throws SQLException {
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
    public void testBatchUpsert() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);

        try {
            Crud<DbObject, Long> objectCrud =
                    JdbcMapperFactory.newInstance().<DbObject, Long>crud(DbObject.class, Long.class).table(connection, "TEST_DB_OBJECT");

        } finally {
            connection.close();
        }
    }

    @Test
    public void testUpsert() throws SQLException {
        Connection connection = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);

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

            when(preparedStatementFail.executeUpdate()).thenThrow(new PacketTooBigException(3, 3));

            objectCrud.create(mockConnection, values);

            verify(preparedStatementFail, times(2)).executeUpdate();
            verify(preparedStatementSucceed).executeUpdate();

        } finally {
            connection.close();
        }
    }


}