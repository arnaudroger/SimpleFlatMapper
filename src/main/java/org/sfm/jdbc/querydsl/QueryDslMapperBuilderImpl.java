package org.sfm.jdbc.querydsl;

import java.lang.reflect.Type;

import org.sfm.map.AbstractMapperBuilderImpl;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MapperImpl;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.PathType;

public final class QueryDslMapperBuilderImpl<T> 
	extends AbstractMapperBuilderImpl<Tuple, T, TupleElementKey<?>, Mapper<Tuple, T>, QueryDslMapperBuilder<T>> 
	implements QueryDslMapperBuilder<T> {


	public QueryDslMapperBuilderImpl(final Type target) throws MapperBuildingException {
		this(target, new ReflectionService());
	}
	
	@SuppressWarnings("unchecked")
	public QueryDslMapperBuilderImpl(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(target, (ClassMeta<T>) reflectService.getClassMeta(target));
	}
	
	public QueryDslMapperBuilderImpl(final Type target, final ClassMeta<T> classMeta) throws MapperBuildingException {
		super(target, Tuple.class, classMeta, new TupleGetterFactory(), new TupleFieldMapperFactory(new TupleGetterFactory()));
	}

	@Override
	public Mapper<Tuple, T> mapper() {
		return new MapperImpl<Tuple, T>(fields(), getInstantiator());
	}
	
	@Override
	protected QueryDslMapperBuilder<T> newMapperBuilder(Type type, ClassMeta<T> classMeta) {
		return new  QueryDslMapperBuilderImpl<T>(type, classMeta);
	}

	@Override
	public <E> QueryDslMapperBuilder<T> addMapping(Expression<E> expr, int i) {
		String propertyName = null;
		if  (expr instanceof Path<?>) {
			@SuppressWarnings("rawtypes")
			PathMetadata<?> metadata = ((Path) expr).getMetadata();
			if (metadata.getPathType() == PathType.PROPERTY) {
				propertyName = metadata.getExpression().toString();
			}
		} 
		
		if (propertyName == null) {
			throw new MapperBuildingException("Unsupported expression " + expr);
		}
		addMapping(propertyName, new TupleElementKey<E>(expr, i));
		return this;
	}
	
	@Override
	public <E> QueryDslMapperBuilder<T> addMapping(Expression<E> expr) {
		return addMapping(expr, -1);
	}


}