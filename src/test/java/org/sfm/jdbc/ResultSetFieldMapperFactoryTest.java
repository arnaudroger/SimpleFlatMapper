package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.ResultSetFieldMapperFactory;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.PropertyMapping;
import org.sfm.map.impl.RethrowMapperBuilderErrorHandler;
import org.sfm.map.impl.fieldmapper.LongFieldMapper;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyMeta;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResultSetFieldMapperFactoryTest {
	
	SetterFactory setterFactory = new SetterFactory(null);
	ResultSetFieldMapperFactory factory = new ResultSetFieldMapperFactory(new ResultSetGetterFactory());
	private FieldMapperErrorHandler<JdbcColumnKey> errorHandler;

	@Test
	public void testPrimitiveField() {

		ClassMeta<DbObject> classMeta = ReflectionService.newInstance(true, false).getRootClassMeta(DbObject.class);
		PropertyMeta<DbObject, Long> id = classMeta.newPropertyFinder().<Long>findProperty(new DefaultPropertyNameMatcher("id"));

		FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> identity = FieldMapperColumnDefinition.identity();
		PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> propertyMapping = new PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>>(id, new JdbcColumnKey("id", 1), identity);
		FieldMapper<ResultSet, DbObject> fieldMapper = factory.newFieldMapper(
				propertyMapping, errorHandler, new RethrowMapperBuilderErrorHandler());
		
		assertTrue(fieldMapper instanceof LongFieldMapper);

		PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> propertyMapping1 = new PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>>(id, new JdbcColumnKey("id", 0), identity);
		fieldMapper = factory.newFieldMapper(propertyMapping1, errorHandler, new RethrowMapperBuilderErrorHandler());
		assertTrue(fieldMapper instanceof LongFieldMapper);

	}

	@Test
	public void testSqlTypeToJavaType() {
		assertEquals(String.class, factory.getTargetTypeFromSqlType(Types.LONGNVARCHAR));
		assertEquals(String.class, factory.getTargetTypeFromSqlType(Types.LONGVARCHAR));
		assertEquals(String.class, factory.getTargetTypeFromSqlType(Types.VARCHAR));
		assertEquals(String.class, factory.getTargetTypeFromSqlType(Types.NVARCHAR));
		assertEquals(String.class, factory.getTargetTypeFromSqlType(Types.CHAR));
		assertEquals(String.class, factory.getTargetTypeFromSqlType(Types.NCHAR));
		assertEquals(String.class, factory.getTargetTypeFromSqlType(Types.CLOB));
		assertEquals(String.class, factory.getTargetTypeFromSqlType(Types.NCLOB));

		assertEquals(Long.class, factory.getTargetTypeFromSqlType(Types.BIGINT));
		assertEquals(Integer.class, factory.getTargetTypeFromSqlType(Types.INTEGER));
		assertEquals(Short.class, factory.getTargetTypeFromSqlType(Types.SMALLINT));
		assertEquals(Byte.class, factory.getTargetTypeFromSqlType(Types.TINYINT));

		assertEquals(Float.class, factory.getTargetTypeFromSqlType(Types.FLOAT));
		assertEquals(Double.class, factory.getTargetTypeFromSqlType(Types.DOUBLE));

		assertEquals(Boolean.class, factory.getTargetTypeFromSqlType(Types.BOOLEAN));
		assertEquals(Date.class, factory.getTargetTypeFromSqlType(Types.DATE));
		assertEquals(Date.class, factory.getTargetTypeFromSqlType(Types.TIMESTAMP));

		assertEquals(BigDecimal.class, factory.getTargetTypeFromSqlType(Types.NUMERIC));



	}


}
