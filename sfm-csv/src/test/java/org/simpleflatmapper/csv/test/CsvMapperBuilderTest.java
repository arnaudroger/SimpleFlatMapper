package org.simpleflatmapper.csv.test;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperBuilder;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.map.mapper.MissingPropertyException;
import org.simpleflatmapper.map.property.MandatoryProperty;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbPartialFinalObject;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ListCollector;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CsvMapperBuilderTest {
	private CsvMapperFactory csvMapperFactory;
	private CsvMapperFactory csvMapperFactoryNoAsm;
	private CsvMapperFactory csvMapperFactoryLowSharding;

	@Before
	public void setUp() {
		csvMapperFactory = CsvMapperFactory.newInstance().failOnAsm(true);
		csvMapperFactoryNoAsm = CsvMapperFactory.newInstance().useAsm(false);
		csvMapperFactoryLowSharding = CsvMapperFactory.newInstance().failOnAsm(true).maxMethodSize(2);
	}

    @Test
	public void testMapDbObject() throws Exception {
		testMapDbObject(csvMapperFactory.newBuilder(DbObject.class));
	}

	@Test
	public void testMapDbObjectSharding() throws Exception {
		testMapDbObject(csvMapperFactoryLowSharding.newBuilder(DbObject.class));
	}

	@Test
	public void testMapToMapStringString() throws Exception {
		final CsvMapper<Map<String, String>> mapper = csvMapperFactory.newBuilder(new TypeReference<Map<String, String>>() {
		}).addMapping("key1").addMapping("key2").mapper();

		final Iterator<Map<String, String>> iterator = mapper.iterator(new StringReader("v1,v2\na1"));
		Map<String, String> map;

		map = iterator.next();
		assertEquals("v1", map.get("key1"));
		assertEquals("v2", map.get("key2"));

		map = iterator.next();

		assertEquals("a1", map.get("key1"));
		assertEquals(null, map.get("key2"));

	}

	@Test
	public void testMapToMapStringInteger() throws Exception {
		final CsvMapperBuilder<Map<String, Integer>> builder = csvMapperFactory.newBuilder(new TypeReference<Map<String, Integer>>() {
		});
		final CsvMapper<Map<String, Integer>> mapper =
				builder
					.addMapping("key1")
					.addMapping("key2")
					.mapper();

		final Iterator<Map<String, Integer>> iterator = mapper.iterator(new StringReader("3,12\n4"));
		Map<String, Integer> map;

		map = iterator.next();
		assertEquals(Integer.valueOf(3), map.get("key1"));
		assertEquals(Integer.valueOf(12), map.get("key2"));

		map = iterator.next();

		assertEquals(Integer.valueOf(4), map.get("key1"));
		assertEquals(null, map.get("key2"));
	}

	@Test
	public void testMapToMapIntegerToInteger() throws Exception {
		final CsvMapperBuilder<Map<Integer, Integer>> builder = csvMapperFactory.newBuilder(new TypeReference<Map<Integer, Integer>>() {
		});
		final CsvMapper<Map<Integer, Integer>> mapper =
				builder
						.addMapping("1")
						.addMapping("2")
						.mapper();

		final Iterator<Map<Integer, Integer>> iterator = mapper.iterator(new StringReader("3,12"));
		Map<Integer, Integer> map;

		map = iterator.next();
		assertEquals(Integer.valueOf(3), map.get(1));
		assertEquals(Integer.valueOf(12), map.get(2));

	}

	@Test
	public void testMapToMapStringToDbObject() throws Exception {
		final CsvMapperBuilder<Map<String, DbObject>> builder = csvMapperFactory.newBuilder(new TypeReference<Map<String, DbObject>>() {
		});
		final CsvMapper<Map<String, DbObject>> mapper =
				builder
						.addMapping("key1_id", new Object[0])
						.addMapping("key1_name")
						.addMapping("key2_2_id")
						.addMapping("key2_2_name", 3, new Object[0])
						.mapper();

		final Iterator<Map<String, DbObject>> iterator = mapper.iterator(new StringReader("1,name1,2,name2"));
		Map<String, DbObject> map;

		map = iterator.next();

		DbObject o = map.get("key1");
		assertEquals(1, o.getId());
		assertEquals("name1", o.getName());

		o = map.get("key2_2");
		assertEquals(2, o.getId());
		assertEquals("name2", o.getName());

	}

	@Test
	public void testMapDbObjectNoAsm() throws Exception {
		testMapDbObject(csvMapperFactoryNoAsm.newBuilder(DbObject.class));
	}

	private void testMapDbObject(CsvMapperBuilder<DbObject> builder)
			throws IOException, ParseException {
		addDbObjectFields(builder);
		CsvMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListCollector<DbObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
    }
	
	@Test
	public void testMapDbObjectWithColumnIndex() throws Exception {
		
		CsvMapperBuilder<DbObject> builder = csvMapperFactory.newBuilder(DbObject.class);
		builder.addMapping("email", 2);
		CsvMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListCollector<DbObject>()).getList();
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
	public void testMapDbObjectWithWrongColumnStillMapGoodOne() throws Exception {
		
		CsvMapperBuilder<DbObject> builder = csvMapperFactory.mapperBuilderErrorHandler(MapperBuilderErrorHandler.NULL).newBuilder(DbObject.class);
		builder.addMapping("no_id");
		builder.addMapping("no_name");
		builder.addMapping("email");
		CsvMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListCollector<DbObject>()).getList();
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
	public void testMapFinalDbObject() throws Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.newBuilder(DbFinalObject.class);
		testMapFinalDbObject(builder);
	}

	@Test
	public void testMapFinalDbObjectSharding() throws Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactoryLowSharding.newBuilder(DbFinalObject.class);
		testMapFinalDbObject(builder);
	}

 	@Test
	public void testMapFinalDbObjectNoAsm() throws Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactoryNoAsm.newBuilder(DbFinalObject.class);
		testMapFinalDbObject(builder);
	}

	private void testMapFinalDbObject(CsvMapperBuilder<DbFinalObject> builder)
			throws IOException, ParseException {
		addDbObjectFields(builder);
		
		CsvMapper<DbFinalObject> mapper = builder.mapper();
		
		List<DbFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListCollector<DbFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}
	@Test
	public void testMapPartialFinalDbObject() throws Exception {
		CsvMapperBuilder<DbPartialFinalObject> builder = csvMapperFactory.newBuilder(DbPartialFinalObject.class);
		testMapPartialFinalDbObject(builder);
	}

	@Test
	public void testMapPartialFinalDbObjectNoAsm() throws Exception {
		CsvMapperBuilder<DbPartialFinalObject> builder = csvMapperFactoryNoAsm.newBuilder(DbPartialFinalObject.class);
		testMapPartialFinalDbObject(builder);
	}

	@Test
	public void testMapPartialFinalDbObjectLowSharding() throws Exception {
		CsvMapperBuilder<DbPartialFinalObject> builder = csvMapperFactoryLowSharding.newBuilder(DbPartialFinalObject.class);
		testMapPartialFinalDbObject(builder);
	}

	@Test
	public void testMapDbObjectWrongName() throws Exception {
		MapperBuilderErrorHandler mapperBuilderErrorHandler = mock(MapperBuilderErrorHandler.class);
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.mapperBuilderErrorHandler(mapperBuilderErrorHandler ).newBuilder(DbFinalObject.class);
		builder.addMapping("id");
		builder.addMapping("No_prop");
		verify(mapperBuilderErrorHandler).propertyNotFound(DbFinalObject.class, "No_prop");
	}
	
	@Test
	public void testMapDbObjectAlias() throws Exception {
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.addAlias("no_prop", "id").newBuilder(DbFinalObject.class);
		builder.addMapping("no_prop");
	}

	private void testMapPartialFinalDbObject(
			CsvMapperBuilder<DbPartialFinalObject> builder) throws IOException,
			 ParseException {
		addDbObjectFields(builder);
		
		CsvMapper<DbPartialFinalObject> mapper = builder.mapper();
		
		List<DbPartialFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListCollector<DbPartialFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}


    @Test
    public void testMapTypeReferenceDynamic() throws IOException {
        Tuple2<String, String> next = CsvMapperFactory
                .newInstance()
                .newMapper(new TypeReference<Tuple2<String, String>>() {
                })
                .iterator(new StringReader("e0,e1\nv0,v1")).next();
        assertEquals("v0", next.first());
        assertEquals("v1", next.second());
    }

    @Test
    public void testMapTypeReferenceBuild() throws IOException {
        Tuple2<String, String> next = CsvMapperFactory
                .newInstance()
                .newBuilder(new TypeReference<Tuple2<String, String>>() {
                }).addMapping("e0").addMapping("e1")
                .mapper()
                .iterator(new StringReader("v0,v1")).next();
        assertEquals("v0", next.first());
        assertEquals("v1", next.second());
    }
    
    
    @Test
    public void testMandatory() {
		try {
			CsvMapperFactory.newInstance().addColumnProperty("email", MandatoryProperty.DEFAULT).newBuilder(DbObject.class).addMapping("id").mapper();
			fail();
		} catch (MissingPropertyException e) {
			
		}
	}

	public static void addDbObjectFields(CsvMapperBuilder<?> builder) {
		builder
				.addMapping("id")
		.addMapping("name")
		.addMapping("email")
		.addMapping("creationTime")
				.addMapping("typeOrdinal")
		.addMapping("typeName");
	}


	@Test
	public void testFactoryMethodOnDifferentClass() throws IOException, NoSuchMethodException {
		final ClassMeta<IClass> classMeta = csvMapperFactory.getClassMetaWithExtraInstantiator(IClass.class, IClassFactory.class.getMethod("of", String.class));
		final IClass iClass = csvMapperFactory.newMapper(classMeta).iterator(new StringReader("value\nval")).next();
		assertEquals("val", iClass.value);
	}

	public static class IClass {
		private final String value;

		private IClass(String value) {
			this.value = value;
		}

	}

	public static class IClassFactory {
		public static IClass of(String value) {
			return new IClass(value);
		}
	}


}
