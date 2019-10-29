package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Issue687ArrayMappingTest {

    @Test
    public void mapBigintArrayToLongArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObj> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObj.class);

            ResultSet rs = s.executeQuery("select 123 as id, array[1, 2]::bigint[] as ids");


            rs.next();

            MyObj map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new long[]{1l, 2l}, map.ids);

        } finally {
            c.close();
        }
    }

    public static class MyObj {
        public final long[] ids;
        public final long id;

        public MyObj(long[] ids, long id) {
            this.ids = ids;
            this.id = id;
        }
    }
}
