package org.sfm.querydsl;

import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.impl.*;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.util.List;

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
		super(Tuple.class, classMeta, new TupleGetterFactory(), new TupleFieldMapperFactory(new TupleGetterFactory()), new IdentityFieldMapperColumnDefinitionProvider<TupleElementKey, Tuple>(), new DefaultPropertyNameMatcherFactory(), new RethrowMapperBuilderErrorHandler());
	}

	@Override
	public Mapper<Tuple, T> mapper() {
        Tuple2<FieldMapper<Tuple, T>[], Instantiator<Tuple, T>> constructorFieldMappersAndInstantiator = getConstructorFieldMappersAndInstantiator();
        return new MapperImpl<Tuple, T>(fields(), constructorFieldMappersAndInstantiator.first(), constructorFieldMappersAndInstantiator.second());
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

    @Override
    protected Predicate<Tuple> nullChecker(final List<TupleElementKey> keys) {
        return new Predicate<Tuple>() {
            @Override
            public boolean test(Tuple r) {
                if (keys.isEmpty()) return false;
                for(TupleElementKey k : keys) {
                    if (r.get(k.getExpression()) != null) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
	

}