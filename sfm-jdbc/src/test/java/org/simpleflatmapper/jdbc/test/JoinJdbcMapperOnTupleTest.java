package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ListCollector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinJdbcMapperOnTupleTest {

    private JdbcMapperFactory asmJdbcMapperFactory = JdbcMapperFactoryHelper.asm().addKeys("0_id", "1_id");
    private JdbcMapperFactory noAsmJdbcMapperFactory = JdbcMapperFactoryHelper.noAsm().addKeys("0_id", "1_id");

    @Test
    public void testTupleJoin() throws Exception {
        validateBuilder(asmJdbcMapperFactory.newBuilder(new TypeReference<Tuple2<Person, List<Person>>>() {
        }));
    }

    @Test
    public void testTupleJoinNoAsm() throws Exception {
        validateBuilder(noAsmJdbcMapperFactory.newBuilder(new TypeReference<Tuple2<Person, List<Person>>>() {
        }));
    }


    @Test
    public void testTupleJoinInverted() throws Exception {
        validateBuilderInverted(asmJdbcMapperFactory.newBuilder(new TypeReference<Tuple2<List<Person>, Person>>() {
        }));
    }

    @Test
    public void testTupleJoinNoAsmInverted() throws Exception {
        validateBuilderInverted(noAsmJdbcMapperFactory.newBuilder(new TypeReference<Tuple2<List<Person>, Person>>() {
        }));
    }

    public static ResultSet setUpResultSetMock(final String[] columns, final Object[][] rows) throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);



        when(metaData.getColumnCount()).thenReturn(columns.length);
        when(metaData.getColumnLabel(anyInt())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return columns[-1 + (Integer)invocationOnMock.getArguments()[0]];
            }
        });

        when(rs.getMetaData()).thenReturn(metaData);

        final AtomicInteger ai = new AtomicInteger();


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

        when(rs.getInt(anyInt())).then(getValue);
        when(rs.getString(anyInt())).then(getValue);
        when(rs.getObject(anyInt())).then(getValue);

        return rs;
    }

    public static void validateProfessors(List<Tuple2<Person, List<Person>>> professors) {
        assertEquals("we get 3 professors from the resultset", 3, professors.size());
        Person professor = professors.get(0).getElement0();
        List<Person> students = professors.get(0).getElement1();

        assertPersonEquals(1, "professor1", professor);
        assertEquals("has 2 students", 2, students.size());
        assertPersonEquals(3, "student3", students.get(0));
        assertPersonEquals(4, "student4", students.get(1));


        professor = professors.get(1).first();
        students = professors.get(1).second();

        assertPersonEquals(2, "professor2", professor);
        assertEquals("has 1 student", 1, students.size());
        assertPersonEquals(4, "student4", students.get(0));

        professor = professors.get(2).first();
        students = professors.get(2).second();
        assertPersonEquals(3, "professor3", professor);
        assertTrue("professor3 has no students", students.isEmpty());

    }

    private void validateProfessorsInverted(List<Tuple2<List<Person>, Person>> professors) {
        assertEquals("we get 3 professors from the resultset", 3, professors.size());
        Person professor = professors.get(0).getElement1();
        List<Person> students = professors.get(0).getElement0();

        assertPersonEquals(1, "professor1", professor);
        assertEquals("has 2 students", 2, students.size());
        assertPersonEquals(3, "student3", students.get(0));
        assertPersonEquals(4, "student4", students.get(1));


        professor = professors.get(1).getElement1();
        students = professors.get(1).getElement0();

        assertPersonEquals(2, "professor2", professor);
        assertEquals("has 1 student", 1, students.size());
        assertPersonEquals(4, "student4", students.get(0));

        professor = professors.get(2).getElement1();
        students = professors.get(2).getElement0();
        assertPersonEquals(3, "professor3", professor);
        assertTrue("professor3 has no students", students.isEmpty());

    }

    public static void assertPersonEquals(int id, String name, Person person) {
        assertEquals(id, person.getId());
        assertEquals(name, person.getName());
    }


    final String[] columns = new String[] { "0_id", "0_name", "1_id", "1_name" };
    final Object[][] rows = new Object[][]{
            {1, "professor1", 3, "student3"},
            {1, "professor1", 4, "student4"},
            {2, "professor2", 4, "student4"},
            {3, "professor3", null, null}
    };
    private void validateBuilder(JdbcMapperBuilder<Tuple2<Person, List<Person>>> builder) throws Exception {
        for(int i = 0; i < columns.length; i++) {
            builder.addMapping(columns[i]);
        }

        final JdbcMapper<Tuple2<Person, List<Person>>> mapper = builder.mapper();
        List<Tuple2<Person, List<Person>>> professors = mapper.forEach(setUpResultSetMock(columns, rows), new ListCollector<Tuple2<Person, List<Person>>>()).getList();
        validateProfessors(professors);
    }

    final Object[][] rowsInverted = new Object[][]{
            {3, "student3", 1, "professor1"},
            {4, "student4", 1, "professor1"},
            {4, "student4", 2, "professor2"},
            { null, null, 3, "professor3"}
    };
    private void validateBuilderInverted(JdbcMapperBuilder<Tuple2<List<Person>, Person>> builder) throws Exception {
        for(int i = 0; i < columns.length; i++) {
            builder.addMapping(columns[i]);
        }

        final JdbcMapper<Tuple2<List<Person>, Person>> mapper = builder.mapper();
        List<Tuple2<List<Person>, Person>> professors = mapper.forEach(setUpResultSetMock(columns, rowsInverted), new ListCollector<Tuple2<List<Person>, Person>>()).getList();
        validateProfessorsInverted(professors);
    }


    public static class Person {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
