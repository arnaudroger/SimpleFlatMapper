package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.PreparedStatementMapperDelegate;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedStatementMapperTest {

    JdbcMapperFactory jdbcMapperFactory = JdbcMapperFactory
            .newInstance()
            .addColumnProperty("type_ordinal", SqlTypeColumnProperty.of(Types.NUMERIC));
    @Test
    public void testInsertAndSelectWithPreparedStatementMapper() throws SQLException {
        NamedSqlQuery insertQuery = NamedSqlQuery.parse("INSERT INTO test_db_object(id, name, email, creation_time, type_ordinal, type_name) values(?, ?, ?, ?, ?, ?) ");
        NamedSqlQuery selectQuery = NamedSqlQuery.parse("select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = ? ");

        PreparedStatementMapper<DbObject> insertPreparedStatementMapper =
                jdbcMapperFactory
                        .from(DbObject.class).to(insertQuery);

        PreparedStatementMapper<DbObject> selectPreparedStatementMapper =
                jdbcMapperFactory.from(DbObject.class).to(selectQuery);


        DbObject dbObject = DbObject.newInstance();

        Connection connection = DbHelper.objectDb();
        try {
            PreparedStatement ps = insertPreparedStatementMapper.prepareAndBind(connection, dbObject);
            try {
                ps.execute();
            } finally {
                ps.close();
            }

            ps = selectPreparedStatementMapper.prepareAndBind(connection, dbObject);
            try {
                ps.setLong(1, dbObject.getId());

                ResultSet resultSet = ps.executeQuery();

                assertTrue(resultSet.next());
                assertEquals(dbObject, JdbcMapperFactory.newInstance().newMapper(DbObject.class).map(resultSet));
            } finally {
                ps.close();
            }
        } finally {
            connection.close();
        }
    }


    @Test
    public void testSelectWithInList() throws SQLException {
        NamedSqlQuery selectInListQuery = NamedSqlQuery.parse("select * from Table where name in (?) and id = ? ");
        PreparedStatementMapper<QueryParamList> selectInListMapper = jdbcMapperFactory.from(QueryParamList.class).to(selectInListQuery);
        Connection conn = mock(Connection.class);
        PreparedStatement mps = mock(PreparedStatement.class);

        QueryParamList value = new QueryParamList();
        value.name = Arrays.asList("name1", "name2");
        value.id = 3334;

        when(conn.prepareStatement("select * from Table where name in (?, ?) and id = ? ")).thenReturn(mps);

        PreparedStatement ps = selectInListMapper.prepareAndBind(conn, value);

        assertSame(mps, ps);
        verify(mps).setString(1, "name1");
        verify(mps).setString(2, "name2");
        verify(mps).setInt(3, 3334);

    }


    public static class QueryParamArray {
        private String name;
        private int[] ids;
    }

    public static class QueryParamList {
        public List<String> name;
        public int id;
    }
}