package org.sfm.map.impl;

import org.sfm.map.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.Predicate;
import org.sfm.utils.UnaryFactory;
import org.sfm.utils.UnaryFactoryWithException;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * JdbcMapperFactory allows you to customise the mappers and create an instance of it using a fluent syntax.
 * <p>
 * JdbcMapperFactory is not Thread-Safe but the mappers are.
 * It is strongly advised to instantiate one jdbcMapper per class for the life of your application.
 * <p>
 * You can instantiate dynamic jdbcMapper which will use the ResultSetMetaData
 * to figure out the list of the columns or a static one using a builder.
 * <p>
 * <code>
 *     // create a dynamic jdbcMapper targeting MyClass<br>
 *     JdbcMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newMapper(MyClass.class);<br>
 *     <br>
 *     // create a static jdbcMapper targeting MyClass<br>
 *     JdbcMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newBuilder(MyClass.class)<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("id")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("field1")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("field2")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.mapper();<br>
 *     <br>
 * </code>
 *
 */
// I don't really like using inheritance but did not see any other way
// to avoid rewriting a lot of delegate method...
@SuppressWarnings("unchecked")
public abstract class AbstractSetRowMapperFactory<
		K extends FieldKey<K>,
		CD extends ColumnDefinition<K, CD>,
		R, S, E extends Exception,
		MF extends AbstractSetRowMapperFactory<K, CD, R, S, E, MF>>
	extends AbstractMapperFactory<K, CD, MF> {

	public AbstractSetRowMapperFactory(AbstractColumnDefinitionProvider<CD, K> columnDefinitions, CD identity) {
		super(columnDefinitions, identity);
	}

	/**
	 * Will create a DynamicMapper on the specified target class.
	 * @param target the class
	 * @param <T> the jdbcMapper target type
	 * @return the DynamicMapper
	 */
	public <T> SetRowMapper<R, S, T, E> mapTo(final Class<T> target) {
		return mapTo((Type) target);
	}

	/**
	 * Will create a DynamicMapper on the type specified by the TypeReference.
	 * @param target the TypeReference
	 * @param <T> the jdbcMapper target type
	 * @return the DynamicMapper
	 */
	public <T> SetRowMapper<R, S, T, E> mapTo(final TypeReference<T> target) {
		return mapTo(target.getType());
	}

	/**
	 * Will create a DynamicMapper on the specified type.
	 * @param target the type
	 * @param <T> the jdbcMapper target type
	 * @return the DynamicMapper
	 */
	public <T> SetRowMapper<R, S, T, E> mapTo(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);
		UnaryFactory<MapperKey<K>, SetRowMapper<R, S, T, E>> mapperFactory = newMapperFactory(classMeta);
		UnaryFactoryWithException<R, MapperKey<K>, E> keyFromRowFactory = newKeyFromRowFactory();
		UnaryFactoryWithException<S, MapperKey<K>, E> keyFromSetFactory = newKeyFromSetFactory();
		return newSetRowMapper(mapperFactory, keyFromRowFactory, keyFromSetFactory);
	}

	protected <T> SetRowMapper<R, S, T, E> newSetRowMapper(
			UnaryFactory<MapperKey<K>, SetRowMapper<R, S, T, E>> mapperFactory,
			UnaryFactoryWithException<R, MapperKey<K>, E> keyFromRowFactory,
			UnaryFactoryWithException<S, MapperKey<K>, E> keyFromSetFactory) {
		return new DynamicSetRowMapper<R, S, T, E, K>(mapperFactory, keyFromRowFactory, keyFromSetFactory);
	}

	protected abstract UnaryFactoryWithException<S,MapperKey<K>, E> newKeyFromSetFactory();

	protected abstract UnaryFactoryWithException<R,MapperKey<K>, E> newKeyFromRowFactory();

	protected abstract <T> UnaryFactory<MapperKey<K>,SetRowMapper<R,S,T,E>> newMapperFactory(ClassMeta<T> classMeta);
}
