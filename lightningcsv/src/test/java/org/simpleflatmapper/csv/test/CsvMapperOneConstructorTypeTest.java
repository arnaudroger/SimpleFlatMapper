package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperBuilder;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.ListCollector;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class CsvMapperOneConstructorTypeTest {

	public static class MyObject {
		public SubObject prop;
	}
	public static class SubObject {
		private final String value;

		public SubObject(String value) {
			this.value = value;
		}
	}
	
	public static class MyObjectAmbiguity {
		public SubObjectAmbiguity prop;
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
		CsvMapperBuilder<MyObject> builder = new CsvMapperBuilder<MyObject>(MyObject.class, ReflectionService.newInstance(false));
		testMatchConstructor(builder);
	}
	
	private void testMatchConstructor(CsvMapperBuilder<MyObject> builder)
			throws MappingException, IOException {
		builder.addMapping("prop");
		CsvMapper<MyObject> mapper = builder.mapper();
		
		assertEquals("value", mapper.forEach(new StringReader("value"), new ListCollector<MyObject>()).getList().get(0).prop.value);
	}
}
