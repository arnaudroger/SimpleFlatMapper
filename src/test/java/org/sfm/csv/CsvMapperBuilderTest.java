package org.sfm.csv;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.utils.ListHandler;

public class CsvMapperBuilderTest {

	@Test
	public void testMapDbObject() throws UnsupportedEncodingException, Exception {
		
		CsvMapperBuilder<DbObject> builder = new CsvMapperBuilder<DbObject>(DbObject.class);
		addDbObjectFields(builder);
		CsvMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvStream(), new ListHandler<DbObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	@Test
	public void testMapFinalDbObject() throws UnsupportedEncodingException, Exception {
		
		CsvMapperBuilder<DbFinalObject> builder = new CsvMapperBuilder<DbFinalObject>(DbFinalObject.class);
		addDbObjectFields(builder);
		
		CsvMapper<DbFinalObject> mapper = builder.mapper();
		
		List<DbFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvStream(), new ListHandler<DbFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	@Test
	public void testMaPartialFinalDbObject() throws UnsupportedEncodingException, Exception {
		
		CsvMapperBuilder<DbPartialFinalObject> builder = new CsvMapperBuilder<DbPartialFinalObject>(DbPartialFinalObject.class);
		addDbObjectFields(builder);
		
		CsvMapper<DbPartialFinalObject> mapper = builder.mapper();
		
		List<DbPartialFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvStream(), new ListHandler<DbPartialFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	public void addDbObjectFields(CsvMapperBuilder<?> builder) {
		builder.addMapping("id")
		.addMapping("name")
		.addMapping("email")
		.addMapping("creationTime")
		.addMapping("typeOrdinal")
		.addMapping("typeName");
	}
}
