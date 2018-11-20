package org.simpleflatmapper.csv.test;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.tuple.Tuple2;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class CsvMapperFactoryDefaultValueTest {
	private CsvMapper<Tuple2<String, String>> csvMapper;
	private CsvMapper<MyObject> csvMapperWithGetterSetter;
	private CsvMapperFactory csvMapperFactory;

	@Before
	public void setUp() {
		csvMapperFactory = CsvMapperFactory
				.newInstance()
				.addColumnProperty("element1", new DefaultValueProperty<String>("defaultValue"))
				.addColumnProperty("element2", new DefaultValueProperty<Integer>(123))
				.failOnAsm(true);
		csvMapper = csvMapperFactory.newMapper(new TypeReference<Tuple2<String, String>>() { });
		csvMapperWithGetterSetter = csvMapperFactory.newMapper(MyObject.class);
	}

	@Test
	public void testDefaultValueOnUndefinedField() throws IOException {
		final Tuple2<String, String> value =
				csvMapper.iterator(CsvParser.reader("element0\nv0")).next();
		assertEquals("v0", value.getElement0());
		assertEquals("defaultValue", value.getElement1());
	}

	@Test
	public void testDefaultValueOnDefinedFieldWithNoValuePresent() throws IOException {
		final Iterator<Tuple2<String, String>> iterator =
				csvMapper.iterator(CsvParser.reader("element0,element1\nv10,v11\nv2"));

		Tuple2<String, String> value = iterator.next();
		assertEquals("v10", value.getElement0());
		assertEquals("v11", value.getElement1());

		value = iterator.next();
		assertEquals("v2", value.getElement0());
		assertEquals("defaultValue", value.getElement1());
	}


	@Test
	public void testDefaultValueOnUndefinedFieldWithGetterSetter() throws IOException {
		final MyObject value =
				csvMapperWithGetterSetter.iterator(CsvParser.reader("element0\nv0")).next();
		assertEquals("v0", value.getElement0());
		assertEquals("defaultValue", value.getElement1());
	}

	@Test
	public void testDefaultValueOnDefinedFieldWithNoValuePresentWithGetterSetter() throws IOException {
		final Iterator<MyObject> iterator =
				csvMapperWithGetterSetter.iterator(CsvParser.reader("element0,element1\nv10,v11\nv2"));

		MyObject value = iterator.next();
		assertEquals("v10", value.getElement0());
		assertEquals("v11", value.getElement1());

		value = iterator.next();
		assertEquals("v2", value.getElement0());
		assertEquals("defaultValue", value.getElement1());
	}

	public static class MyObject {
		private String element0;
		private String element1;

		public String getElement0() {
			return element0;
		}

		public void setElement0(String element0) {
			this.element0 = element0;
		}

		public String getElement1() {
			return element1;
		}

		public void setElement1(String element1) {
			this.element1 = element1;
		}
	}


	@Test
	public void testDefaultValueOnUndefinedFieldWithGetterSetterPrimitive() throws IOException {
		final MyObjectPrimitiveGS value =
				csvMapperFactory.newMapper(MyObjectPrimitiveGS.class).iterator(CsvParser.reader("element0\nv0")).next();
		assertEquals("v0", value.getElement0());
		assertEquals(123, value.getElement2());
	}

	@Test
	public void testDefaultValueOnUndefinedFieldWithConsPrimitive() throws IOException {
		final MyObjectPrimitiveC value =
				csvMapperFactory.newMapper(MyObjectPrimitiveC.class).iterator(CsvParser.reader("element0\nv0")).next();
		assertEquals("v0", value.getElement0());
		assertEquals(123, value.getElement2());
	}

	@Test
	public void testDefaultValueJodaDateTime() throws IOException {
		LocalDateTime localDateTime = LocalDateTime.now();
		final Tuple2<String, LocalDateTime> value =
				CsvMapperFactory
						.newInstance()
						.addColumnProperty("element1", new DefaultValueProperty<LocalDateTime>(localDateTime))
						.newMapper(new TypeReference<Tuple2<String, LocalDateTime>>() {
						})
						.iterator(CsvParser.reader("element0\nv0"))
						.next();
		assertEquals("v0", value.getElement0());
		assertEquals(localDateTime, value.getElement1());
	}


	public static class MyObjectPrimitiveGS {
		private String element0;
		private int element2;

		public String getElement0() {
			return element0;
		}

		public void setElement0(String element0) {
			this.element0 = element0;
		}

		public int getElement2() {
			return element2;
		}

		public void setElement2(int element2) {
			this.element2 = element2;
		}
	}

	public static class MyObjectPrimitiveC {
		private final String element0;
		private final int element2;

		public MyObjectPrimitiveC(String element0, int element2) {
			this.element0 = element0;
			this.element2 = element2;
		}

		public String getElement0() {
			return element0;
		}

		public int getElement2() {
			return element2;
		}
	}
}
