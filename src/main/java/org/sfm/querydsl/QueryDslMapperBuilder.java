package org.sfm.querydsl;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.impl.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.util.HashMap;

public final class QueryDslMapperBuilder<T> 
	extends AbstractFieldMapperMapperBuilder<Tuple, T, TupleElementKey> {


	public QueryDslMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public QueryDslMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<T>getRootClassMeta(target));
	}
	
	public QueryDslMapperBuilder(final ClassMeta<T> classMeta) throws MapperBuildingException {
		super(Tuple.class, classMeta, new TupleGetterFactory(), new TupleFieldMapperFactory(new TupleGetterFactory()), new HashMap<String, FieldMapperColumnDefinition<TupleElementKey, Tuple>>(), new DefaultPropertyNameMatcherFactory(), new RethrowMapperBuilderErrorHandler());
	}

	@Override
	public Mapper<Tuple, T> mapper() {
		return new MapperImpl<Tuple, T>(fields(), getInstantiator());
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<Tuple, ST, TupleElementKey> newSubBuilder(
			Type type, ClassMeta<ST> classMeta) {
		return new QueryDslMapperBuilder<ST>(classMeta);
	}

	public <E> QueryDslMapperBuilder<T> addMapping(Expression<?> expression, int i) {
		_addMapping(new TupleElementKey(expression, i), FieldMapperColumnDefinition.<TupleElementKey, Tuple>identity());
		return this;
	}
	

}