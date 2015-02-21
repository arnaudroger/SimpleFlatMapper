package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.utils.ListHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
                .newBuilder(ProfessorField.class)
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
                .joinOn("id");


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
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.noAsm()
                .newBuilder(ProfessorGS.class)
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
                .joinOn("id");

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
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.asm()
                .newBuilder(ProfessorGS.class)
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
                .joinOn("id");

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
        JdbcMapper<ProfessorC> mapper = JdbcMapperFactoryHelper.asm()
                .newBuilder(ProfessorC.class)
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
                .joinOn("id");

        List<ProfessorC> professors = mapper.forEach(setUpResultSetMockConstructor(), new ListHandler<ProfessorC>()).getList();
        validateC(professors);

        //IFJAVA8_START
        validateC(mapper.stream(setUpResultSetMockConstructor()).collect(Collectors.toList()));

        validateC(mapper.stream(setUpResultSetMockConstructor()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorC> iterator = mapper.iterator(setUpResultSetMockConstructor());
        professors = new ArrayList<ProfessorC>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateC(professors);
    }


    @Test
    public void testJoinTableCNoAsm() throws SQLException {
        JdbcMapper<ProfessorC> mapper = JdbcMapperFactoryHelper.noAsm()
                .newBuilder(ProfessorC.class)
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
                .joinOn("id");

        ResultSet rs = setUpResultSetMockConstructor();
        ListHandler<ProfessorC> listHandler = new ListHandler<ProfessorC>();
        List<ProfessorC> professors = mapper.forEach(rs, listHandler).getList();
        validateC(professors);

        //IFJAVA8_START
        validateC(mapper.stream(setUpResultSetMockConstructor()).collect(Collectors.toList()));

        validateC(mapper.stream(setUpResultSetMockConstructor()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorC> iterator = mapper.iterator(setUpResultSetMockConstructor());
        professors = new ArrayList<ProfessorC>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateC(professors);
    }


    private ResultSet setUpResultSetMock() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        when(rs.next()).thenReturn(true, true, true, false);
        when(rs.getInt(1)).thenReturn(1, 1, 2);
        when(rs.getString(2)).thenReturn("professor1", "professor1", "professor2");
        when(rs.getInt(3)).thenReturn(3, 4, 3);
        when(rs.getString(4)).thenReturn("student3", "student4", "student3");
        when(rs.getObject(1)).thenReturn(1, 1, 2);
        return rs;
    }

    private ResultSet setUpResultSetMockConstructor() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        when(rs.next()).thenReturn(true, true, true, false);
        when(rs.getInt(1)).thenReturn(1, 2);
        when(rs.getString(2)).thenReturn("professor1", "professor2");
        when(rs.getInt(3)).thenReturn(3, 4, 3);
        when(rs.getString(4)).thenReturn("student3", "student4", "student3");
        when(rs.getObject(1)).thenReturn(1, 1, 2);
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
