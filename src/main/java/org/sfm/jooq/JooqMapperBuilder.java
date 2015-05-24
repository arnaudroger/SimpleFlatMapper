package org.sfm.jooq;

import org.jooq.Record;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.FieldMapperFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;


public class JooqMapperBuilder<E>  {

	private final FieldMapperMapperBuilder<Record, E, JooqFieldKey> fieldMapperMapperBuilder;

	public JooqMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}

	@SuppressWarnings("unchecked")
	public JooqMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<E>getClassMeta(target), new JooqMappingContextFactoryBuilder<Record>());
	}

	public JooqMapperBuilder(final ClassMeta<E> classMeta, MappingContextFactoryBuilder<Record, JooqFieldKey> mappingContextFactoryBuilder) throws MapperBuildingException {
		fieldMapperMapperBuilder =
				new FieldMapperMapperBuilder<Record, E, JooqFieldKey>(
					Record.class,
					classMeta,
					new RecordGetterFactory<Record>(),
					new IdentityFieldMapperColumnDefinitionProvider<JooqFieldKey, Record>(),
					new DefaultPropertyNameMatcherFactory(),
					new RethrowMapperBuilderErrorHandler(),
					mappingContextFactoryBuilder,
					false,
					FieldMapperMapperBuilder.NO_ASM_MAPPER_THRESHOLD);
	}

    public JooqMapperBuilder<E> addField(JooqFieldKey key) {
		fieldMapperMapperBuilder.addMapping(key, FieldMapperColumnDefinition.<JooqFieldKey, Record>identity());
		return this;
	}

	public Mapper<Record, E> mapper() {
		return fieldMapperMapperBuilder.mapper();
	}
}
