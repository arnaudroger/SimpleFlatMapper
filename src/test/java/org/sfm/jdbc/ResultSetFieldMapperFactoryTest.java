package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;

public class ResultSetFieldMapperFactoryTest {
	
	SetterFactory setterFactory = new SetterFactory(null);
	ResultSetFieldMapperFactory factory = new ResultSetFieldMapperFactory(setterFactory, new ResultSetGetterFactory());
	private FieldMapperErrorHandler<ColumnKey> errorHandler;

	@Test
	public void testPrimitiveFieldMapperWrontType() {
		Setter<DbObject, String> setter = setterFactory.getFieldSetter(DbObject.class, "name");
		
		factory.newFieldMapper(setter, new ColumnKey("id", 1), errorHandler, new RethrowMapperBuilderErrorHandler());
		factory.newFieldMapper(setter, new ColumnKey("col1"), errorHandler, new RethrowMapperBuilderErrorHandler());
	}


}
