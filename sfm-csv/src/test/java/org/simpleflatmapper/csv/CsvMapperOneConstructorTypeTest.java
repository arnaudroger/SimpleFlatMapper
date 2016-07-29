package org.simpleflatmapper.csv;

import org.junit.Test;
import org.sfm.map.MappingException;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.ListCollectorHandler;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class CsvMapperOneConstructorTypeTest {

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
		
		public SubObjectAmbiguity(String value, int iValue) {
			this.value = value;
		}
	}
	
	@Test
	public void testCanCreateTypeFromUnambiguousConstructor() throws Exception {
		CsvMapperBuilder<MyObject> builder = new CsvMapperBuilder<MyObject>(MyObject.class, ReflectionService.newInstance(false, false));
		testMatchConstructor(builder);
	}
	
	private void testMatchConstructor(CsvMapperBuilder<MyObject> builder)
			throws MappingException, IOException {
		builder.addMapping("prop");
		CsvMapper<MyObject> mapper = builder.mapper();
		
		assertEquals("value", mapper.forEach(new StringReader("value"), new ListCollectorHandler<MyObject>()).getList().get(0).prop.value);
	}
}
