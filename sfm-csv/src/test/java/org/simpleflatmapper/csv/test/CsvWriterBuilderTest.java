package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvWriterBuilder;
import org.simpleflatmapper.map.mapper.ContextualSourceFieldMapperImpl;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.property.DateFormatProperty;
import org.simpleflatmapper.map.property.EnumOrdinalFormatProperty;

import static org.junit.Assert.*;

public class CsvWriterBuilderTest {

    @Test
    public void testWriteCsvOnDbObject() throws Exception {
        CsvWriterBuilder<DbObject> builder = CsvWriterBuilder.newBuilder(DbObject.class);

        ContextualSourceFieldMapperImpl<DbObject, Appendable> mapper =
                builder.addColumn("id")
                        .addColumn("name")
                        .addColumn("email")
                        .addColumn("creation_time", new DateFormatProperty("dd/MM/yyyy HH:mm:ss"))
                        .addColumn("type_ordinal", new EnumOrdinalFormatProperty())
                        .addColumn("type_name")
                        .mapper();

        DbObject dbObject = CsvWriterTest.newDbObject();

        StringBuilder sb = new StringBuilder();
        mapper.mapTo(dbObject, sb, mapper.newMappingContext());
        assertEquals("13,name,email,06/06/2015 17:46:23,1,type3\r\n", sb.toString());

        sb = new StringBuilder();
        dbObject.setEmail("email,e\" ");
        mapper.mapTo(dbObject, sb, mapper.newMappingContext());
        assertEquals("13,name,\"email,e\"\" \",06/06/2015 17:46:23,1,type3\r\n", sb.toString());

    }

}