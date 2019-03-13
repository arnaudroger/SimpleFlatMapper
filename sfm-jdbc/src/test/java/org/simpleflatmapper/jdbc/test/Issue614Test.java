package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.postgresql.util.PGobject;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.TypeReference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Issue614Test {



    @Test
    public void test() throws SQLException {

        JdbcMapper<B> mapper = JdbcMapperFactory
                .newInstance()
                .discriminator(A.class,
                        "a_type",
                        ResultSet::getString,
                        builder -> builder.when("A1", A1.class)
                                .when("A2", A2.class)
                                .when(v -> true, A.class)
                )
                .addCustomGetter(
                        "a_json",
                        rs -> {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("s", "t");
                            return map;
                        })
                .newMapper(B.class);

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try (
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("select 1 as id, '{\"a\": \"a\"}'::json as a_json, 'A1' as a_type"))
        {

            // use to fail
            mapper.forEach(rs, System.out::println);
        } finally {
            c.close();
        }

    }

    public class A
    {
        public HashMap<String, String> json;
        public String type;

        @Override
        public String toString() {
            return "A{" +
                    "json=" + json +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public class A1 extends A
    {
        @Override
        public String toString() {
            return "A1{" +
                    "json=" + json +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
    public class A2 extends A
    {
        @Override
        public String toString() {
            return "A2{" +
                    "json=" + json +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public class B
    {
        public Integer id;
        public A a;

        @Override
        public String toString() {
            return "B{" +
                    "id=" + id +
                    ", a=" + a +
                    '}';
        }
    }

}
