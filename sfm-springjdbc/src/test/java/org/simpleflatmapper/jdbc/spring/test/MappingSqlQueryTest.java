package org.simpleflatmapper.jdbc.spring.test;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.MappingSqlQuery;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MappingSqlQueryTest {

    private SingleConnectionDataSource dataSource;

    @Before
    public void setUp() throws SQLException {
        Connection dbConnection = DbHelper.getDbConnection(DbHelper.TargetDB.HSQLDB);
        dataSource = new SingleConnectionDataSource(dbConnection, true);
    }
    @Test
    public void testSelectDbObject() throws ParseException {
        MappingSqlQuery<DbObject> sqlQuery =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .mappingSqlQuery(
                                DbObject.class,
                                dataSource,
                                "select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id = :id ");

        DbHelper.assertDbObjectMapping(sqlQuery.findObject(1));
    }

    @Test
    public void testSelectDbObjects() throws ParseException {
        MappingSqlQuery<DbObject> sqlQuery =
                JdbcTemplateMapperFactory
                        .newInstance()
                        .mappingSqlQuery(
                                DbObject.class,
                                dataSource,
                                "select id, name, email, creation_time, type_ordinal, type_name from TEST_DB_OBJECT where id in (:id) ");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", Arrays.asList(1, -1));
        List<DbObject> list = sqlQuery.executeByNamedParam(params);

        assertEquals(1, list.size());
        DbHelper.assertDbObjectMapping(list.get(0));

    }

}