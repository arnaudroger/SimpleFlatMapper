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
		this(target, (ClassMeta<T>) reflectService.getRootClassMeta(target));
	}
	
	public QueryDslMapperBuilder(final Type target, final ClassMeta<T> classMeta) throws MapperBuildingException {
		super(target, Tuple.class, classMeta, new TupleGetterFactory(), new TupleFieldMapperFactory(new TupleGetterFactory()), new HashMap<String, FieldMapperColumnDefinition<TupleElementKey, Tuple>>(), new DefaultPropertyNameMatcherFactory(), new RethrowMapperBuilderErrorHandler());
	}

	@Override
	public Mapper<Tuple, T> mapper() {
		return new MapperImpl<Tuple, T>(fields(), getInstantiator());
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<Tuple, ST, TupleElementKey> newSubBuilder(
			Type type, ClassMeta<ST> classMeta) {
		return new QueryDslMapperBuilder<ST>(type, classMeta);
	}

	public <E> QueryDslMapperBuilder<T> addMapping(Expression<?> expression, int i) {
		addMapping(new TupleElementKey(expression, i), FieldMapperColumnDefinition.<TupleElementKey, Tuple>identity());
		return this;
	}
	

}