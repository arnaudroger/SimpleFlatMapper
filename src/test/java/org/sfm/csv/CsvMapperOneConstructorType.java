package org.sfm.csv;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;

import org.junit.Test;
import org.sfm.map.MappingException;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.ListHandler;

public class CsvMapperOneConstructorType {

	public static class MyObject {
		SubObject prop;
	}
	public static class SubObject {
		private final String value;

		public SubObject(String value) {
			this.value = value;
		}
	}
	
	public static class MyObjectAmbiguity {
		SubObjectAmbiguity prop;
	}
	public static class SubObjectAmbiguity {
		@SuppressWarnings("unused")
		private final String value;

		public SubObjectAmbiguity(String value) {
			this.value = value;
		}
		
		public SubObjectAmbiguity(String value, int ivalue) {
			this.value = value;
		}
	}
	
	@Test
	public void testCanCreateTypeFromUnambiguousConstructor() throws Exception {
		CsvMapperBuilder<MyObject> builder = new CsvMapperBuilder<MyObject>(MyObject.class, new ReflectionService(false, false));
		testMatchConstructor(builder);
	}

	@Test
	public void testCantCreateTypeFromAmbiguousConstructor() throws Exception {

		CsvMapperBuilder<MyObjectAmbiguity> builder = new CsvMapperBuilder<MyObjectAmbiguity>(MyObjectAmbiguity.class, new ReflectionService(false, false));
		
		try {
			builder.addMapping("prop").mapper();
			fail("Cannot map with ambiguous constructor");
		} catch(Exception e) {
			// expected
		}
	}

	
	
	private void testMatchConstructor(CsvMapperBuilder<MyObject> builder)
			throws MappingException, IOException {
		builder.addMapping("prop");
		CsvMapper<MyObject> mapper = builder.mapper();
		
		assertEquals("value", mapper.forEach(new StringReader("value"), new ListHandler<MyObject>()).getList().get(0).prop.value);
	}
}
