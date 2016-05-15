package org.sfm.jooq;

import org.jooq.BetweenAndStep;
import org.jooq.Binding;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.QuantifiedSelect;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.WindowIgnoreNullsStep;
import org.jooq.WindowPartitionByStep;
import org.jooq.impl.CustomField;
import org.jooq.impl.DefaultDataType;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.MapperConfig;
import org.sfm.map.mapper.FieldMapperMapperBuilder;
import org.sfm.map.mapper.KeyFactory;
import org.sfm.map.mapper.MapperSource;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.mapper.MapperSourceImpl;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;


public class JooqMapperBuilder<E> {

	public static final MapperSource<Record, JooqFieldKey> FIELD_MAPPER_SOURCE = new MapperSourceImpl<Record, JooqFieldKey>(Record.class, new RecordGetterFactory<Record>());
	private static final KeyFactory<JooqFieldKey> KEY_FACTORY = new KeyFactory<JooqFieldKey>() {
		@Override
		public JooqFieldKey newKey(String name, int i) {
			return new JooqFieldKey(new FakeField(name), i);
		}
	};

	private final FieldMapperMapperBuilder<Record, E, JooqFieldKey> fieldMapperMapperBuilder;

	public JooqMapperBuilder(final Type target) throws MapperBuildingException {
		this(target, ReflectionService.newInstance());
	}

	@SuppressWarnings("unchecked")
	public JooqMapperBuilder(final Type target, ReflectionService reflectService) throws MapperBuildingException {
		this(reflectService.<E>getClassMeta(target), new JooqMappingContextFactoryBuilder<Record>());
	}

	public JooqMapperBuilder(final ClassMeta<E> classMeta,
							 MappingContextFactoryBuilder<Record, JooqFieldKey> mappingContextFactoryBuilder) throws MapperBuildingException {
		this(classMeta, mappingContextFactoryBuilder, MapperConfig.<JooqFieldKey>fieldMapperConfig());
	}

	public JooqMapperBuilder(final ClassMeta<E> classMeta,
							 MappingContextFactoryBuilder<Record, JooqFieldKey> mappingContextFactoryBuilder,
							 MapperConfig<JooqFieldKey, FieldMapperColumnDefinition<JooqFieldKey>> mapperConfig) throws MapperBuildingException {
		fieldMapperMapperBuilder =
				new FieldMapperMapperBuilder<Record, E, JooqFieldKey>(
						FIELD_MAPPER_SOURCE,
						classMeta,
						mapperConfig,
						mappingContextFactoryBuilder,
						KEY_FACTORY);
	}

		public JooqMapperBuilder<E> addField(JooqFieldKey key) {
		fieldMapperMapperBuilder.addMapping(key, FieldMapperColumnDefinition.<JooqFieldKey>identity());
		return this;
	}

	public Mapper<Record, E> mapper() {
		return fieldMapperMapperBuilder.mapper();
	}

	private static class FakeField extends CustomField<Object> {

        protected FakeField(String name) {
            super(name, new DefaultDataType<Object>(SQLDialect.DEFAULT, Object.class, "varchar"));
        }
    }
}
