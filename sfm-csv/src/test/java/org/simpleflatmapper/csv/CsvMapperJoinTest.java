package org.simpleflatmapper.csv;

import org.junit.Test;
import org.simpleflatmapper.test.beans.Person;
import org.simpleflatmapper.test.jdbc.JoinTest;
import org.simpleflatmapper.test.beans.ProfessorC;
import org.simpleflatmapper.test.beans.ProfessorField;
import org.simpleflatmapper.test.beans.ProfessorGS;
import org.simpleflatmapper.util.ListCollector;

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
        final CsvMapper<ProfessorGS> mapper =
                geStaticCsvMapper(getCsvMapperFactory(), ProfessorGS.class);

        final List<ProfessorGS> professors =
                mapper.forEach(new StringReader(DATA), new ListCollector<ProfessorGS>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testStaticProfessorGSSharding() throws IOException {
        final CsvMapper<ProfessorGS> mapper =
                geStaticCsvMapper(getCsvShardingMapperFactory(), ProfessorGS.class);

        final List<ProfessorGS> professors =
                mapper.forEach(new StringReader(DATA), new ListCollector<ProfessorGS>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testStaticCsvParserDSL() throws IOException {
        final CsvParser.StaticMapToDSL<ProfessorGS> professorGSStaticMapToDSL = CsvParser.mapTo(ProfessorGS.class)
                .addKey("id")
                .addMapping("name")
                .addKey("students_id")
                .addMapping("students_name");
        List<ProfessorGS> professors =
                professorGSStaticMapToDSL
                        .addMapping("students_phones_value")
                        .forEach(new StringReader(HEADER_DATA), new ListCollector<ProfessorGS>())
                        .getList();
        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorGS() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<ProfessorGS> professors =
                mapperFactory.newMapper(ProfessorGS.class).forEach(new StringReader(HEADER_DATA), new ListCollector<ProfessorGS>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorGSSharding() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvShardingMapperFactory();

        final List<ProfessorGS> professors =
                mapperFactory.newMapper(ProfessorGS.class).forEach(new StringReader(HEADER_DATA), new ListCollector<ProfessorGS>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicCsvParserDSL() throws IOException {
        List<ProfessorGS> professors =
                CsvParser.mapTo(ProfessorGS.class)
                        .addKeys("id", "students_id")
                        .forEach(new StringReader(HEADER_DATA), new ListCollector<ProfessorGS>())
                        .getList();
        JoinTest.validateProfessors(professors);
    }


    @Test
    public void testStaticProfessorC() throws IOException {
        final CsvMapper<ProfessorC> mapper =
                geStaticCsvMapper(getCsvMapperFactory(), ProfessorC.class);

        final List<ProfessorC> professors =
                mapper.forEach(new StringReader(DATA), new ListCollector<ProfessorC>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testStaticProfessorCSharding() throws IOException {
        final CsvMapper<ProfessorC> mapper =
                geStaticCsvMapper(getCsvShardingMapperFactory(), ProfessorC.class);

        final List<ProfessorC> professors =
                mapper.forEach(new StringReader(DATA), new ListCollector<ProfessorC>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorC() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<ProfessorC> professors =
                mapperFactory.newMapper(ProfessorC.class).forEach(new StringReader(HEADER_DATA), new ListCollector<ProfessorC>()).getList();

        JoinTest.validateProfessors(professors);
    }

    @Test
    public void testDynamicProfessorCSharding() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvShardingMapperFactory();

        final List<ProfessorC> professors =
                mapperFactory.newMapper(ProfessorC.class).forEach(new StringReader(HEADER_DATA), new ListCollector<ProfessorC>()).getList();

        JoinTest.validateProfessors(professors);
    }

   @Test
    public void testDynamicProfessorField() throws IOException {
        final CsvMapperFactory mapperFactory = getCsvMapperFactory();

        final List<ProfessorField> professors =
                mapperFactory.newMapper(ProfessorField.class).forEach(new StringReader(HEADER_DATA), new ListCollector<ProfessorField>()).getList();

        JoinTest.validateProfessors(professors);
    }


    private <T extends Person> CsvMapper<T> geStaticCsvMapper(CsvMapperFactory mapperFactory, Class<T> target) {
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
