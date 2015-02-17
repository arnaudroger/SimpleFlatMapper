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
        private List<StudentField> students;

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

        public List<StudentField> getStudents() {
            return students;
        }

        public void setStudents(List<StudentField> students) {
            this.students = students;
        }
    }


    @Test
    public void testJoinTableFields() throws SQLException {
        JdbcMapper<ProfessorField> mapper = JdbcMapperFactory.newInstance().disableAsm(true)
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
    public void testJoinTableGS() throws SQLException {
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactory.newInstance().disableAsm(true)
                .newBuilder(ProfessorGS.class)
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
                .joinOn("id");

        List<ProfessorGS> professors = mapper.forEach(setUpResultSetMock(), new ListHandler<ProfessorGS>()).getList();
        validate(professors);

        //IFJAVA8_START
        validate(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        validate(mapper.stream(setUpResultSetMock()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<ProfessorGS> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<ProfessorGS>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validate(professors);




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

    private void validate(List<ProfessorGS> professors) {
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
