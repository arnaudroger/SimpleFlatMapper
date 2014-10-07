package org.sfm.csv;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.map.MapperBuildingException;
import org.sfm.utils.ListHandler;

public class CsvMapperBuilderTest {
	private CsvMapperFactory csvMapperFactory;

	@Before
	public void setUp() {
		csvMapperFactory = new CsvMapperFactory();
	}
	
	@Test
	public void testMapDbObject() throws UnsupportedEncodingException, Exception {
		testMapDbObject(csvMapperFactory.newBuilder(DbObject.class));
	}
	
	@Test
	public void testMapDbObjectNoAsm() throws UnsupportedEncodingException, Exception {
		testMapDbObject(csvMapperFactory.disableAsm(true).newBuilder(DbObject.class));
	}

	private void testMapDbObject(CsvMapperBuilder<DbObject> builder)
			throws IOException, UnsupportedEncodingException, ParseException {
		addDbObjectFields(builder);
		CsvMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	
	@Test
	public void testMapDbObjectWithColumnIndex() throws UnsupportedEncodingException, Exception {
		
		CsvMapperBuilder<DbObject> builder = csvMapperFactory.newBuilder(DbObject.class);
		builder.addMapping("email", 2);
		CsvMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbObject>()).getList();
		assertEquals(1, list.size());
		
		DbObject o = list.get(0);
		assertEquals(0,  o.getId());
		assertNull(o.getName());
		assertEquals("name1@mail.com", o.getEmail());
		assertNull(o.getCreationTime());
		assertNull(o.getTypeName());
		assertNull(o.getTypeOrdinal());
	}
	
	@Test
	public void testMapFinalDbObject() throws UnsupportedEncodingException, Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.newBuilder(DbFinalObject.class);
		testMapFinalDbObject(builder);
	}
	@Test
	public void testMapFinalDbObjectNoAsm() throws UnsupportedEncodingException, Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.useAsm(false).newBuilder(DbFinalObject.class);
		testMapFinalDbObject(builder);
	}

	private void testMapFinalDbObject(CsvMapperBuilder<DbFinalObject> builder)
			throws IOException, UnsupportedEncodingException, ParseException {
		addDbObjectFields(builder);
		
		CsvMapper<DbFinalObject> mapper = builder.mapper();
		
		List<DbFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	@Test
	public void testMapPartialFinalDbObject() throws UnsupportedEncodingException, Exception {
		CsvMapperBuilder<DbPartialFinalObject> builder = csvMapperFactory.newBuilder(DbPartialFinalObject.class);
		testMapPartialFinalDbObject(builder);
	}
	
	@Test
	public void testMapPartialFinalDbObjectNoAsm() throws UnsupportedEncodingException, Exception {
		CsvMapperBuilder<DbPartialFinalObject> builder = csvMapperFactory.useAsm(false).newBuilder(DbPartialFinalObject.class);
		testMapPartialFinalDbObject(builder);
	}

	@Test
	public void testMapFinalDbObjectDisableAsm() throws UnsupportedEncodingException, Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.disableAsm(true).newBuilder(DbFinalObject.class);
		try {
			addDbObjectFields(builder);
			fail("Expect failure");
		} catch(MapperBuildingException e) {
			// expected
		}
	}
	
	@Test
	public void testMapDbObjectWrongName() throws UnsupportedEncodingException, Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.newBuilder(DbFinalObject.class);
		try {
			builder.addMapping("No_prop");
			fail("Expect failure");
		} catch(MapperBuildingException e) {
			// expected
		}
	}
	
	@Test
	public void testMapDbObjectAlias() throws UnsupportedEncodingException, Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.addAlias("no_prop", "id").newBuilder(DbFinalObject.class);
		builder.addMapping("No_prop");
	}


	private void testMapPartialFinalDbObject(
			CsvMapperBuilder<DbPartialFinalObject> builder) throws IOException,
			UnsupportedEncodingException, ParseException {
		addDbObjectFields(builder);
		
		CsvMapper<DbPartialFinalObject> mapper = builder.mapper();
		
		List<DbPartialFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbPartialFinalObject>()).getList();
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
