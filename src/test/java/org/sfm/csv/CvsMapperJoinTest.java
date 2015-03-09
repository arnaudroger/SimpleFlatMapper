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
            + "3,professor3,,,,";
    private static final String HEADER_DATA = "id,name,students_id,students_name,students_phones_value\n"
            + DATA;

    @Test
    public void testStaticProfessorGS() throws IOException {
        final CsvMapper<JoinJdbcMapperTest.ProfessorGS> mapper =
                geStaticCsvMapper(getCsvMapperFactory(), JoinJdbcMapperTest.ProfessorGS.class);

        final List<JoinJdbcMapperTest.ProfessorGS> professors =
                mapper.forEach(new StringReader(DATA), new ListHandler<JoinJdbcMapperTest.ProfessorGS>()).getList();

        JoinJdbcMapperTest.validateProfessors(professors);
    }

    @Test
    public void testStaticProfessorC() throws IOException {
        final CsvMapper<JoinJdbcMapperTest.ProfessorC> mapper =
                geStaticCsvMapper(getCsvMapperFactory(), JoinJdbcMapperTest.ProfessorC.class);

        final List<JoinJdbcMapperTest.ProfessorC> professors =
                mapper.forEach(new StringReader(DATA), new ListHandler<JoinJdbcMapperTest.ProfessorC>()).getList();

        JoinJdbcMapperTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorGS() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<JoinJdbcMapperTest.ProfessorGS> professors =
                mapperFactory.newMapper(JoinJdbcMapperTest.ProfessorGS.class).forEach(new StringReader(HEADER_DATA), new ListHandler<JoinJdbcMapperTest.ProfessorGS>()).getList();

        JoinJdbcMapperTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorC() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<JoinJdbcMapperTest.ProfessorC> professors =
                mapperFactory.newMapper(JoinJdbcMapperTest.ProfessorC.class).forEach(new StringReader(HEADER_DATA), new ListHandler<JoinJdbcMapperTest.ProfessorC>()).getList();

        JoinJdbcMapperTest.validateProfessors(professors);
    }

   @Test
    public void testDynamicProfessorField() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<JoinJdbcMapperTest.ProfessorField> professors =
                mapperFactory.newMapper(JoinJdbcMapperTest.ProfessorField.class).forEach(new StringReader(HEADER_DATA), new ListHandler<JoinJdbcMapperTest.ProfessorField>()).getList();

        JoinJdbcMapperTest.validateProfessors(professors);
    }


    private <T extends JoinJdbcMapperTest.Person> CsvMapper<T> geStaticCsvMapper(CsvMapperFactory mapperFactory, Class<T> target) {
        final CsvMapperBuilder<T> builder = mapperFactory
                .newBuilder(target);
        return builder
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
                .addMapping("students_phones_value")
                .<T>mapper();
    }

    private CsvMapperFactory getCsvMapperFactory() {
        return CsvMapperFactory
                .newInstance().useAsm(false)
                .addKeys("id", "students_id");
    }

}
