package org.simpleflatmapper.querydsl;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.ConstantSourceMapperBuilder;
import org.simpleflatmapper.map.mapper.DefaultConstantSourceMapperBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSource;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.lang.reflect.Type;

public final class QueryDslMapperBuilder<T> {
	public static final MapperSource<Tuple, TupleElementKey> FIELD_MAPPER_SOURCE =
			new MapperSourceImpl<Tuple, TupleElementKey>(Tuple.class, new ContextualGetterFactoryAdapter<Tuple, TupleElementKey>(new TupleGetterFactory()));
	private static final KeyFactory<TupleElementKey> KEY_FACTORY = new KeyFactory<TupleElementKey>() {
		@Override
		public TupleElementKey newKey(String name, int i) {
			return new TupleElementKey(name, i);
		}
	} ;

	private final ConstantSourceMapperBuilder<Tuple, T, TupleElementKey> constantSourceMapperBuilder;

	public QueryDslMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public QueryDslMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<T>getClassMeta(target), new QueryDslMappingContextFactoryBuilder(true));
	}
	
	public QueryDslMapperBuilder(final ClassMeta<T> classMeta, MappingContextFactoryBuilder<Tuple, TupleElementKey> parentBuilder) throws MapperBuildingException {
		constantSourceMapperBuilder =
				ConstantSourceMapperBuilder.<Tuple, T, TupleElementKey>newConstantSourceMapperBuilder(
						FIELD_MAPPER_SOURCE,
						classMeta,
						MapperConfig.<TupleElementKey, Tuple>fieldMapperConfig(),
						parentBuilder, KEY_FACTORY);
	}

    public <E> QueryDslMapperBuilder<T> addMapping(Expression<?> expression, int i) {
		constantSourceMapperBuilder.addMapping(new TupleElementKey(expression, i), FieldMapperColumnDefinition.<TupleElementKey>identity());
		return this;
	}

	public SourceMapper<Tuple, T> mapper() {
		return constantSourceMapperBuilder.mapper();
	}

	public MappingContextFactory<? super Tuple> contextFactory() {
		return constantSourceMapperBuilder.contextFactory();
	}
}