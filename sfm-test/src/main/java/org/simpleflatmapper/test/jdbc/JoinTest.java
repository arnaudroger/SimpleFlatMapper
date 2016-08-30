package org.simpleflatmapper.test.jdbc;

import org.simpleflatmapper.test.beans.Person;
import org.simpleflatmapper.test.beans.Professor;

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


}
