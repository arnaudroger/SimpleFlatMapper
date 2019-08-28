package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Issue671Test {
    @Test
    public void failing() throws SQLException {
        JdbcMapperBuilder<Root> builder = JdbcMapperFactory.newInstance().newBuilder(Root.class);
        builder.addMapping("source_source_source_foo_bars$1231511225$");
    }

    @SuppressWarnings("unused")
    public static class Root {
        private String id;
        private Foo foo;
        private Root source;

        public Root() {}

        public Root(final String id) {
            this.id = id;
        }
        public Root(final String id, final Foo foo, final Root source) {
            this.id = id;
            this.foo = foo;
            this.source = source;
        }
    }

    @SuppressWarnings("unused")
    public static class Foo {
        private String id;
        private Set<Bar> bars;

        public Foo() {}
        public Foo(final String id) {
            this.id = id;
        }
        public Foo(final String id, final Set<Bar> bars) {
            this.id = id;
            this.bars = bars;
        }
    }

    @SuppressWarnings("unused")
    public static class Bar {
        private String id;

        public Bar() {}
        public Bar(final String id) {
            this.id = id;
        }
    }
}
