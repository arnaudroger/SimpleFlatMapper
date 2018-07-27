package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvWriter;
import org.simpleflatmapper.lightningcsv.CsvCellWriter;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.property.RenameProperty;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.property.DateFormatProperty;
import org.simpleflatmapper.map.property.EnumOrdinalFormatProperty;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuples;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CsvWriterTest {

    @Test
    public void testWriterCustomEscaping() throws ParseException, IOException {
        StringWriter sw = new StringWriter();

        DbObject value = newDbObject();
        value.setName("Arnaud\"What\"");
        
        CsvWriter.from(DbObject.class).escape('\\').to(sw).append(value);
        assertEquals(
                "id,name,email,creation_time,type_ordinal,type_name\r\n" +
                        "13,\"Arnaud\\\"What\\\"\",email,2015-06-06 17:46:23,type2,type3\r\n",
                sw.toString());
    } 
    
    @Test
    public void testWriterCustomCellWriter() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class).separator('\t').quote('\'').alwaysEscape().endOfLine("\n").to(sw).append(newDbObject());
        assertEquals(
                "\'id\'\t\'name\'\t\'email\'\t\'creation_time\'\t\'type_ordinal\'\t\'type_name\'\n" +
                        "\'13\'\t\'name\'\t\'email\'\t\'2015-06-06 17:46:23\'\t\'type2\'\t\'type3\'\n",
                sw.toString());
    }

    @Test
    public void testWriterCustomSeparator() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class).separator('\t').to(sw).append(newDbObject());
        assertEquals(
                "id\tname\temail\tcreation_time\ttype_ordinal\ttype_name\r\n" +
                        "13\tname\temail\t2015-06-06 17:46:23\ttype2\ttype3\r\n",
                sw.toString());
    }

    @Test
    public void testWriterDefaultBehaviour() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class).to(sw).append(newDbObject());
        assertEquals(
                "id,name,email,creation_time,type_ordinal,type_name\r\n" +
                "13,name,email,2015-06-06 17:46:23,type2,type3\r\n",
                sw.toString());
    }

    @Test
    public void testWriterSkipHeader() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class).skipHeaders().to(sw).append(newDbObject());
        assertEquals(
                        "13,name,email,2015-06-06 17:46:23,type2,type3\r\n",
                sw.toString());
    }

    @Test
    public void testWriterWithCustomizedCellWriter() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class).cellWriter(CsvCellWriter.DEFAULT_WRITER.separator('\t').quote('\'').alwaysEscape(true).endOfLine("\n")).to(sw).append(newDbObject());
        assertEquals(
                "'id'\t'name'\t'email'\t'creation_time'\t'type_ordinal'\t'type_name'\n'13'\t'name'\t'email'\t'2015-06-06 17:46:23'\t'type2'\t'type3'\n",
                sw.toString());
    }

    @Test
    public void testWriterWithManualHeaders() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class).columns("id", "name").to(sw).append(newDbObject());
        assertEquals(
                "id,name\r\n" +
                        "13,name\r\n",
                sw.toString());
    }

    @Test
    public void testWriterWithManualHeadersAfterSkipHeaders() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class).skipHeaders().columns("id", "name").to(sw).append(newDbObject());
        assertEquals(
                "13,name\r\n",
                sw.toString());
    }

    @Test
    public void testWriterWithOneManualColumnWithFormat() throws  Exception {
        StringWriter sw = new StringWriter();
        final CsvWriter.CsvWriterDSL<DbObject> dbObjectCsvWriterDSL = CsvWriter.from(DbObject.class).skipHeaders();
        final CsvWriter<DbObject> to = dbObjectCsvWriterDSL.column("creation_time", new SimpleDateFormat("yyyyMMdd")).to(sw);
        to.append(newDbObject());
        assertEquals(
                "20150606\r\n",
                sw.toString());

    }

    @Test
    public void testWriterFailOnInvalidColumn() throws  Exception {
        try {
            CsvWriter.from(DbObject.class).columns("id", "nonexistent");
            fail();
        } catch(MapperBuildingException e) {
            // expected
        }
    }

    @Test
    public void testWriterWithCustomMapperConfig() throws  Exception {
        CsvWriter.CsvWriterDSL<DbObject> from = CsvWriter.from(DbObject.class);
        from = from.mapperConfig(from.mapperConfig().mapperBuilderErrorHandler(new MapperBuilderErrorHandler() {
            @Override
            public void accessorNotFound(String msg) {
            }

            @Override
            public void propertyNotFound(Type target, String property) {
            }

            @Override
            public void customFieldError(FieldKey<?> key, String message) {
            }
        }));

        from.columns("nonexistent");
    }

    @Test
    public void testWriterWithFormatter() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class)
                .columns("id", "name")
                .column("creation_time", new DateFormatProperty("dd/MM/yyyy"))
                .column("type_ordinal", new EnumOrdinalFormatProperty())
                .to(sw).append(newDbObject());
        assertEquals(
                "id,name,creation_time,type_ordinal\r\n" +
                        "13,name,06/06/2015,1\r\n",
                sw.toString());
    }

    @Test
    public void testOnTuples() throws IOException {
        StringWriter sw = new StringWriter();
        CsvWriter
                .from(Tuples.typeDef(String.class, Double.class))
                .to(sw)
                .append(new Tuple2<String, Double>("aa", 3.1));
        assertEquals("element0,element1\r\naa,3.1\r\n", sw.toString());
    }

    @Test
    public void testFailOnListWithDefaultHeader() throws Exception {
        try {
            CsvWriter.from(new TypeReference<List<String>>() {
            }).to(new StringWriter());
            fail();
        } catch(IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testFailOnListWithDefaultHeaderAndSkipHeaders() throws IOException {
        try {
            CsvWriter.from(new TypeReference<List<String>>() {
            })
                    .skipHeaders()
                    .to(new StringWriter());
            fail();
        } catch(IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testListOnSpecifiedColumn() throws Exception {
        StringWriter sw = new StringWriter();
        CsvWriter.from(new TypeReference<List<String>>() {
        }).columns("etl0", "elt1", "elt2").to(sw).append(Arrays.asList("e11", "e12"));

        assertEquals("etl0,elt1,elt2\r\ne11,e12,\r\n", sw.toString());
    }

    public static DbObject newDbObject() throws ParseException {
        DbObject dbObject = new DbObject();
        dbObject.setId(13);
        dbObject.setEmail("email");
        dbObject.setName("name");
        dbObject.setCreationTime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("06/06/2015 17:46:23"));
        dbObject.setTypeOrdinal(DbObject.Type.type2);
        dbObject.setTypeName(DbObject.Type.type3);
        return dbObject;
    }
    
    @Test
    public void testIssue461() throws IOException {
        CsvWriter.CsvWriterDSL<Pojo461> csvWriterConfig = CsvWriter.from(Pojo461.class)
                .column("timestamp", new RenameProperty("ts")) // OK
                .column("action");
        
        StringBuilder sb = new StringBuilder();
        csvWriterConfig.to(sb).append(new Pojo461(1, "add"));

        System.out.println("sb = " + sb);
    }
    
    @Test
    public void testIssue461Part2() throws IOException {
        CsvWriter.CsvWriterDSL<Pojo461> dsl = CsvWriter.from(Pojo461.class);
        try {
            dsl.column("ts", new RenameProperty("timestamp"));
            fail();
        } catch (MappingException ex) {
            // expected
        }
    }


    public static class Pojo461 {
        public final long ts;
        public final String action;

        private Pojo461(long ts, String action) {
            this.ts = ts;
            this.action = action;
        }
    }
}
