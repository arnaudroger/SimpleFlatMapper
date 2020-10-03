package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.ListCollector;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class Issue707Test {
    @Test
    public void test() throws Exception {
        JdbcMapperBuilder<M> builder = JdbcMapperFactory
                .newInstance()
                .addKeys("id", "v_id", "t_id", "s_id")
                .discriminator(M.class)
                .onColumn("type", String.class)
                .with(b -> b.when("type1", M2.class)
                        .when(x -> true, M.class))
                .ignorePropertyNotFound()
                .newBuilder(M.class);


        builder.addMapping("id");
        builder.addMapping("v_id");
        builder.addMapping("t_id");
        builder.addMapping("s_id");
        builder.addMapping("type");

        JdbcMapper<M> mapper = builder.mapper();


        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;


        String query = "SELECT * FROM (\n" +
                " select 9675 as id, 162 as v_id, 564681 as t_id, 9671 as s_id, 'type1' as type\n" +
                " UNION\n" +
                " select 9675 as id, 162 as v_id, 564681 as t_id, 9672 as s_id, 'type1' as type\n" +
                ") as foo";

        try (Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(query)) {
            List<M> list = mapper.forEach(rs, new ListCollector<>()).getList();
            assertEquals(1, list.size());
        } finally {
            c.close();
        }
    }

    public class M {
        public Integer id;
        public String type;
        public List<V> v;
        public List<T> t;

        @Override
        public String toString() {
            return "M{" +
                    "id=" + id +
                    ", v=" + v +
                    ", t=" + t +
                    '}';
        }

        public M() {
        }
    }

    public class M2 extends M {
        public List<S> s;

        @Override
        public String toString() {
            return "M2{" +
                    "\n\tid=" + id +
                    "\n\t, type='" + type + '\'' +
                    "\n\t, v=" + v +
                    "\n\t, t=" + t +
                    "\n\t, s=" + s +
                    "\n";
        }

        public M2() {
        }

    }

    public class V {
        public Integer id;

        public V() {
        }

        @Override
        public String toString() {
            return "V{" +
                    "id=" + id +
                    '}';
        }
    }

    public class S {
        public Integer id;

        public S() {
        }

        @Override
        public String toString() {
            return "S{" +
                    "id=" + id +
                    '}';
        }
    }

    public class T {
        public Integer id;

        public T() {
        }

        @Override
        public String toString() {
            return "T{" +
                    "id=" + id +
                    '}';
        }
    }
}
