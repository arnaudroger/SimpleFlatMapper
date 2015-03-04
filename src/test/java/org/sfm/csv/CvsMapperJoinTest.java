package org.sfm.csv;

import org.junit.Test;
import org.sfm.jdbc.JoinJdbcMapperTest;
import org.sfm.utils.ListHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class CvsMapperJoinTest {

    private static final String DATA = "1,professor1,3,student3,phone31\n"
            + "1,professor1,3,student3,phone32\n"
            + "1,professor1,4,student4,phone41\n"
            + "2,professor2,4,student4,phone51\n"
            + "2,professor2,4,student4,phone52\n"
            + "3,professor3,,,";

    //@Test
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


        final List<JoinJdbcMapperTest.ProfessorGS> professors =
                mapper.forEach(new StringReader(DATA), new ListHandler<JoinJdbcMapperTest.ProfessorGS>()).getList();

        JoinJdbcMapperTest.validateProfessors(professors);
    }
}
