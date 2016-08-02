package org.simpleflatmapper.csv;

import org.junit.Test;
import org.simpleflatmapper.test.jdbc.JoinTest;
import org.simpleflatmapper.core.utils.ListCollectorHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class CsvMapperJoinTest {


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
        final CsvMapper<JoinTest.ProfessorGS> mapper =
                geStaticCsvMapper(getCsvMapperFactory(), JoinTest.ProfessorGS.class);

        final List<JoinTest.ProfessorGS> professors =
                mapper.forEach(new StringReader(DATA), new ListCollectorHandler<JoinTest.ProfessorGS>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testStaticProfessorGSSharding() throws IOException {
        final CsvMapper<JoinTest.ProfessorGS> mapper =
                geStaticCsvMapper(getCsvShardingMapperFactory(), JoinTest.ProfessorGS.class);

        final List<JoinTest.ProfessorGS> professors =
                mapper.forEach(new StringReader(DATA), new ListCollectorHandler<JoinTest.ProfessorGS>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testStaticCsvParserDSL() throws IOException {
        final CsvParser.StaticMapToDSL<JoinTest.ProfessorGS> professorGSStaticMapToDSL = CsvParser.mapTo(JoinTest.ProfessorGS.class)
                .addKey("id")
                .addMapping("name")
                .addKey("students_id")
                .addMapping("students_name");
        List<JoinTest.ProfessorGS> professors =
                professorGSStaticMapToDSL
                        .addMapping("students_phones_value")
                        .forEach(new StringReader(HEADER_DATA), new ListCollectorHandler<JoinTest.ProfessorGS>())
                        .getList();
        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorGS() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<JoinTest.ProfessorGS> professors =
                mapperFactory.newMapper(JoinTest.ProfessorGS.class).forEach(new StringReader(HEADER_DATA), new ListCollectorHandler<JoinTest.ProfessorGS>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorGSSharding() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvShardingMapperFactory();

        final List<JoinTest.ProfessorGS> professors =
                mapperFactory.newMapper(JoinTest.ProfessorGS.class).forEach(new StringReader(HEADER_DATA), new ListCollectorHandler<JoinTest.ProfessorGS>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicCsvParserDSL() throws IOException {
        List<JoinTest.ProfessorGS> professors =
                CsvParser.mapTo(JoinTest.ProfessorGS.class)
                        .addKeys("id", "students_id")
                        .forEach(new StringReader(HEADER_DATA), new ListCollectorHandler<JoinTest.ProfessorGS>())
                        .getList();
        JoinTest.validateProfessors(professors);
    }


    @Test
    public void testStaticProfessorC() throws IOException {
        final CsvMapper<JoinTest.ProfessorC> mapper =
                geStaticCsvMapper(getCsvMapperFactory(), JoinTest.ProfessorC.class);

        final List<JoinTest.ProfessorC> professors =
                mapper.forEach(new StringReader(DATA), new ListCollectorHandler<JoinTest.ProfessorC>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testStaticProfessorCSharding() throws IOException {
        final CsvMapper<JoinTest.ProfessorC> mapper =
                geStaticCsvMapper(getCsvShardingMapperFactory(), JoinTest.ProfessorC.class);

        final List<JoinTest.ProfessorC> professors =
                mapper.forEach(new StringReader(DATA), new ListCollectorHandler<JoinTest.ProfessorC>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorC() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<JoinTest.ProfessorC> professors =
                mapperFactory.newMapper(JoinTest.ProfessorC.class).forEach(new StringReader(HEADER_DATA), new ListCollectorHandler<JoinTest.ProfessorC>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorCSharding() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvShardingMapperFactory();

        final List<JoinTest.ProfessorC> professors =
                mapperFactory.newMapper(JoinTest.ProfessorC.class).forEach(new StringReader(HEADER_DATA), new ListCollectorHandler<JoinTest.ProfessorC>()).getList();

        JoinTest.validateProfessors(professors);
    }

   @Test
    public void testDynamicProfessorField() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<JoinTest.ProfessorField> professors =
                mapperFactory.newMapper(JoinTest.ProfessorField.class).forEach(new StringReader(HEADER_DATA), new ListCollectorHandler<JoinTest.ProfessorField>()).getList();

        JoinTest.validateProfessors(professors);
    }


    private <T extends JoinTest.Person> CsvMapper<T> geStaticCsvMapper(CsvMapperFactory mapperFactory, Class<T> target) {
        final CsvMapperBuilder<T> builder = mapperFactory
                .newBuilder(target);
        return builder
                .addMapping("id")
                .addMapping("name")
                .addMapping("students_id")
                .addMapping("students_name")
                .addMapping("students_phones_value")
                .mapper();
    }

    private CsvMapperFactory getCsvMapperFactory() {
        return CsvMapperFactory
                .newInstance().useAsm(false)
                .failOnAsm(true)
                .addKeys("id", "students_id");
    }
    private CsvMapperFactory getCsvShardingMapperFactory() {
        return getCsvMapperFactory().maxMethodSize(2);
    }
}
