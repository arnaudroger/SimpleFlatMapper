package org.sfm.csv;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.Db1DeepObject;
import org.sfm.beans.DbFinal1DeepObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.ListHandler;

public class CsvMapperBuilderSubObjectTest {

	@Test
	public void testMapDbObject() throws UnsupportedEncodingException, Exception {
		
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
	public void testMapDbFinalObject() throws UnsupportedEncodingException, Exception {
		
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
