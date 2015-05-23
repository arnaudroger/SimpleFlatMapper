package org.sfm.jooq;

import org.jooq.Record;
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


public class JooqMapperBuilder<E> extends
		AbstractFieldMapperMapperBuilder<Record, E, JooqFieldKey> {

	public JooqMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}

	@SuppressWarnings("unchecked")
	public JooqMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<E>getClassMeta(target), new JooqMappingContextFactoryBuilder<Record>());
	}

	public JooqMapperBuilder(final ClassMeta<E> classMeta, MappingContextFactoryBuilder<Record, JooqFieldKey> mappingContextFactoryBuilder) throws MapperBuildingException {
		super(
				Record.class,
				classMeta,
				new RecordGetterFactory<Record>(),
				new FieldMapperFactory<Record, JooqFieldKey>(
						new RecordGetterFactory<Record>()),
				new IdentityFieldMapperColumnDefinitionProvider<JooqFieldKey, Record>(),
				new DefaultPropertyNameMatcherFactory(),
				new RethrowMapperBuilderErrorHandler(),
				mappingContextFactoryBuilder,
				false,
				AbstractFieldMapperMapperBuilder.NO_ASM_MAPPER_THRESHOLD);
	}

	@Override
	protected <ST> AbstractFieldMapperMapperBuilder<Record, ST, JooqFieldKey> newSubBuilder(Type type, ClassMeta<ST> classMeta, MappingContextFactoryBuilder<Record, JooqFieldKey> mappingContextFactoryBuilder) {
		return new JooqMapperBuilder<ST>(classMeta, mappingContextFactoryBuilder);
	}

    public JooqMapperBuilder<E> addField(JooqFieldKey key) {
		super._addMapping(key, FieldMapperColumnDefinition.<JooqFieldKey, Record>identity());
		return this;
	}

}
