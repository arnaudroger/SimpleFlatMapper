package org.sfm.csv;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.EnumOrdinalFormatProperty;
import org.sfm.reflect.TypeReference;
import org.sfm.tuples.Tuple2;
import org.sfm.tuples.Tuples;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CsvWriterTest {

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
    public void testWriterWithManualHeaders() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.from(DbObject.class).columns("id", "name").to(sw).append(newDbObject());
        assertEquals(
                "id,name\r\n" +
                        "13,name\r\n",
                sw.toString());
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
}