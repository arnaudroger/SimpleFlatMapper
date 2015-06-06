package org.sfm.csv;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.EnumOrdinalFormatProperty;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.MapperConfig;
import org.sfm.reflect.ReflectionService;

import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

public class CsvWriterBuilderTest {

    @Test
    public void testWriteCsvOnDbObject() throws Exception {
        MapperConfig<CsvColumnKey,FieldMapperColumnDefinition<CsvColumnKey,DbObject>> config =
                MapperConfig.<DbObject, CsvColumnKey>fieldMapperConfig();
        CsvWriterBuilder<DbObject> builder = new CsvWriterBuilder<DbObject>(DbObject.class,
                ReflectionService.disableAsm(), config);

        Mapper<DbObject, Appendable> mapper =
                builder.addColumn("id")
                        .addColumn("name")
                        .addColumn("email")
                        .addColumn("creation_time", new DateFormatProperty("dd/MM/yyyy HH:mm:ss"))
                        .addColumn("type_ordinal", new EnumOrdinalFormatProperty())
                        .addColumn("type_name")
                        .mapper();

        DbObject dbObject = new DbObject();
        dbObject.setId(13);
        dbObject.setEmail("email");
        dbObject.setName("name");
        dbObject.setCreationTime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("06/06/2015 17:46:23"));
        dbObject.setTypeOrdinal(DbObject.Type.type2);
        dbObject.setTypeName(DbObject.Type.type3);

        assertEquals("13,name,email,06/06/2015 17:46:23,1,type3\r\n", mapper.map(dbObject).toString());
    }
}