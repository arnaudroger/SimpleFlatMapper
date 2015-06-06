package org.sfm.csv;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapper;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.MapperConfig;
import org.sfm.reflect.ReflectionService;

import static org.junit.Assert.*;

public class CsvWriterBuilderTest {

    @Test
    public void testWriteCsvOnDbObject() throws Exception {
        MapperConfig<CsvColumnKey,FieldMapperColumnDefinition<CsvColumnKey,DbObject>> config =
                MapperConfig.<DbObject, CsvColumnKey>fieldMapperConfig();
        CsvWriterBuilder<DbObject> builder = new CsvWriterBuilder<DbObject>(DbObject.class,
                ReflectionService.disableAsm(), config);

        FieldMapper<DbObject, Appendable> mapper =
                builder.addColumn("id")
                        .addColumn("name")
                        .addColumn("email")
                        .mapper();

        DbObject dbObject = new DbObject();
        dbObject.setId(13);
        dbObject.setEmail("email");
        dbObject.setName("name");

        StringBuilder sb = new StringBuilder();

        mapper.mapTo(dbObject, sb, null);

        assertEquals("13,name,email", sb.toString());
    }
}