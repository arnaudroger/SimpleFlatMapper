package org.sfm.jooq;

import org.jooq.Record;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Type;


public class JooqMapperBuilder<R extends Record, E> extends
		AbstractFieldMapperMapperBuilder<R, E, JooqFieldKey> {

	public JooqMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public JooqMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<E>getClassMeta(target), new JooqMappingContextFactoryBuilder<R>());
	}
	
	public JooqMapperBuilder(final ClassMeta<E> classMeta, MappingContextFactoryBuilder<R, JooqFieldKey> mappingContextFactoryBuilder) throws MapperBuildingException {
		super(Record.class, classMeta, new RecordGetterFactory<R>(), new RecordFieldMapperFactory<R>(new RecordGetterFactory<R>()), new IdentityFieldMapperColumnDefinitionProvider<JooqFieldKey, R>(), new DefaultPropertyNameMatcherFactory(), new RethrowMapperBuilderErrorHandler(), mappingContextFactoryBuilder);
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<R, ST, JooqFieldKey> newSubBuilder(Type type, ClassMeta<ST> classMeta, MappingContextFactoryBuilder<R, JooqFieldKey> mappingContextFactoryBuilder) {
		return new JooqMapperBuilder<R, ST>(classMeta, mappingContextFactoryBuilder);
	}

    public JooqMapperBuilder<R, E> addField(JooqFieldKey key) {
		super._addMapping(key, FieldMapperColumnDefinition.<JooqFieldKey, R>identity());
		return this;
	}
	
	@Override
	public Mapper<R, E> mapper() {
        Tuple2<FieldMapper<R, E>[], Instantiator<R, E>> constructorFieldMappersAndInstantiator = getConstructorFieldMappersAndInstantiator();
        return new MapperImpl<R, E>(fields(), constructorFieldMappersAndInstantiator.first(), constructorFieldMappersAndInstantiator.second(), mappingContextFactoryBuilder.newFactory());
	}

}
