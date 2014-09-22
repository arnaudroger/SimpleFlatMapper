package org.sfm.csv;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.reflect.asm.ConstructorParameter;
import org.sfm.utils.ListHandler;

public class CsvMapperImplTest {

	@Test
	public void testDbObject() throws Exception {
		InputStream sr = dbObjectCsvStream();

		CellSetterFactory cellSetterFactory = new CellSetterFactory();
		SetterFactory setterFactory = new SetterFactory(null);
		
		@SuppressWarnings("unchecked")
		CellSetter<DbObject>[] setters = new CellSetter[] {
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbObject.class, "id")),
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbObject.class, "name")),
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbObject.class, "email")),
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbObject.class, "creationTime")),
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbObject.class, "typeOrdinal")),
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbObject.class, "typeName"))
		};
		@SuppressWarnings("rawtypes")
		Instantiator<DelayedSetter[], DbObject> instantiator = new InstantiatorFactory(null).getInstantiator(DelayedSetter[].class, DbObject.class);
		@SuppressWarnings("unchecked")
		CsvMapperImpl<DbObject> mapper = new CsvMapperImpl<DbObject>(instantiator ,
				(DelayedCellSetter<DbObject, ?>[])new DelayedCellSetter[] {} 
				, setters);
		
		List<DbObject> list = mapper.forEach(sr, new ListHandler<DbObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
		
	}

	public static InputStream dbObjectCsvStream() throws UnsupportedEncodingException {
		InputStream sr = new ByteArrayInputStream("1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4".getBytes("UTF-8"));
		return sr;
	}
	
	@Test
	public void testFinalDbObject() throws Exception {
		InputStream sr = dbObjectCsvStream();

		CellSetterFactory cellSetterFactory = new CellSetterFactory();

		@SuppressWarnings("unchecked")
		DelayedCellSetter<DbFinalObject, ?>[] delayedSetters = new DelayedCellSetter[] {
			cellSetterFactory.getDelayedCellSetter(int.class),
			cellSetterFactory.getDelayedCellSetter(String.class),
			cellSetterFactory.getDelayedCellSetter(String.class),
			cellSetterFactory.getDelayedCellSetter(Date.class),
			cellSetterFactory.getDelayedCellSetter(DbObject.Type.class),
			cellSetterFactory.getDelayedCellSetter(DbObject.Type.class)
		};
		@SuppressWarnings("unchecked")
		CellSetter<DbFinalObject>[] setters = new CellSetter[] {
		};
		final List<ConstructorDefinition<DbFinalObject>> constructorsDefinition = ConstructorDefinition.extractConstructors(DbFinalObject.class);
		@SuppressWarnings({ "rawtypes", "serial" })
		Instantiator<DelayedSetter[], DbFinalObject> instantiator = 
			new InstantiatorFactory(null)
				.getInstantiator(DelayedSetter[].class, constructorsDefinition,
						new HashMap<ConstructorParameter, Getter<DelayedSetter[], ?>>() {{
							put(new ConstructorParameter("id", long.class), new DelayedGetter<Long>(0));
							put(new ConstructorParameter("name", String.class), new DelayedGetter<String>(1));
							put(new ConstructorParameter("email", String.class), new DelayedGetter<String>(2));
							put(new ConstructorParameter("creationTime", Date.class), new DelayedGetter<Date>(3));
							put(new ConstructorParameter("typeOrdinal", DbObject.Type.class), new DelayedGetter<DbObject.Type>(4));
							put(new ConstructorParameter("typeName", DbObject.Type.class), new DelayedGetter<DbObject.Type>(5));
						}}
						);
		
		CsvMapperImpl<DbFinalObject> mapper = new CsvMapperImpl<DbFinalObject>(instantiator ,
				delayedSetters, 
				setters);
		
		List<DbFinalObject> list = mapper.forEach(sr, new ListHandler<DbFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
		
	}
	@Test
	public void testPartialFinalDbObject() throws Exception {
		InputStream sr = dbObjectCsvStream();

		CellSetterFactory cellSetterFactory = new CellSetterFactory();
		SetterFactory setterFactory = new SetterFactory(null);
		
		@SuppressWarnings("unchecked")
		DelayedCellSetter<DbPartialFinalObject, ?>[] delayedSetters = new DelayedCellSetter[] {
			cellSetterFactory.getDelayedCellSetter(int.class),
			cellSetterFactory.getDelayedCellSetter(setterFactory.getSetter(DbPartialFinalObject.class, "name")),
			cellSetterFactory.getDelayedCellSetter(String.class),
		};
		@SuppressWarnings("unchecked")
		CellSetter<DbPartialFinalObject>[] setters = new CellSetter[] {
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbPartialFinalObject.class, "creationTime")),
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbPartialFinalObject.class, "typeOrdinal")),
			cellSetterFactory.getCellSetter(setterFactory.getSetter(DbPartialFinalObject.class, "typeName"))
		};
		final List<ConstructorDefinition<DbPartialFinalObject>> constructorsDefinition = ConstructorDefinition.extractConstructors(DbPartialFinalObject.class);
		@SuppressWarnings({ "rawtypes", "serial" })
		Instantiator<DelayedSetter[], DbPartialFinalObject> instantiator = 
			new InstantiatorFactory(null)
				.getInstantiator(DelayedSetter[].class, constructorsDefinition,
						new HashMap<ConstructorParameter, Getter<DelayedSetter[], ?>>() {{
							put(new ConstructorParameter("id", long.class), new DelayedGetter<Long>(0));
							put(new ConstructorParameter("email", String.class), new DelayedGetter<String>(2));
						}}
						);
		
		CsvMapperImpl<DbPartialFinalObject> mapper = new CsvMapperImpl<DbPartialFinalObject>(instantiator ,
				delayedSetters, 
				setters);
		
		List<DbPartialFinalObject> list = mapper.forEach(sr, new ListHandler<DbPartialFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
		
	}
}
