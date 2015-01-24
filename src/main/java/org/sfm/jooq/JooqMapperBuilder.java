package org.sfm.jooq;

import org.jooq.Record;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.impl.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.util.HashMap;


public class JooqMapperBuilder<R extends Record, E> extends
		AbstractFieldMapperMapperBuilder<R, E, JooqFieldKey> {

	public JooqMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public JooqMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(target, (ClassMeta<E>) reflectService.getRootClassMeta(target));
	}
	
	public JooqMapperBuilder(final Type target, final ClassMeta<E> classMeta) throws MapperBuildingException {
		super(target, Record.class, classMeta, new RecordGetterFactory<R>(), new RecordFieldMapperFactory<R>(new RecordGetterFactory<R>()), new HashMap<String, FieldMapperColumnDefinition<JooqFieldKey, R>>(), new DefaultPropertyNameMatcherFactory(), new RethrowMapperBuilderErrorHandler());
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<R, ST, JooqFieldKey> newSubBuilder(Type type, ClassMeta<ST> classMeta) {
		return new JooqMapperBuilder<R, ST>(type, classMeta);
	}
	
	public JooqMapperBuilder<R, E> addField(JooqFieldKey key) {
		super._addMapping(key, FieldMapperColumnDefinition.<JooqFieldKey, R>identity());
		return this;
	}
	
	@Override
	public Mapper<R, E> mapper() {
		return new MapperImpl<R, E>(fields(), getInstantiator());
	}

}
