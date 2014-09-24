package org.sfm.csv;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbFinalPrimitiveObject;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.utils.ListHandler;

public class CsvMapperImplPrimitiveTest {

	@Test
	public void testDbObject() throws Exception {
		
		CsvMapperBuilder<DbPrimitiveObjectWithSetter> builder = new CsvMapperBuilder<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class);
		
		addMapping(builder);

		CsvMapper<DbPrimitiveObjectWithSetter> mapper = builder.mapper();
		
		List<DbPrimitiveObjectWithSetter> list = mapper.forEach(dbObjectCsvStream(), new ListHandler<DbPrimitiveObjectWithSetter>()).getList();
		assertEquals(1, list.size());
		assertDbPrimitiveObject(list.get(0));

		list = mapper.forEach(dbObjectCsvReader(), new ListHandler<DbPrimitiveObjectWithSetter>()).getList();
		assertEquals(1, list.size());
		assertDbPrimitiveObject(list.get(0));

	}

	private void assertDbPrimitiveObject(DbPrimitiveObject object) {
		assertEquals(true,  object.ispBoolean());
		assertEquals((byte)12, object.getpByte());
		assertEquals((char)13, object.getpCharacter());
		assertEquals((short)345, object.getpShort());
		assertEquals((int)3452, object.getpInt());
		assertEquals((long)4533, object.getpLong());
		assertEquals((float)3.14f, object.getpFloat(), 0);
		assertEquals((double)3.14159, object.getpDouble(), 0);	
	}

	private void assertDbPrimitiveObject(DbFinalPrimitiveObject object) {
		assertEquals(true,  object.ispBoolean());
		assertEquals((byte)12, object.getpByte());
		assertEquals((char)13, object.getpCharacter());
		assertEquals((short)345, object.getpShort());
		assertEquals((int)3452, object.getpInt());
		assertEquals((long)4533, object.getpLong());
		assertEquals((float)3.14f, object.getpFloat(), 0);
		assertEquals((double)3.14159, object.getpDouble(), 0);	
	}
	
	private void addMapping(
			CsvMapperBuilder<?> builder) {
		builder.addMapping("pBoolean");
		builder.addMapping("pByte");
		builder.addMapping("pCharacter");
		builder.addMapping("pShort");
		builder.addMapping("pInt");
		builder.addMapping("pLong");
		builder.addMapping("pFloat");
		builder.addMapping("pDouble");
	}

	static String CONTENT = "true,12,13,345,3452,4533,3.14,3.14159";
	public static InputStream dbObjectCsvStream() throws UnsupportedEncodingException {
		InputStream sr = new ByteArrayInputStream(CONTENT.getBytes("UTF-8"));
		return sr;
	}
	
	public static Reader dbObjectCsvReader() throws UnsupportedEncodingException {
		Reader sr = new StringReader(CONTENT);
		return sr;
	}
	
	@Test
	public void testFinalDbObject() throws Exception {
		CsvMapperBuilder<DbFinalPrimitiveObject> builder = new CsvMapperBuilder<DbFinalPrimitiveObject>(DbFinalPrimitiveObject.class);
		
		addMapping(builder);

		CsvMapper<DbFinalPrimitiveObject> mapper = builder.mapper();
		
		List<DbFinalPrimitiveObject> list = mapper.forEach(dbObjectCsvStream(), new ListHandler<DbFinalPrimitiveObject>()).getList();
		assertEquals(1, list.size());
		assertDbPrimitiveObject(list.get(0));

		list = mapper.forEach(dbObjectCsvReader(), new ListHandler<DbFinalPrimitiveObject>()).getList();
		assertEquals(1, list.size());
		assertDbPrimitiveObject(list.get(0));
		
	}
}
