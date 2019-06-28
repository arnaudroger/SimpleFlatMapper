package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.CheckedBiFunction;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Issue614Test {



    @Test
    public void test() throws SQLException {

        JdbcMapper<B> mapper = JdbcMapperFactory
                .newInstance()
                .discriminator(A.class,
                        "a_type",
                        new CheckedBiFunction<ResultSet, String, String>() {
                            @Override
                            public String apply(ResultSet resultSet, String columnLabel) throws Exception {
                                return resultSet.getString(columnLabel);
                            }
                        },
                        new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, A>>() {
                            @Override
                            public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, A> builder) {
                                builder.when("A1", A1.class)
                                        .when("A2", A2.class)
                                        .when(new Predicate<String>() {
                                            @Override
                                            public boolean test(String v) {
                                                return true;
                                            }
                                        }, A.class);
                            }
                        }
                )
                .addCustomGetter(
                        "a_json",
                        new Getter<ResultSet, Object>() {
                            @Override
                            public Object get(ResultSet rs) throws Exception {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("s", "t");
                                return map;
                            }
                        })
                .newMapper(B.class);

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("select 1 as id, '{\"a\": \"a\"}'::json as a_json, 'A1' as a_type");

            // use to fail
            mapper.forEach(rs, new CheckedConsumer<B>() {
                @Override
                public void accept(B x) throws Exception {
                    System.out.println(x);
                }
            });
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
