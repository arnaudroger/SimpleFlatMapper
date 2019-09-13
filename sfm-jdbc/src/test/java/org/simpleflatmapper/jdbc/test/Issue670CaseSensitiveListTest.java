package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.*;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class Issue670CaseSensitiveListTest {
    private static final String query =
            "with t (id, linkBList_id, linkBList_linkc_id, linkBList_linkC_id ) as (values('aa', 'bb', 'cc', 'cc')) select * from t";

    @Test
    public void minimalTest() throws Exception {
        final JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance()
                .useAsm(false)
                .ignorePropertyNotFound()
                .addKeys("linkBList_linkC_id");

        JdbcMapperBuilder<Root> builder = mapperFactory.newBuilder(Root.class);
        builder.addMapping("linkBList_linkc_id");
        builder.addMapping("linkBList_linkC_id");
        JdbcMapper<Root> mapper = builder.mapper();

        testMapper(mapper);
    }

    @Test
    public void failing() throws SQLException {
        final JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance()
                .useAsm(false)
                .ignorePropertyNotFound()
                .addKeys("id", "linkBList_id", "linkBList_linkC_id");

        JdbcMapper<Root> mapper = setUpMapper(mapperFactory);
        testMapper(mapper);
    }

    private JdbcMapper<Root> setUpMapper(JdbcMapperFactory mapperFactory) {
        JdbcMapperBuilder<Root> builder = mapperFactory.newBuilder(Root.class);
        builder.addMapping("id");
        builder.addMapping("linkBList_id");
        builder.addMapping("linkBList_linkc_id");
        builder.addMapping("linkBList_linkC_id");
        return builder.mapper();
    }


    @Test
    public void succeeding() throws SQLException {
        final JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance()
                .useAsm(false)
                .ignorePropertyNotFound()
                .addKeys("id", "linkBList_id", "linkBList_linkC_id")
                .ignoreColumns("linkBList_linkc_id");

        JdbcMapper<Root> mapper = setUpMapper(mapperFactory);

        testMapper(mapper);
    }

    private void testMapper(JdbcMapper<Root> mapper) throws SQLException {
      Connection conn = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
	if (conn == null) return;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            Iterator<Root> iterator = mapper.iterator(rs);
            assertTrue(iterator.hasNext());
            final Root found = iterator.next();
            assertNotNull(found);
        } finally {
          conn.close();
        }
    }

    public static class Root {
        private String id;
        private List<Foo> linkBList;

        public Root() {
        }

        public Root(final String id, final List<Foo> linkBList) {
            this.id = id;
            this.linkBList = linkBList;
        }
    }

    public static class Foo {
        private String id;
        private Bar linkC;

        public Foo() {
        }

        public Foo(final String id, final Bar linkC) {
            this.id = id;
            this.linkC = linkC;
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
