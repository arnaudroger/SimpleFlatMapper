package org.sfm.jdbc;

import com.mysql.jdbc.PacketTooBigException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.DefaultCrud;
import org.sfm.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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