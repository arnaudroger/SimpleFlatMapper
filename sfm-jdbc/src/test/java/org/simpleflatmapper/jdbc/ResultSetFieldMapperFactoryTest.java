package org.simpleflatmapper.jdbc;

import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapperFactoryImpl;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapperFactory;
import org.simpleflatmapper.map.fieldmapper.LongFieldMapper;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyMeta;

import java.sql.ResultSet;

import static org.junit.Assert.assertTrue;

public class ResultSetFieldMapperFactoryTest {
	
	ConstantSourceFieldMapperFactory<ResultSet, JdbcColumnKey> factory = new ConstantSourceFieldMapperFactoryImpl<ResultSet, JdbcColumnKey>(new ResultSetGetterFactory());

	@Test
	public void testPrimitiveField() {

		ClassMeta<DbObject> classMeta = ReflectionService.newInstance(true, false).getClassMeta(DbObject.class);
		PropertyMeta<DbObject, Long> id = classMeta.newPropertyFinder().<Long>findProperty(new DefaultPropertyNameMatcher("id", 0, false, false));

		FieldMapperColumnDefinition<JdbcColumnKey> identity = FieldMapperColumnDefinition.identity();
		PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> propertyMapping = new PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>(id, new JdbcColumnKey("id", 1), identity);
		FieldMapper<ResultSet, DbObject> fieldMapper = factory.newFieldMapper(
				propertyMapping, null, new RethrowMapperBuilderErrorHandler());
		
		assertTrue(fieldMapper instanceof LongFieldMapper);

		PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> propertyMapping1 = new PropertyMapping<DbObject, Long, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>(id, new JdbcColumnKey("id", 0), identity);
		fieldMapper = factory.newFieldMapper(propertyMapping1, null, new RethrowMapperBuilderErrorHandler());
		assertTrue(fieldMapper instanceof LongFieldMapper);

	}
}
