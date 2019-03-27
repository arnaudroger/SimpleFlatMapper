package org.simpleflatmapper.jooq;

import org.jooq.Context;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.CustomField;
import org.jooq.impl.DefaultDataType;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.ConstantSourceMapperBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSource;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.lang.reflect.Type;


public class JooqMapperBuilder<E> {

	public static final MapperSource<Record, JooqFieldKey> FIELD_MAPPER_SOURCE = new MapperSourceImpl<Record, JooqFieldKey>(Record.class, new ContextualGetterFactoryAdapter<Record, JooqFieldKey>(new RecordGetterFactory()));
	private static final KeyFactory<JooqFieldKey> KEY_FACTORY = new KeyFactory<JooqFieldKey>() {
		@Override
		public JooqFieldKey newKey(String name, int i) {
			return new JooqFieldKey(new FakeField(name), i);
		}
	};

	private final ConstantSourceMapperBuilder<Record, E, JooqFieldKey> constantSourceMapperBuilder;

	public JooqMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}

	@SuppressWarnings("unchecked")
	public JooqMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<E>getClassMeta(target), new JooqMappingContextFactoryBuilder<Record>(true));
	}

	public JooqMapperBuilder(final ClassMeta<E> classMeta,
							 MappingContextFactoryBuilder<Record, JooqFieldKey> mappingContextFactoryBuilder) throws MapperBuildingException {
		this(classMeta, mappingContextFactoryBuilder, MapperConfig.<JooqFieldKey, Record>fieldMapperConfig());
	}

	public JooqMapperBuilder(final ClassMeta<E> classMeta,
							 MappingContextFactoryBuilder<Record, JooqFieldKey> mappingContextFactoryBuilder,
							 MapperConfig<JooqFieldKey, Record> mapperConfig) throws MapperBuildingException {
		constantSourceMapperBuilder =
				ConstantSourceMapperBuilder.<Record, E, JooqFieldKey>newConstantSourceMapperBuilder(
						FIELD_MAPPER_SOURCE,
						classMeta,
						mapperConfig,
						mappingContextFactoryBuilder,
						KEY_FACTORY);
	}

		public JooqMapperBuilder<E> addField(JooqFieldKey key) {
		constantSourceMapperBuilder.addMapping(key, FieldMapperColumnDefinition.<JooqFieldKey>identity());
		return this;
	}

	public SourceMapper<Record, E> mapper() {
		return constantSourceMapperBuilder.mapper();
	}

	public MappingContextFactory<? super Record> contextFactory() {
		return constantSourceMapperBuilder.contextFactory();
	}

	private static class FakeField extends CustomField<Object> {

        protected FakeField(String name) {
            super(name, new DefaultDataType<Object>(SQLDialect.DEFAULT, Object.class, "varchar"));
        }

		public void accept(Context<?> context) {
			throw new UnsupportedOperationException("Fake field not supposed to be used in query generation");
		}
	}
}
