package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.map.CaseInsensitiveEndsWithPredicate;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.Predicate;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Issue661_2_discriminator_same_classTest {

    @Test
    public void test2AssFieldWithSameTypeDiscriminatorNoAsmDSL() throws Exception {
        JdbcMapper<Foo> mapper =
                JdbcMapperFactoryHelper.noAsm()
                        .addKeys("id", "pFirst_id", "pSecond_id")
                        .discriminator(Parent.class)
                        .onColumn(CaseInsensitiveEndsWithPredicate.of("class_id"), Integer.class)
                        .with(new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Parent>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Parent> builder) {
                                        builder
                                                .when(1, Parent.class)
                                                .when(2, ChildA.class)
                                                .when(3, ChildB.class);
                                    }
                                }
                        )
                        .newMapper(Foo.class);


        validateMapper(mapper);

    }
    @Test
    public void test2AssFieldWithSameTypeDiscriminatorNoAsm() throws Exception {
        JdbcMapper<Foo> mapper =
                JdbcMapperFactoryHelper.noAsm()
                        .addKeys("id", "pFirst_id", "pSecond_id")
                        .discriminator(Parent.class)
                        .onColumn(CaseInsensitiveEndsWithPredicate.of("class_id"), Integer.class)
                        .with(new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Parent>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Parent> builder) {
                                        builder
                                                .when(1, Parent.class)
                                                .when(2, ChildA.class)
                                                .when(3, ChildB.class);
                                    }
                                }
                        )
                        .newMapper(Foo.class);


        validateMapper(mapper);

    }


    @Test
    public void test2AddFieldWithSameTypeWithDifferentColumnAndDifferentTypeBuilder2() throws Exception {
        JdbcMapperBuilder<Foo> builder =
                JdbcMapperFactoryHelper.noAsm()
                        .addKeys("id", "pFirst_id", "pSecond_id")
                        .discriminator(Parent.class).onColumn("pFirst_class_id", Integer.class).with(
                        new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Parent>>() {
                            @Override
                            public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Parent> builder) {
                                builder
                                        .when(1, Parent.class)
                                        .when(2, ChildA.class)
                                        .when(3, ChildB.class);
                            }
                        }
                )
                        .discriminator(Parent.class).onColumn( "pSecond_class_id",
                        Integer.class).with(new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Parent>>() {
                                                @Override
                                                public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Parent> builder) {
                                                    builder
                                                            .when(1, Parent.class)
                                                            .when(2, ChildC.class)
                                                            .when(3, ChildD.class);
                                                }
                                            }
                )
                        .newBuilder(Foo.class);

        final String[] columns = new String[] { "id", "pFirst_id", "pFirst_class_id", "pFirst_a_string", "pFirst_b_string", "pSecond_id", "pSecond_class_id", "pSecond_a_string", "pSecond_b_string",};

        for(String col : columns) {
            builder.addMapping(col);
        }
//        builder.addMapping("id");
//        builder.addMapping("pFirst_class_id");

        JdbcMapper<Foo> mapper = builder.mapper();

    }
    @Test
    public void test2AssFieldWithSameTypeWithDifferentColumnAndDifferentType() throws Exception {
        JdbcMapper<Foo> mapper =
                JdbcMapperFactoryHelper.noAsm()
                        .addKeys("id", "pFirst_id", "pSecond_id")
                        .discriminator(Parent.class, "pFirst_class_id",
                                Integer.class, new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Object>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Object> builder) {
                                        builder
                                                .when(1, Parent.class)
                                                .when(2, ChildA.class)
                                                .when(3, ChildB.class);
                                    }
                                }
                        )
                        .discriminator(Parent.class, "pSecond_class_id",
                                Integer.class, new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Object>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Object> builder) {
                                        builder
                                                .when(1, Parent.class)
                                                .when(2, ChildC.class)
                                                .when(3, ChildD.class);
                                    }
                                }
                        )
                        .newMapper(Foo.class);


        validateMapper2(mapper);

        /*
                final String[] columns = new String[]
                { "id", "pFirst_id", "pFirst_class_id", "pFirst_a_string", "pFirst_b_string", "pSecond_id", "pSecond_class_id", "pSecond_a_string", "pSecond_b_string",};
                {  1,    1,           2, ChildA           "aString",         null,              2,             3, ChildD           null,              "bString"},
                {  2,    1,           2, ChildA           "aString",         null,              3,             1, Parent           null,               null}
                assertTrue(is.get(0).pFirst instanceof ChildC);
        assertTrue(is.get(0).pSecond instanceof ChildD);

        assertTrue(is.get(1).pFirst instanceof ChildC);
        assertTrue(is.get(1).pSecond instanceof Parent);
         */

    }

    private void validateMapper(JdbcMapper<Foo> mapper) throws Exception {
        List<Foo> is = mapper.forEach(setUpResultSetMock(), new ListCollector<Foo>()).getList();
        assertTrue(is.get(0).pFirst instanceof ChildA);
        assertEquals(1, is.get(0).pFirst.id);
        assertEquals("ab", ((ChildA)is.get(0).pFirst).aString);

        assertTrue(is.get(0).pSecond instanceof ChildB);
        assertEquals(2, is.get(0).pSecond.id);
        assertEquals("kl", ((ChildB)is.get(0).pSecond).bString);

        assertTrue(is.get(1).pFirst instanceof ChildA);
        assertEquals(1, is.get(1).pFirst.id);
        assertEquals("ef", ((ChildA)is.get(1).pFirst).aString);

        assertTrue(is.get(1).pSecond instanceof Parent);
        assertEquals(3, is.get(1).pSecond.id);
    }

    private void validateMapper2(JdbcMapper<Foo> mapper) throws Exception {
        List<Foo> is = mapper.forEach(setUpResultSetMock(), new ListCollector<Foo>()).getList();
        assertTrue(is.get(0).pFirst instanceof ChildA);
        assertEquals(1, is.get(0).pFirst.id);
        assertEquals("ab", ((ChildA)is.get(0).pFirst).aString);


        assertTrue(is.get(0).pSecond instanceof ChildD);
        assertEquals(2, is.get(0).pSecond.id);
        assertEquals("kl", ((ChildD)is.get(0).pSecond).bString);

        assertTrue(is.get(1).pFirst instanceof ChildA);
        assertEquals(1, is.get(1).pFirst.id);
        assertEquals("ef", ((ChildA)is.get(1).pFirst).aString);

        assertTrue(is.get(1).pSecond instanceof Parent);
        assertEquals(3, is.get(1).pSecond.id);
    }

    private ResultSet setUpResultSetMock() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);


        final String[] columns = new String[] { "id", "pFirst_id", "pFirst_class_id", "pFirst_a_string", "pFirst_b_string", "pSecond_id", "pSecond_class_id", "pSecond_a_string", "pSecond_b_string",};

        when(metaData.getColumnCount()).thenReturn(columns.length);
        when(metaData.getColumnLabel(anyInt())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return columns[-1 + (Integer)invocationOnMock.getArguments()[0]];
            }
        });

        when(rs.getMetaData()).thenReturn(metaData);

        final AtomicInteger ai = new AtomicInteger();

        final Object[][] rows = new Object[][]{
                {1, 1, 2, "ab", "cd", 2, 3, "ij", "kl"},
                {2, 1, 2, "ef", "gh", 3, 1, "mn", "op"}
        };

        when(rs.next()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ai.getAndIncrement() < rows.length;
            }
        });
        final Answer<Object> getValue = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] row = rows[ai.get() - 1];
                final Integer col = -1 + (Integer) invocationOnMock.getArguments()[0];
                return (row[col]);
            }
        };

        final Answer<Object> getColumnValue = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] row = rows[ai.get() - 1];
                final String col = (String) invocationOnMock.getArguments()[0];
                return (row[Arrays.asList(columns).indexOf(col)]);
            }
        };

        when(rs.getInt(anyInt())).then(getValue);
        when(rs.getString(anyInt())).then(getValue);
        when(rs.getString(any(String.class))).then(getColumnValue);
        when(rs.getObject(anyInt())).then(getValue);
        when(rs.getObject(anyString(), any(Class.class))).then(getColumnValue);

        return rs;
    }

    public class Parent {
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public class ChildA extends Parent {
        String aString;

        public String getaString() {
            return aString;
        }

        public void setaString(String aString) {
            this.aString = aString;
        }

    }

    public class ChildB extends Parent {
        String bString;

        public String getbString() {
            return bString;
        }

        public void setbString(String bString) {
            this.bString = bString;
        }
    }

    public class ChildC extends Parent {
        String aString;

        public String getaString() {
            return aString;
        }

        public void setaString(String aString) {
            this.aString = aString;
        }

    }

    public class ChildD extends Parent {
        String bString;

        public String getbString() {
            return bString;
        }

        public void setbString(String bString) {
            this.bString = bString;
        }
    }

    public class Foo {
        int id;
        Parent pFirst;
        Parent pSecond;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public Parent getpFirst() {
            return pFirst;
        }
        public void setpFirst(Parent pFirst) {
            this.pFirst = pFirst;
        }
        public Parent getpSecond() {
            return pSecond;
        }
        public void setpSecond(Parent pSecond) {
            this.pSecond = pSecond;
        }

    }

}
