package org.sfm.jooq;

import org.jooq.Record;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.FieldMapperFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;


public class JooqMapperBuilder<E> extends
		FieldMapperMapperBuilder<Record, E, JooqFieldKey> {

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
				FieldMapperMapperBuilder.NO_ASM_MAPPER_THRESHOLD);
	}

    public JooqMapperBuilder<E> addField(JooqFieldKey key) {
		super._addMapping(key, FieldMapperColumnDefinition.<JooqFieldKey, Record>identity());
		return this;
	}

}
