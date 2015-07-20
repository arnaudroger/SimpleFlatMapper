package org.sfm.csv;

import org.junit.Test;
import org.sfm.beans.Db1DeepObject;
import org.sfm.beans.Db1DeepPartialObject;
import org.sfm.beans.DbFinal1DeepObject;
import org.sfm.test.jdbc.DbHelper;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.ListHandler;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvMapperBuilderSubObjectTest {

    @Test
    public void testMapDbObjectWithCustomReader() throws Exception {
        CsvMapperBuilder<Db1DeepObject> builder = new CsvMapperBuilder<Db1DeepObject>(Db1DeepObject.class,  ReflectionService.newInstance(true, false));
        CsvColumnDefinition columnDefinition = CsvColumnDefinition.customReaderDefinition(new CellValueReader<String>() {
            @Override
            public String read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                return "cv1";
            }
        });
        builder.addMapping("db_Object_name", columnDefinition);

        CsvMapper<Db1DeepObject> mapper = builder.mapper();

        Db1DeepObject v1 = mapper.iterator(new StringReader("v1")).next();

        assertEquals("cv1", v1.getDbObject().getName());
    }

	@Test
	public void testMapDbObject() throws Exception {
		
		CsvMapperBuilder<Db1DeepObject> builder = new CsvMapperBuilder<Db1DeepObject>(Db1DeepObject.class,  ReflectionService.newInstance(true, false));
		addDbObjectFields(builder);
		CsvMapper<Db1DeepObject> mapper = builder.mapper();
		
		List<Db1DeepObject> list = mapper.forEach(db1deepObjectCsvReader(), new ListHandler<Db1DeepObject>()).getList();
		assertEquals(1, list.size());
		
		Db1DeepObject o = list.get(0);
		assertEquals(1234, o.getId());
		assertEquals("val!", o.getValue());
		DbHelper.assertDbObjectMapping(o.getDbObject());
	}
	
	@Test
	public void testMapDbFinalObject() throws  Exception {
		
		CsvMapperBuilder<DbFinal1DeepObject> builder = new CsvMapperBuilder<DbFinal1DeepObject>(DbFinal1DeepObject.class);
		addDbObjectFields(builder);
		CsvMapper<DbFinal1DeepObject> mapper = builder.mapper();
		
		List<DbFinal1DeepObject> list = mapper.forEach(db1deepObjectCsvReader(), new ListHandler<DbFinal1DeepObject>()).getList();
		assertEquals(1, list.size());
		
		DbFinal1DeepObject o = list.get(0);
		assertEquals(1234, o.getId());
		assertEquals("val!", o.getValue());
		DbHelper.assertDbObjectMapping(o.getDbObject());
	}

    @Test
    public void testMapDbPartialObject() throws UnsupportedEncodingException, Exception {

        CsvMapperBuilder<Db1DeepPartialObject> builder = new CsvMapperBuilder<Db1DeepPartialObject>(Db1DeepPartialObject.class);
        addDbObjectFields(builder);
        CsvMapper<Db1DeepPartialObject> mapper = builder.mapper();

        List<Db1DeepPartialObject> list = mapper.forEach(db1deepObjectCsvReader(), new ListHandler<Db1DeepPartialObject>()).getList();
        assertEquals(1, list.size());

        Db1DeepPartialObject o = list.get(0);
        assertEquals(1234, o.getId());
        assertEquals("val!", o.getValue());
        DbHelper.assertDbObjectMapping(o.getDbObject());
    }


    public static Reader db1deepObjectCsvReader() throws UnsupportedEncodingException {
			return new StringReader("1234,val!,1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4");
	}
	public void addDbObjectFields(CsvMapperBuilder<?> builder) {
		builder
		.addMapping("id")
		.addMapping("value")
		.addMapping("db_Object_id")
		.addMapping("db_Object_name")
		.addMapping("db_Object_email")
		.addMapping("db_Object_creationTime")
		.addMapping("db_Object_typeOrdinal")
		.addMapping("db_Object_typeName");
	}
}
