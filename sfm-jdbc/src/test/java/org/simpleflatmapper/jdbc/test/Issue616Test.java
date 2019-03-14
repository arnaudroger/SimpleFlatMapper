package org.simpleflatmapper.jdbc.test;


import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Issue616Test {

    //IFJAVA8_START
    public static final int TZ = -101;

    @org.junit.Test
    public void test() throws SQLException {
        JdbcMapper<Test> mapper = JdbcMapperFactory.newInstance().useAsm(false).newBuilder(Test.class).addMapping(new JdbcColumnKey("zdt", 1, TZ, getClass().getName())).mapper();

        ResultSet rs = mock(ResultSet.class);

        Iterator<Test> iterator = mapper.iterator(rs);

        Timestamp t = new Timestamp(System.currentTimeMillis());
        when(rs.getTimestamp(1)).thenReturn(t);
        when(rs.getObject(1)).thenReturn(new Issue616Test());
        when(rs.next()).thenReturn(true);

        Test v = iterator.next();

        assertEquals(t.getTime(), v.zdt.toInstant().toEpochMilli());
    }

    public static class Test {
        public java.time.ZonedDateTime zdt;
    }
    //IFJAVA8_END
}
