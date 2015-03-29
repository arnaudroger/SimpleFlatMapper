package org.sfm.csv;

import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.TypeReference;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ListHandler;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;

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
    public void testStaticMapperDbObjectToStringNoAsm() throws Exception {
        CsvMapperBuilder<DbObject> builder = csvMapperFactoryNoAsm.newBuilder(DbObject.class);
        addDbObjectFields(builder);
        assertEquals(
				"CsvMapperImpl{" +
						"targetSettersFactory=TargetSettersFactory{instantiator=StaticConstructorInstantiator{constructor=public org.sfm.beans.DbObject(), args=[]}}, " +
						"delayedCellSetters=[], " +
						"setters=[LongCellSetter{setter=LongMethodSetter{method=public void org.sfm.beans.DbObject.setId(long)}, reader=LongCellValueReaderImpl{}}, " +
						"CellSetterImpl{reader=StringCellValueReader{}, setter=MethodSetter{method=public void org.sfm.beans.DbObject.setName(java.lang.String)}}, " +
						"CellSetterImpl{reader=StringCellValueReader{}, setter=MethodSetter{method=public void org.sfm.beans.DbObject.setEmail(java.lang.String)}}, " +
						"CellSetterImpl{reader=DateCellValueReader{index=3, timeZone=Greenwich Mean Time, pattern='yyyy-MM-dd HH:mm:ss'}, setter=MethodSetter{method=public void org.sfm.beans.DbObject.setCreationTime(java.util.Date)}}, " +
						"CellSetterImpl{reader=EnumCellValueReader{enumClass=class org.sfm.beans.DbObject$Type}, setter=MethodSetter{method=public void org.sfm.beans.DbObject.setTypeOrdinal(org.sfm.beans.DbObject$Type)}}, " +
						"CellSetterImpl{reader=EnumCellValueReader{enumClass=class org.sfm.beans.DbObject$Type}, setter=MethodSetter{method=public void org.sfm.beans.DbObject.setTypeName(org.sfm.beans.DbObject$Type)}}]}", builder.mapper().toString());
    }

    @Test
    public void testStaticMapperDbFinalObjectToString() throws Exception {
        CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.useAsm(false).newBuilder(DbFinalObject.class);
        addDbObjectFields(builder);
        assertEquals(
                "CsvMapperImpl{" +
                        "targetSettersFactory=TargetSettersFactory{instantiator=InjectConstructorInstantiator{" +
                            "constructorDefinition=ConstructorDefinition{constructor=public org.sfm.beans.DbFinalObject(long,java.lang.String,java.lang.String,java.util.Date,org.sfm.beans.DbObject$Type,org.sfm.beans.DbObject$Type), " +
                            "parameters=[" +
                                "ConstructorParameter{name='id', type=long, resolvedType=long}, " +
                                "ConstructorParameter{name='name', type=class java.lang.String, resolvedType=class java.lang.String}, " +
                                "ConstructorParameter{name='email', type=class java.lang.String, resolvedType=class java.lang.String}, " +
                                "ConstructorParameter{name='creationTime', type=class java.util.Date, resolvedType=class java.util.Date}, " +
                                "ConstructorParameter{name='typeOrdinal', type=class org.sfm.beans.DbObject$Type, resolvedType=class org.sfm.beans.DbObject$Type}, " +
                                "ConstructorParameter{name='typeName', type=class org.sfm.beans.DbObject$Type, resolvedType=class org.sfm.beans.DbObject$Type}]}}}, " +
                        "delayedCellSetters=[" +
                        "LongDelayedCellSetterFactory{setter=null, reader=LongCellValueReaderImpl{}}, " +
                        "DelayedCellSetterFactoryImpl{reader=StringCellValueReader{}, setter=null}, " +
                        "DelayedCellSetterFactoryImpl{reader=StringCellValueReader{}, setter=null}, " +
                        "DelayedCellSetterFactoryImpl{reader=DateCellValueReader{index=3, timeZone=Greenwich Mean Time, pattern='yyyy-MM-dd HH:mm:ss'}, setter=null}, " +
                        "DelayedCellSetterFactoryImpl{reader=EnumCellValueReader{enumClass=class org.sfm.beans.DbObject$Type}, setter=null}, " +
                        "DelayedCellSetterFactoryImpl{reader=EnumCellValueReader{enumClass=class org.sfm.beans.DbObject$Type}, setter=null}], setters=[null]}", builder.mapper().toString());
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
	public void testMapDbObjectNoAsm() throws Exception {
		testMapDbObject(csvMapperFactoryNoAsm.newBuilder(DbObject.class));
	}

	private void testMapDbObject(CsvMapperBuilder<DbObject> builder)
			throws IOException, ParseException {
		addDbObjectFields(builder);
		CsvMapper<DbObject> mapper = builder.mapper();
		
		List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
    }
	
	@Test
	public void testMapDbObjectWithColumnIndex() throws Exception {
		
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
	public void testMapDbObjectWithWrongColumnStillMapGoodOne() throws Exception {
		
		CsvMapperBuilder<DbObject> builder = csvMapperFactory.mapperBuilderErrorHandler(MapperBuilderErrorHandler.NULL).newBuilder(DbObject.class);
		builder.addMapping("no_id");
		builder.addMapping("no_name");
		builder.addMapping("email");
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
		
		List<DbFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbFinalObject>()).getList();
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
	public void testMapFinalDbObjectDisableAsm() throws Exception {
		CsvMapperBuilder<DbFinalObject> builder = CsvMapperFactory.newInstance().disableAsm(true).newBuilder(DbFinalObject.class);
		try {
			addDbObjectFields(builder);
			fail("Expect failure");
		} catch(MapperBuildingException e) {
			// expected
		}
	}
	
	@Test
	public void testMapDbObjectWrongName() throws Exception {
		MapperBuilderErrorHandler mapperBuilderErrorHandler = mock(MapperBuilderErrorHandler.class);
		CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.mapperBuilderErrorHandler(mapperBuilderErrorHandler ).newBuilder(DbFinalObject.class);
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
		
		List<DbPartialFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbPartialFinalObject>()).getList();
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
	
	public static void addDbObjectFields(CsvMapperBuilder<?> builder) {
		builder.addMapping("id")
		.addMapping("name")
		.addMapping("email")
		.addMapping("creationTime", CsvColumnDefinition.timeZoneDefinition(TimeZone.getTimeZone("Europe/London")))
		.addMapping("typeOrdinal")
		.addMapping("typeName");
	}
}
