package org.sfm.jooq;

import org.jooq.Record;
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


public class JooqMapperBuilder<R extends Record, E> extends
		AbstractFieldMapperMapperBuilder<R, E, JooqFieldKey> {

	public JooqMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public JooqMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<E>getRootClassMeta(target));
	}
	
	public JooqMapperBuilder(final ClassMeta<E> classMeta) throws MapperBuildingException {
		super(Record.class, classMeta, new RecordGetterFactory<R>(), new RecordFieldMapperFactory<R>(new RecordGetterFactory<R>()), new IdentityFieldMapperColumnDefinitionProvider<JooqFieldKey, R>(), new DefaultPropertyNameMatcherFactory(), new RethrowMapperBuilderErrorHandler());
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<R, ST, JooqFieldKey> newSubBuilder(Type type, ClassMeta<ST> classMeta) {
		return new JooqMapperBuilder<R, ST>(classMeta);
	}

    @Override
    protected Predicate<R> nullChecker(final List<JooqFieldKey> keys) {
        return new Predicate<R>() {
            @Override
            public boolean test(R r) {
                if (keys.isEmpty()) return false;
                for(JooqFieldKey k : keys) {
                    if (r.getValue(k.getIndex()) != null) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public JooqMapperBuilder<R, E> addField(JooqFieldKey key) {
		super._addMapping(key, FieldMapperColumnDefinition.<JooqFieldKey, R>identity());
		return this;
	}
	
	@Override
	public Mapper<R, E> mapper() {
        Tuple2<FieldMapper<R, E>[], Instantiator<R, E>> constructorFieldMappersAndInstantiator = getConstructorFieldMappersAndInstantiator();
        return new MapperImpl<R, E>(fields(), constructorFieldMappersAndInstantiator.first(), constructorFieldMappersAndInstantiator.second());
	}

}
