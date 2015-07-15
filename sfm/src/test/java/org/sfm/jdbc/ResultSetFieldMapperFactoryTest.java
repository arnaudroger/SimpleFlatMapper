package org.sfm.jdbc;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.FieldMapper;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.PropertyMapping;
import org.sfm.map.impl.RethrowMapperBuilderErrorHandler;
import org.sfm.map.impl.fieldmapper.FieldMapperFactory;
import org.sfm.map.impl.fieldmapper.LongFieldMapper;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyMeta;

import java.sql.ResultSet;

import static org.junit.Assert.assertTrue;

public class ResultSetFieldMapperFactoryTest {
	
	FieldMapperFactory<ResultSet, JdbcColumnKey> factory = new FieldMapperFactory<ResultSet, JdbcColumnKey>(new ResultSetGetterFactory());

	@Test
	public void testPrimitiveField() {

		ClassMeta<DbObject> classMeta = ReflectionService.newInstance(true, false).getClassMeta(DbObject.class);
		PropertyMeta<DbObject, Long> id = classMeta.newPropertyFinder().<Long>findProperty(new DefaultPropertyNameMatcher("id", 0, false, false));

		FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> identity = FieldMapperColumnDefinition.identity();
		PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> propertyMapping = new PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>>(id, new JdbcColumnKey("id", 1), identity);
		FieldMapper<ResultSet, DbObject> fieldMapper = factory.newFieldMapper(
				propertyMapping, new RethrowMapperBuilderErrorHandler());
		
		assertTrue(fieldMapper instanceof LongFieldMapper);

		PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> propertyMapping1 = new PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>>(id, new JdbcColumnKey("id", 0), identity);
		fieldMapper = factory.newFieldMapper(propertyMapping1, new RethrowMapperBuilderErrorHandler());
		assertTrue(fieldMapper instanceof LongFieldMapper);

	}
}
