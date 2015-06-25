package org.sfm.csv;

import org.junit.Test;
import org.sfm.beans.DbObject;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

public class CsvWriterTest {

    @Test
    public void testWriterDefaultBehaviour() throws ParseException, IOException {
        StringWriter sw = new StringWriter();
        CsvWriter.map(DbObject.class).writeTo(sw).write(newDbObject());
        assertEquals(
                "id,name,email,creation_time,type_ordinal,type_name\r\n" +
                "13,name,email,2015-06-06 17:46:23,type2,type3\r\n",
                sw.toString());
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