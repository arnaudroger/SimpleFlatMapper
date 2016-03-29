package org.sfm.csv;

import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.column.DefaultValueProperty;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.test.jdbc.DbHelper;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ListCollectorHandler;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CsvMapperFactoryDefaultValueTest {
	private CsvMapper<Tuple2<String, String>> csvMapper;

	@Before
	public void setUp() {
		CsvMapperFactory csvMapperFactory = CsvMapperFactory
				.newInstance()
				.addColumnProperty("element1", new DefaultValueProperty("defaultValue"))
				.failOnAsm(true);
		csvMapper = csvMapperFactory.newMapper(new TypeReference<Tuple2<String, String>>() { });
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
}
