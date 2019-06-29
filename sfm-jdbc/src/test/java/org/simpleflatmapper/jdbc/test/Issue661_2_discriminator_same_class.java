package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Issue661_2_discriminator_same_class {

    @Test
    public void test2AssFieldWithSameTypeDiscriminatorNoAsmDSL() throws Exception {
        JdbcMapper<Foo> mapper =
                JdbcMapperFactoryHelper.noAsm()
                        .addKeys("id", "pFirst_id", "pSecond_id")
                        .discriminator(Parent.class)
                        .onColumn(CaseInsensitiveEndsWithPredicate.of("class_id"), Integer.class)
                        .with(new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Object>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, Integer, Object> builder) {
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
                        .discriminatorOn(Parent.class, CaseInsensitiveEndsWithPredicate.of("class_id"),
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
                        .newMapper(Foo.class);


        validateMapper(mapper);

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

    }

    private void validateMapper(JdbcMapper<Foo> mapper) throws Exception {
        List<Foo> is = mapper.forEach(setUpResultSetMock(), new ListCollector<Foo>()).getList();
        assertTrue(is.get(0).pFirst instanceof ChildA);
        assertTrue(is.get(0).pSecond instanceof ChildB);

        assertTrue(is.get(1).pFirst instanceof ChildA);
        assertTrue(is.get(1).pSecond instanceof Parent);
    }

    private void validateMapper2(JdbcMapper<Foo> mapper) throws Exception {
        List<Foo> is = mapper.forEach(setUpResultSetMock(), new ListCollector<Foo>()).getList();
        assertTrue(is.get(0).pFirst instanceof ChildC);
        assertTrue(is.get(0).pSecond instanceof ChildD);

        assertTrue(is.get(1).pFirst instanceof ChildC);
        assertTrue(is.get(1).pSecond instanceof Parent);
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
                {1, 1, 2, "aString", null, 2, 3, null, "bString"},
                {2, 1, 2, "aString", null, 3, 1, null, null}
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
