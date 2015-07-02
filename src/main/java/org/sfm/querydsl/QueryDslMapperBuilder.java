package org.sfm.querydsl;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import org.sfm.map.GetterFactory;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.impl.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;

public final class QueryDslMapperBuilder<T> {
	public static final MapperSource<Tuple, TupleElementKey> FIELD_MAPPER_SOURCE =
			new MapperSourceImpl<>(Tuple.class, new TupleGetterFactory());

	private final FieldMapperMapperBuilder<Tuple, T, TupleElementKey> fieldMapperMapperBuilder;

	public QueryDslMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public QueryDslMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<T>getClassMeta(target), new QueryDslMappingContextFactoryBuilder());
	}
	
	public QueryDslMapperBuilder(final ClassMeta<T> classMeta, MappingContextFactoryBuilder<Tuple, TupleElementKey> parentBuilder) throws MapperBuildingException {
		fieldMapperMapperBuilder =
				new FieldMapperMapperBuilder<Tuple, T, TupleElementKey>(
						FIELD_MAPPER_SOURCE,
						classMeta,
						MapperConfig.<Tuple, TupleElementKey>fieldMapperConfig(),
						parentBuilder);
	}

    public <E> QueryDslMapperBuilder<T> addMapping(Expression<?> expression, int i) {
		fieldMapperMapperBuilder.addMapping(new TupleElementKey(expression, i), FieldMapperColumnDefinition.<TupleElementKey, Tuple>identity());
		return this;
	}

	public Mapper<Tuple, T> mapper() {
		return fieldMapperMapperBuilder.mapper();
	}
}