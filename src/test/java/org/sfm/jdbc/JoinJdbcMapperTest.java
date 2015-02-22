package org.sfm.jdbc;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.utils.ListHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
//IFJAVA8_START
import java.util.stream.Collectors;
//IFJAVA8_END

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinJdbcMapperTest {

    public static class StudentField {
        public int id;
        public String name;
    }
    public static class ProfessorField {
        public int id;
        public String name;
        public List<StudentField> students;
    }

    public static class StudentGS {
        private int id;
        private String name;
        private List<String> surnames;

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

        public List<String> getSurnames() {
            return surnames;
        }

        public void setSurnames(List<String> surnames) {
            this.surnames = surnames;
        }
    }
    public static class ProfessorGS {
        private int id;
        private String name;
        private List<StudentGS> students;

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

        public List<StudentGS> getStudents() {
            return students;
        }

        public void setStudents(List<StudentGS> students) {
            this.students = students;
        }
    }


    public static class StudentC{
        private final int id;
        private final String name;

        public StudentC(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }
    public static class ProfessorC {
        private final int id;
        private final String name;
        private final List<StudentC> students;

        public ProfessorC(int id, String name, List<StudentC> students) {
            this.id = id;
            this.name = name;
            this.students = students;
        }


        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<StudentC> getStudents() {
            return students;
        }

    }


    @Test
    public void testJoinTableFields() throws SQLException {
        JdbcMapper<ProfessorField> mapper = JdbcMapperFactoryHelper.asm()
                .addKeys("id", "student_id")
                .newMapper(ProfessorField.class);


        ResultSet rs = setUpResultSetMock();


        List<ProfessorField> professors = mapper.forEach(rs, new ListHandler<ProfessorField>()).getList();
        assertEquals(2, professors.size());
        assertEquals(1, professors.get(0).id);
        assertEquals("professor1", professors.get(0).name);
        assertEquals(2, professors.get(0).students.size());
        assertEquals(3, professors.get(0).students.get(0).id);
        assertEquals("student3", professors.get(0).students.get(0).name);
        assertEquals(4, professors.get(0).students.get(1).id);
        assertEquals("student4", professors.get(0).students.get(1).name);


        assertEquals(2, professors.get(1).id);
        assertEquals("professor2", professors.get(1).name);
        assertEquals(1, professors.get(1).students.size());
        assertEquals(3, professors.get(1).students.get(0).id);
        assertEquals("student3", professors.get(1).students.get(0).name);


    }

    @Test
    public void testJoinTableGSNoAsm() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.noAsm()
                .addKeys("id", "student_id")
                .newMapper(ProfessorGS.class);

        List<ProfessorGS> professors = mapper.forEach(setUpResultSetMock(), new ListHandler<ProfessorGS>()).getList();
        validateGS(professors);

        //IFJAVA8_START
        validateGS(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        validateGS(mapper.stream(setUpResultSetMock()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorGS> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<ProfessorGS>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateGS(professors);
    }

    @Test
    public void testJoinTableGS() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.asm()
                .addKeys("id", "student_id")
                .newMapper(ProfessorGS.class);

        List<ProfessorGS> professors = mapper.forEach(setUpResultSetMock(), new ListHandler<ProfessorGS>()).getList();
        validateGS(professors);

        //IFJAVA8_START
        validateGS(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        validateGS(mapper.stream(setUpResultSetMock()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorGS> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<ProfessorGS>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateGS(professors);
    }


    @Test
    public void testJoinTableGS2Joins() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.asm()
                .addKeys("id", "student_id")
                .newMapper(ProfessorGS.class);

        List<ProfessorGS> professors = mapper.forEach(setUpResultSetMock(), new ListHandler<ProfessorGS>()).getList();
        validateGS(professors);

        //IFJAVA8_START
        validateGS(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        validateGS(mapper.stream(setUpResultSetMock()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorGS> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<ProfessorGS>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateGS(professors);
    }

    @Test
    public void testJoinTableGSManualMapping() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.asm()
                .newBuilder(ProfessorGS.class)
                .addKey("id")
                .addMapping("name")
                .addMapping("students_id")
                .addKey("students_name")
                .mapper();

        List<ProfessorGS> professors = mapper.forEach(setUpResultSetMock(), new ListHandler<ProfessorGS>()).getList();
        validateGS(professors);

        //IFJAVA8_START
        validateGS(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        validateGS(mapper.stream(setUpResultSetMock()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorGS> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<ProfessorGS>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateGS(professors);
    }



    @Test
    public void testJoinTableC() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorC> mapper = JdbcMapperFactoryHelper.asm()
                .addKeys("id", "students_id")
                .newMapper(ProfessorC.class);

        List<ProfessorC> professors = mapper.forEach(setUpResultSetMock(), new ListHandler<ProfessorC>()).getList();
        validateC(professors);

        //IFJAVA8_START
        validateC(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        validateC(mapper.stream(setUpResultSetMock()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorC> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<ProfessorC>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateC(professors);
    }


    @Test
    public void testJoinTableCNoAsm() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorC> mapper = JdbcMapperFactoryHelper.noAsm()
                .addKeys("id", "students_id")
                .newMapper(ProfessorC.class);

        ResultSet rs = setUpResultSetMock();
        ListHandler<ProfessorC> listHandler = new ListHandler<ProfessorC>();
        List<ProfessorC> professors = mapper.forEach(rs, listHandler).getList();
        validateC(professors);

        //IFJAVA8_START
        validateC(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        validateC(mapper.stream(setUpResultSetMock()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorC> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<ProfessorC>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateC(professors);
    }


    private ResultSet setUpResultSetMock() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(metaData.getColumnCount()).thenReturn(4);
        when(metaData.getColumnLabel(1)).thenReturn("id");
        when(metaData.getColumnLabel(2)).thenReturn("name");
        when(metaData.getColumnLabel(3)).thenReturn("students_id");
        when(metaData.getColumnLabel(4)).thenReturn("students_name");

        when(rs.getMetaData()).thenReturn(metaData);

        final AtomicInteger ai = new AtomicInteger();

        final int[] professorIds = new int[]{1, 1, 2};
        final String[] professorNames = new String[] {"professor1", "professor1", "professor2"};
        final int[] studentIds = new int[]{3, 4, 3};
        final String[] studentNames = new String[] {"student3", "student4", "student3"};

        when(rs.next()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ai.getAndIncrement() < 3;
            }
        });
        when(rs.getInt(1)).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return professorIds[ai.get() - 1];
            }
        });
        when(rs.getString(2)).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return professorNames[ai.get() - 1];
            }
        });
        when(rs.getInt(3)).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return studentIds[ai.get() - 1];
            }
        });
        when(rs.getString(4))
        .then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return studentNames[ai.get() - 1];
            }
        });
        when(rs.getObject(1)).then(new Answer<Object>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return professorIds[ai.get() - 1];
            }
        });
        when(rs.getObject(3)).then(new Answer<Object>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return studentIds[ai.get() - 1];
            }
        });
        return rs;
    }

    private void validateGS(List<ProfessorGS> professors) {
        assertEquals(2, professors.size());
        assertEquals(1, professors.get(0).id);
        assertEquals("professor1", professors.get(0).name);
        assertEquals(2, professors.get(0).students.size());
        assertEquals(3, professors.get(0).students.get(0).id);
        assertEquals("student3", professors.get(0).students.get(0).name);
        assertEquals(4, professors.get(0).students.get(1).id);
        assertEquals("student4", professors.get(0).students.get(1).name);


        assertEquals(2, professors.get(1).id);
        assertEquals("professor2", professors.get(1).name);
        assertEquals(1, professors.get(1).students.size());
        assertEquals(3, professors.get(1).students.get(0).id);
        assertEquals("student3", professors.get(1).students.get(0).name);
    }


    private void validateC(List<ProfessorC> professors) {
        assertEquals(2, professors.size());
        assertEquals(1, professors.get(0).id);
        assertEquals("professor1", professors.get(0).name);
        assertEquals(2, professors.get(0).students.size());
        assertEquals(3, professors.get(0).students.get(0).id);
        assertEquals("student3", professors.get(0).students.get(0).name);
        assertEquals(4, professors.get(0).students.get(1).id);
        assertEquals("student4", professors.get(0).students.get(1).name);


        assertEquals(2, professors.get(1).id);
        assertEquals("professor2", professors.get(1).name);
        assertEquals(1, professors.get(1).students.size());
        assertEquals(3, professors.get(1).students.get(0).id);
        assertEquals("student3", professors.get(1).students.get(0).name);
    }
}
