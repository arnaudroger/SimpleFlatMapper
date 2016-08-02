package org.simpleflatmapper.test.jdbc;

import java.util.List;

import static org.junit.Assert.*;


public class JoinTest {


    public static final Object[][] ROWS = new Object[][]{
            {1, "professor1", 3, "student3", "phone31"},
            {1, "professor1", 3, "student3", "phone32"},
            {1, "professor1", 4, "student4", "phone41"},
            {2, "professor2", 4, "student4", "phone51"},
            {2, "professor2", 4, "student4", "phone52"},
            {3, "professor3", null, null, null}
    };


    public static void validateProfessors(List<? extends Professor<?>> professors) {
        assertEquals("we get 3 professors from the resultset", 3, professors.size());
        final Professor<?> professor1 = professors.get(0);

        assertPersonEquals(1, "professor1", professor1);
        assertEquals("has 2 students", 2, professor1.getStudents().size());
        assertPersonEquals(3, "student3", professor1.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31", "phone32"}, professor1.getStudents().get(0).getPhones().toArray());
        assertPersonEquals(4, "student4", professor1.getStudents().get(1));
        assertArrayEquals(new Object[]{"phone41"}, professor1.getStudents().get(1).getPhones().toArray());


        final Professor<?> professor2 = professors.get(1);
        assertPersonEquals(2, "professor2", professor2);
        assertEquals("has 1 student", 1, professor2.getStudents().size());
        assertPersonEquals(4, "student4", professor2.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone51", "phone52"}, professor2.getStudents().get(0).getPhones().toArray());

        final Professor<?> professor3 = professors.get(2);
        assertPersonEquals(3, "professor3", professor3);
        assertNotNull(professor3.getStudents());
        assertTrue("professor3 has no students", professor3.getStudents().isEmpty());

    }

    public static void assertPersonEquals(int id, String name, Person person) {
        assertEquals(id, person.getId());
        assertEquals(name, person.getName());
    }


    public static <T extends Professor<?>> void validateProfessorMap(T professor0) {
        assertPersonEquals(1, "professor1", professor0);
        assertEquals("has 2 students", 1, professor0.getStudents().size());
        assertPersonEquals(3, "student3", professor0.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31"}, professor0.getStudents().get(0).getPhones().toArray());
    }
    public static <T extends Professor<?>> void validateProfessorMapTo(T professor0) {
        assertPersonEquals(1, "professor1", professor0);
        assertEquals("has 2 students", 2, professor0.getStudents().size());
        assertPersonEquals(3, "student3", professor0.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31"}, professor0.getStudents().get(0).getPhones().toArray());
        assertPersonEquals(4, "student4", professor0.getStudents().get(1));
        assertArrayEquals(new Object[]{"phone41"}, professor0.getStudents().get(1).getPhones().toArray());

    }


    private <T extends Professor<?>> void validateProfessor(T professor) {
        assertPersonEquals(1, "professor1", professor);
    }

    public interface Person {
        int getId();
        String getName();
    }

    public interface Student  extends Person {
        List<String> getPhones();
    }

    public interface Professor<T extends Student> extends Person {
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

}
