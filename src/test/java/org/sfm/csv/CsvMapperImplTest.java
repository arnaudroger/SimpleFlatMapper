package org.sfm.csv;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.csv.parser.BytesCellHandler;
import org.sfm.jdbc.DbHelper;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.ListHandler;

public class CsvMapperImplTest {

	@Test
	public void testDbObject() throws Exception {
		InputStream sr = new ByteArrayInputStream("1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4".getBytes("UTF-8"));

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
		Instantiator<BytesCellHandler, DbObject> instantiator = new InstantiatorFactory(null).getInstantiator(BytesCellHandler.class, DbObject.class);
		CsvMapperImpl<DbObject> mapper = new CsvMapperImpl<DbObject>(instantiator , setters);
		
		List<DbObject> list = mapper.forEach(sr, new ListHandler<DbObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
		
	}

}
