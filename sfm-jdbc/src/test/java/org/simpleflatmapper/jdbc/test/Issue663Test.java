package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.property.InferNullProperty;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.ListCollector;

import java.net.UnknownHostException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Issue663Test {



    @Test
    public void testIngerNullProperty() throws SQLException, UnknownHostException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            JdbcMapper<AResource> mapper = JdbcMapperFactory.newInstance()
                    .useAsm(false)
                    .ignorePropertyNotFound()
                    .addKeys("id")
                    .addColumnProperty("items_item", InferNullProperty.DEFAULT)
                    .newMapper(AResource.class);

            ResultSet resultSet = s.executeQuery("with t(id, items_item) as ( \n" +
                    "\tvalues" +
                    "('XX', 'foo'),\n" +
                    "('XX', 'bar'),\n" +
                    "('XX', 'foo'),\n" +
                    "('YY', null)\n" +
                    ")\n" +
                    "select * from t\n");

            List<AResource> collect = mapper.forEach(resultSet, new ListCollector<AResource>()).getList();

            assertEquals(Arrays.asList("foo", "bar", "foo" ), collect.get(0).items);
            assertEquals(Arrays.asList(), collect.get(1).items);
        } finally {
            c.close();
        }
    }


    public static class AResource {
        public String id;
        public List<String> items;
    }




}
