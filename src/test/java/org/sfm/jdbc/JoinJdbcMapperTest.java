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

    public static interface Student {
        int getId();

        String getName();

        List<String> getPhones();
    }

    public static interface Professor<T extends Student> {
        int getId();

        String getName();

        List<T> getStudents();
    }
    public static class StudentField implements Student {
        public int id;
        public String name;
        public List<String> phones;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getPhones() {
            return phones;
        }
    }
    public static class ProfessorField implements Professor<StudentField> {
        public int id;
        public String name;
        public List<StudentField> students;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<StudentField> getStudents() {
            return students;
        }
    }

    public static class StudentGS implements Student {
        private int id;
        private String name;
        private List<String> phones;

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

        public List<String> getPhones() {
            return phones;
        }

        public void setPhones(List<String> phones) {
            this.phones = phones;
        }
    }
    public static class ProfessorGS implements Professor<StudentGS> {
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


    public static class StudentC implements Student {
        private final int id;
        private final String name;
        private final List<String> phones;

        public StudentC(int id, String name, List<String> phones) {
            this.id = id;
            this.name = name;
            this.phones = phones;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<String> getPhones() {
            return phones;
        }
    }
    public static class ProfessorC implements  Professor<StudentC> {
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


        validateMapper(mapper);

    }

    @Test
    public void testJoinTableGSNoAsm() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.noAsm()
                .addKeys("id", "student_id")
                .newMapper(ProfessorGS.class);

        validateMapper(mapper);
    }

    private <T extends Professor<?>> void validateMapper(JdbcMapper<T> mapper) throws SQLException {
        List<T> professors = mapper.forEach(setUpResultSetMock(), new ListHandler<T>()).getList();
        validateProfessors(professors);

        //IFJAVA8_START
        validateProfessors(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        validateProfessors(mapper.stream(setUpResultSetMock()).limit(2).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<T> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<T>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        validateProfessors(professors);
    }

    @Test
    public void testJoinTableGS() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.asm()
                .addKeys("id", "student_id")
                .newMapper(ProfessorGS.class);

        validateMapper(mapper);
    }


    @Test
    public void testJoinTableGS2Joins() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.asm()
                .addKeys("id", "student_id")
                .newMapper(ProfessorGS.class);

        validateMapper(mapper);
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

        validateMapper(mapper);
    }



    @Test
    public void testJoinTableC() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorC> mapper = JdbcMapperFactoryHelper.asm()
                .addKeys("id", "students_id")
                .newMapper(ProfessorC.class);

        validateMapper(mapper);

    }


    @Test
    public void testJoinTableCNoAsm() throws SQLException {
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> key = FieldMapperColumnDefinition.key();
        JdbcMapper<ProfessorC> mapper = JdbcMapperFactoryHelper.noAsm()
                .addKeys("id", "students_id")
                .newMapper(ProfessorC.class);

        validateMapper(mapper);

    }


    private ResultSet setUpResultSetMock() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);

        when(metaData.getColumnCount()).thenReturn(4);
        when(metaData.getColumnLabel(1)).thenReturn("id");
        when(metaData.getColumnLabel(2)).thenReturn("name");
        when(metaData.getColumnLabel(3)).thenReturn("students_id");
        when(metaData.getColumnLabel(4)).thenReturn("students_name");
//        when(metaData.getColumnLabel(5)).thenReturn("students_phones");

        when(rs.getMetaData()).thenReturn(metaData);

        final AtomicInteger ai = new AtomicInteger();

//        final int[] professorIds = new int[]{1, 1, 1, 2, 2, 3};
//        final String[] professorNames = new String[] {"professor1", "professor1", "professor1", "professor2", "professor2", "professor3"};
//        final Integer[] studentIds = new Integer []{3, 4, 4, 5, 5, null};
//        final String[] studentNames = new String[] {"student3", "student4", "student4", "student5", "student5", null};
//        final String[] phones = new String[] {"phone31", "phone41", "phone42", "phone51", "phone52", null};

        final int[] professorIds = new int[]{1, 1, 2};
        final String[] professorNames = new String[] {"professor1", "professor1", "professor2"};
        final Integer[] studentIds = new Integer []{3, 4, 5, 5, null};
        final String[] studentNames = new String[] {"student3", "student4","student5"};
        final String[] phones = new String[] {"phone31", "phone41", "phone42", "phone51", "phone52", null};

        when(rs.next()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ai.getAndIncrement() < professorIds.length;
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
                Integer studentId = studentIds[ai.get() - 1];
                return studentId != null ? studentId : 0;
            }
        });
        when(rs.getString(4))
        .then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return studentNames[ai.get() - 1];
            }
        });
        when(rs.getString(5))
                .then(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                        return phones[ai.get() - 1];
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

    private void validateProfessors(List<? extends Professor<?>> professors) {
        assertEquals(2, professors.size());
        assertEquals(1, professors.get(0).getId());
        assertEquals("professor1", professors.get(0).getName());
        assertEquals(2, professors.get(0).getStudents().size());
        assertEquals(3, professors.get(0).getStudents().get(0).getId());
        assertEquals("student3", professors.get(0).getStudents().get(0).getName());
        assertEquals(4, professors.get(0).getStudents().get(1).getId());
        assertEquals("student4", professors.get(0).getStudents().get(1).getName());


        assertEquals(2, professors.get(1).getId());
        assertEquals("professor2", professors.get(1).getName());
        assertEquals(1, professors.get(1).getStudents().size());
        assertEquals(5, professors.get(1).getStudents().get(0).getId());
        assertEquals("student5", professors.get(1).getStudents().get(0).getName());
    }

}
