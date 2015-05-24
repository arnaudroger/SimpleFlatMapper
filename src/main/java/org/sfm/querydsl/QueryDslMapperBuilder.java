package org.sfm.querydsl;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.FieldMapperFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;

public final class QueryDslMapperBuilder<T> {

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
						Tuple.class,
						classMeta,
						new TupleGetterFactory(),
                		new FieldMapperFactory<Tuple, TupleElementKey>(new TupleGetterFactory()),
                		new IdentityFieldMapperColumnDefinitionProvider<TupleElementKey, Tuple>(),
             		    new DefaultPropertyNameMatcherFactory(),
						new RethrowMapperBuilderErrorHandler(),
						parentBuilder,
						false,
						FieldMapperMapperBuilder.NO_ASM_MAPPER_THRESHOLD);
	}

    public <E> QueryDslMapperBuilder<T> addMapping(Expression<?> expression, int i) {
		fieldMapperMapperBuilder.addMapping(new TupleElementKey(expression, i), FieldMapperColumnDefinition.<TupleElementKey, Tuple>identity());
		return this;
	}

	public Mapper<Tuple, T> mapper() {
		return fieldMapperMapperBuilder.mapper();
	}
}