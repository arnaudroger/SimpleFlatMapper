package org.sfm.querydsl;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.FieldMapperFactory;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Type;

public final class QueryDslMapperBuilder<T>
	extends AbstractFieldMapperMapperBuilder<Tuple, T, TupleElementKey> {


	public QueryDslMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public QueryDslMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<T>getClassMeta(target), new QueryDslMappingContextFactoryBuilder());
	}
	
	public QueryDslMapperBuilder(final ClassMeta<T> classMeta, MappingContextFactoryBuilder<Tuple, TupleElementKey> parentBuilder) throws MapperBuildingException {
		super(Tuple.class, classMeta, new TupleGetterFactory(),
                new FieldMapperFactory<Tuple, TupleElementKey>(new TupleGetterFactory()),
                new IdentityFieldMapperColumnDefinitionProvider<TupleElementKey, Tuple>(),
                new DefaultPropertyNameMatcherFactory(), new RethrowMapperBuilderErrorHandler(), parentBuilder, false, AbstractFieldMapperMapperBuilder.NO_ASM_MAPPER_THRESHOLD);
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<Tuple, ST, TupleElementKey> newSubBuilder(
			Type type, ClassMeta<ST> classMeta, MappingContextFactoryBuilder<Tuple, TupleElementKey> mappingContextFactoryBuilder) {
		return new QueryDslMapperBuilder<ST>(classMeta, mappingContextFactoryBuilder);
	}

    public <E> QueryDslMapperBuilder<T> addMapping(Expression<?> expression, int i) {
		_addMapping(new TupleElementKey(expression, i), FieldMapperColumnDefinition.<TupleElementKey, Tuple>identity());
		return this;
	}

}