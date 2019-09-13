package org.simpleflatmapper.jdbc.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.*;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class Issue668ListIndexTest {


    @Test
    public void failing() throws SQLException {
        final String query =
                "with t (foos_bar1_id, foos_bar2_id ) as (values('c', 'd')) select * from t";

        Connection conn = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
	if (conn == null) return;
        try {

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            final JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance()
                    .useAsm(false);

            JdbcMapperBuilder<Root> builder = mapperFactory.newBuilder(Root.class);

            builder.addMapping("foos_bar1_id");
            builder.addMapping("foos_bar2_id");

            JdbcMapper<Root> mapper = builder.mapper();
            final Iterator<Root> iterator = mapper.iterator(rs);

            assertTrue(iterator.hasNext());
            final Root found = iterator.next();
            assertNotNull(found);
            assertEquals(1, found.foos.size());
            assertEquals("c", found.foos.get(0).bar1.id);
            assertEquals("d", found.foos.get(0).bar2.id);
        } finally {
            conn.close();
        }
    }

    public static class Root {
        private String id;
        private String name;
        private List<Foo> foos;

        public Root() {
        }

        public Root(final String id, final String name, final List<Foo> foos) {
            this.id = id;
            this.name = name;
            this.foos = foos;
        }
    }

    public static class Foo {
        private String id;
        private Bar bar1;
        private Bar bar2;

        public Foo() {
        }

        public Foo(final String id, final Bar bar1, final Bar bar2) {
            this.id = id;
            this.bar1 = bar1;
            this.bar2 = bar2;
        }
    }

    public static class Bar {
        private String id;

        public Bar() {
        }

        public Bar(final String id) {
            this.id = id;
        }
    }
}
