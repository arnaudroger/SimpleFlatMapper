package org.sfm.csv;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbListObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.utils.ListHandler;

public class DynamicCsvMapperImplTest {

	private static final String CSV = "id,name,email,creationTime,typeOrdinal,typeName\n"
			+ "1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4";
	@Test
	public void testDbObject() throws Exception {
		
		CsvMapper<DbObject> mapper = CsvMapperFactory.newInstance().newMapper(DbObject.class);
		
		
		List<DbObject> list = mapper.forEach(dbObjectCsvReader(), new ListHandler<DbObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}

	public static Reader dbObjectCsvReader() throws UnsupportedEncodingException {
		Reader sr = new StringReader(CSV);
		return sr;
	}
	
	@Test
	public void testFinalDbObject() throws Exception {
		CsvMapper<DbFinalObject> mapper = CsvMapperFactory.newInstance().newMapper(DbFinalObject.class);

		List<DbFinalObject> list = mapper.forEach(dbObjectCsvReader(), new ListHandler<DbFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
		
	}
	
	@Test
	public void testPartialFinalDbObject() throws Exception {
		CsvMapper<DbPartialFinalObject> mapper = CsvMapperFactory.newInstance().newMapper(DbPartialFinalObject.class);
		
		List<DbPartialFinalObject> list = mapper.forEach(dbObjectCsvReader(), new ListHandler<DbPartialFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
		
	}
	
	private static final String CSV_LIST = "id,objects_0_id,objects_0_name,objects_0_email,objects_0_creationTime,objects_0_typeOrdinal,objects_0_typeName\n"
			+ "1,1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4";
	@Test
	public void testDbListObject() throws Exception {
		
		CsvMapper<DbListObject> mapper = CsvMapperFactory.newInstance().newMapper(DbListObject.class);
		
		
		List<DbListObject> list = mapper.forEach(new StringReader(CSV_LIST), new ListHandler<DbListObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0).getObjects().get(0));

	}
}
