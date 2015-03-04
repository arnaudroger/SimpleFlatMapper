package org.sfm.csv;

import org.junit.Test;
import org.sfm.jdbc.JoinJdbcMapperTest;
import org.sfm.utils.ListHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CvsMapperJoinTest {

//    private static final String DATA = "1,professor1,3,student3,phone31\n"
//            + "1,professor1,3,student3,phone32\n"
//            + "1,professor1,4,student4,phone41\n"
//            + "2,professor2,4,student4,phone51\n"
//            + "2,professor2,4,student4,phone52\n"
//            + "3,professor3,,,";

    private static final String DATA = "1,professor1\n"
            + "1,professor1,3,student3\n"
            + "1,professor1,4,student4,\n"
            + "2,professor2,4,student4\n"
            + "2,professor2,4,student4\n"
            + "3,professor3,,";


    @Test
    public void testProfessorGS() throws IOException {
        final CsvMapper<JoinJdbcMapperTest.ProfessorGS> mapper = CsvMapperFactory
                .newInstance()
                .addKeys("id", "students_id")
                .newBuilder(JoinJdbcMapperTest.ProfessorGS.class)
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
              //  .addMapping("students_phones_value")
                .mapper();


        final StringReader reader = new StringReader(DATA);
        final ListHandler<JoinJdbcMapperTest.ProfessorGS> handler = new ListHandler<JoinJdbcMapperTest.ProfessorGS>();
        final List<JoinJdbcMapperTest.ProfessorGS> professors =
                mapper.forEach(reader, handler).getList();

        validateProfessors(professors);
    }

    public static void validateProfessors(List<? extends JoinJdbcMapperTest.Professor<?>> professors) {
        assertEquals("we get 3 professors from the resultset", 3, professors.size());
        final JoinJdbcMapperTest.Professor<?> professor1 = professors.get(0);

        JoinJdbcMapperTest.assertPersonEquals(1, "professor1", professor1);
//        assertEquals("has 2 students", 2, professor1.getStudents().size());
//        JoinJdbcMapperTest.assertPersonEquals(3, "student3", professor1.getStudents().get(0));
//        assertArrayEquals(new Object[]{"phone31", "phone32"}, professor1.getStudents().get(0).getPhones().toArray());
//        JoinJdbcMapperTest.assertPersonEquals(4, "student4", professor1.getStudents().get(1));
//        assertArrayEquals(new Object[]{"phone41"}, professor1.getStudents().get(1).getPhones().toArray());


        final JoinJdbcMapperTest.Professor<?> professor2 = professors.get(1);
        JoinJdbcMapperTest.assertPersonEquals(2, "professor2", professor2);
//        assertEquals("has 1 student", 1, professor2.getStudents().size());
//        JoinJdbcMapperTest.assertPersonEquals(4, "student4", professor2.getStudents().get(0));
//        assertArrayEquals(new Object[]{"phone51", "phone52"}, professor2.getStudents().get(0).getPhones().toArray());

        final JoinJdbcMapperTest.Professor<?> professor3 = professors.get(2);
        JoinJdbcMapperTest.assertPersonEquals(3, "professor3", professor3);
//        assertTrue("professor3 has no students", professor3.getStudents().isEmpty());

    }
}
