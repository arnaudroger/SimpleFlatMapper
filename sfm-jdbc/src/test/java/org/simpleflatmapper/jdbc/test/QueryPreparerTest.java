package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.QueryBinder;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.SqlTypeColumnProperty;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QueryPreparerTest {

    JdbcMapperFactory jdbcMapperFactory = JdbcMapperFactory
            .newInstance()
            .addColumnProperty("type_ordinal", SqlTypeColumnProperty.of(Types.NUMERIC));
    @Test
    public void testInsertAndSelectWithPreparedStatementMapper() throws SQLException {
        NamedSqlQuery insertQuery = NamedSqlQuery.parse("INSERT INTO test_db_object(id, name, email, creation_time, type_ordinal, type_name) values(?, ?, ?, ?, ?, ?) ");
        NamedSqlQuery selectQuery = NamedSqlQuery.parse("select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = ? ");

        QueryPreparer<DbObject> insertQueryPreparer =
                jdbcMapperFactory
                        .from(DbObject.class).to(insertQuery);

        QueryPreparer<DbObject> selectQueryPreparer =
                jdbcMapperFactory.from(DbObject.class).to(selectQuery);


        DbObject dbObject = DbObject.newInstance();

        Connection connection = DbHelper.objectDb();
        try {
            PreparedStatement ps = insertQueryPreparer.prepare(connection).bind(dbObject);
            try {
                ps.execute();
            } finally {
                ps.close();
            }

            final QueryBinder<DbObject> queryBinder = selectQueryPreparer.prepare(connection);
            ps = selectQueryPreparer.prepareStatement(connection);
            try {
                queryBinder.bindTo(dbObject, ps);

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
        QueryPreparer<QueryParamList> selectInListMapper = jdbcMapperFactory.from(QueryParamList.class).to(selectInListQuery);
        Connection conn = mock(Connection.class);
        PreparedStatement mps = mock(PreparedStatement.class);

        QueryParamList value = new QueryParamList();
        value.name = Arrays.asList("name1", "name2");
        value.id = 3334;

        when(conn.prepareStatement("select * from Table where name in (?, ?) and id = ? ")).thenReturn(mps);

        PreparedStatement ps = selectInListMapper.prepare(conn).bind(value);

        assertSame(mps, ps);
        verify(mps).setString(1, "name1");
        verify(mps).setString(2, "name2");
        verify(mps).setInt(3, 3334);

    }

    @Test
    public void testSelectWithInArray() throws SQLException {
        NamedSqlQuery selectInListQuery = NamedSqlQuery.parse("select * from Table where name = ? and id in (?) ");
        QueryPreparer<QueryParamArray> selectInListMapper = jdbcMapperFactory.from(QueryParamArray.class).to(selectInListQuery);
        Connection conn = mock(Connection.class);
        PreparedStatement mps = mock(PreparedStatement.class);

        QueryParamArray value = new QueryParamArray();
        value.name = "nannme";
        value.id = new int[] { 3334, 3336 };

        when(conn.prepareStatement("select * from Table where name = ? and id in (?, ?) ")).thenReturn(mps);

        PreparedStatement ps = selectInListMapper.prepare(conn).bind(value);

        assertSame(mps, ps);
        verify(mps).setString(1, "nannme");
        verify(mps).setInt(2, 3334);
        verify(mps).setInt(3, 3336);

    }

    @Test
    public void testQueryBinderClosePsOnException() throws SQLException {
        NamedSqlQuery query = NamedSqlQuery.parse("select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = ? ");

        QueryPreparer<DbObject> queryPreparer =
                jdbcMapperFactory
                        .from(DbObject.class).to(query);

        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);

        doThrow(new IllegalStateException()).when(ps).setLong(anyInt(), anyLong());

        try {
            queryPreparer.prepare(conn).bind(DbObject.newInstance());
            fail();
        } catch(IllegalStateException e) {
            // expected
        }

        verify(ps).close();
    }

    public static class QueryParamArray {
        public String name;
        public int[] id;
    }

    public static class QueryParamList {
        public List<String> name;
        public int id;
    }
}