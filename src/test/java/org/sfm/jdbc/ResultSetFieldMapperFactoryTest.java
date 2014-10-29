package org.sfm.jdbc;
import static org.junit.Assert.*;

import java.sql.ResultSet;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;

public class ResultSetFieldMapperFactoryTest {
	
	SetterFactory setterFactory = new SetterFactory(null);
	ResultSetFieldMapperFactory factory = new ResultSetFieldMapperFactory(new ResultSetGetterFactory());
	private FieldMapperErrorHandler<JdbcColumnKey> errorHandler;

	@Test
	public void testPrimitiveField() {
		Setter<DbObject, Integer> setter = setterFactory.getFieldSetter(DbObject.class, "id");
		
		
		FieldMapper<ResultSet, DbObject> fieldMapper = factory.newFieldMapper(setter, new JdbcColumnKey("id", 1), errorHandler, new RethrowMapperBuilderErrorHandler());
		
		assertTrue(fieldMapper instanceof LongFieldMapper);

		fieldMapper = factory.newFieldMapper(setter, new JdbcColumnKey("id", 0), errorHandler, new RethrowMapperBuilderErrorHandler());
		assertTrue(fieldMapper instanceof LongFieldMapper);

	}


}
