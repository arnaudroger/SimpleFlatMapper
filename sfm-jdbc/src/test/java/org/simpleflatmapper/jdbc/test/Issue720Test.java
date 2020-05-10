package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Issue720Test {

    @Test
    public void test() throws SQLException {
        JdbcMapperBuilder<Foo> builder = JdbcMapperFactory.newInstance().newBuilder(Foo.class);


        builder.addMapping("id");
        builder.addMapping("name");
        builder.addMapping("bar_id");
        builder.addMapping("bar_id");
        builder.addMapping("bar_name");
        builder.mapper();

        JdbcMapper<Foo> mapper = builder.mapper();

        ResultSet rs = mock(ResultSet.class);

        when(rs.getInt(1)).thenReturn(1);
        when(rs.getString(2)).thenReturn("foo");
        when(rs.getInt(3)).thenReturn(2);
        when(rs.getInt(4)).thenReturn(3);
        when(rs.getString(5)).thenReturn("bar");


        Foo foo = mapper.map(rs);

        assertEquals(1, foo.id);
        assertEquals(2, foo.barId);
        assertEquals(3, foo.bar.id);

        assertEquals("foo", foo.name);
        assertEquals("bar", foo.bar.name);


    }



    public static class Foo {
        public final int id;
        public final String name;
        public final int barId;
        public final Bar bar;


        public Foo(int id, String name, int barId, Bar bar) {
            this.id = id;
            this.name = name;
            this.bar = bar;
            this.barId = barId;
        }


    }

    public static final class Bar {
        public final int id;
        public final String name;

        public Bar(int id, String name) {
            this.id = id;
            this.name=name;
        }
    }

}
