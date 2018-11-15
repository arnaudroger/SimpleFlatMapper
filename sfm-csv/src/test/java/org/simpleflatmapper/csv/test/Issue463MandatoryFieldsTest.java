package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.csv.property.MandatoryColumnProperty;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.Predicate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Issue463MandatoryFieldsTest {
    
    private static String payload = "f1,f2,f3\nv11,v12,v13\nv21,v22\nv31";
    
    @Test
    public void testNoMandatoryFields() throws IOException {
        CsvMapper<Pojo463> mapper = CsvMapperFactory
                .newInstance()
                .newMapper(Pojo463.class);
        List<Pojo463> list = 
                CsvParser.mapWith(mapper).forEach(payload, new ListCollector<Pojo463>()).getList();
        
        assertEquals(
                Arrays.asList(
                    new Pojo463("v11", "v12", "v13"),
                    new Pojo463("v21", "v22", null),
                    new Pojo463("v31", null, null)
                ), 
                list);
    }
    
    @Test
    public void testAllMandatoryFields() throws IOException {

        CsvMapper<Pojo463> mapper = CsvMapperFactory
                .newInstance()
                .addColumnProperty(new Predicate<CsvColumnKey>() {
                    @Override
                    public boolean test(CsvColumnKey csvColumnKey) {
                        return true;
                    }
                }, MandatoryColumnProperty.INSTANCE)
                .newMapper(Pojo463.class);
        
        List<Pojo463> list =
                CsvParser.mapWith(mapper).forEach(payload, new ListCollector<Pojo463>()).getList();

        assertEquals(
                Arrays.asList(
                        new Pojo463("v11", "v12", "v13")
                ),
                list);
        
    }

    @Test
    public void testF1MandatoryField() throws IOException {

        CsvMapper<Pojo463> mapper = CsvMapperFactory
                .newInstance()
                .addColumnProperty("f1", MandatoryColumnProperty.INSTANCE)
                .newMapper(Pojo463.class);

        List<Pojo463> list =
                CsvParser.mapWith(mapper).forEach(payload, new ListCollector<Pojo463>()).getList();

        assertEquals(
                Arrays.asList(
                        new Pojo463("v11", "v12", "v13"),
                        new Pojo463("v21", "v22", null),
                        new Pojo463("v31", null, null)
                ),
                list);

    }

    @Test
    public void testF2MandatoryField() throws IOException {

        CsvMapper<Pojo463> mapper = CsvMapperFactory
                .newInstance()
                .addColumnProperty("f2", MandatoryColumnProperty.INSTANCE)
                .newMapper(Pojo463.class);

        List<Pojo463> list =
                CsvParser.mapWith(mapper).forEach(payload, new ListCollector<Pojo463>()).getList();

        assertEquals(
                Arrays.asList(
                        new Pojo463("v11", "v12", "v13"),
                        new Pojo463("v21", "v22", null)
                ),
                list);

    }

    @Test
    public void testF3MandatoryField() throws IOException {

        CsvMapper<Pojo463> mapper = CsvMapperFactory
                .newInstance()
                .addColumnProperty("f3", MandatoryColumnProperty.INSTANCE)
                .newMapper(Pojo463.class);

        List<Pojo463> list =
                CsvParser.mapWith(mapper).forEach(payload, new ListCollector<Pojo463>()).getList();

        assertEquals(
                Arrays.asList(
                        new Pojo463("v11", "v12", "v13")
                        ),
                list);

    }


    public static class Pojo463 {
        public final String f1;
        public final String f2;
        public final String f3;

        public Pojo463(String f1, String f2, String f3) {
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pojo463 pojo463 = (Pojo463) o;

            if (f1 != null ? !f1.equals(pojo463.f1) : pojo463.f1 != null) return false;
            if (f2 != null ? !f2.equals(pojo463.f2) : pojo463.f2 != null) return false;
            return f3 != null ? f3.equals(pojo463.f3) : pojo463.f3 == null;
        }

        @Override
        public int hashCode() {
            int result = f1 != null ? f1.hashCode() : 0;
            result = 31 * result + (f2 != null ? f2.hashCode() : 0);
            result = 31 * result + (f3 != null ? f3.hashCode() : 0);
            return result;
        }
    }
}
