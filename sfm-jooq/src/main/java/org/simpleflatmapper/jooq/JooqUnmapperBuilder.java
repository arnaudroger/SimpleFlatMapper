package org.simpleflatmapper.jooq;

import org.jooq.Context;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.CustomField;
import org.jooq.impl.DefaultDataType;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSource;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.lang.reflect.Type;


public class JooqUnmapperBuilder<E, R extends Record> {


	private final RecordMapperBuilder<E, R> recordMapperBuilder;


	public JooqUnmapperBuilder(final ClassMeta<E> classMeta,
                               MapperConfig<JooqFieldKey, Record> mapperConfig, Class<R> recordClass) throws MapperBuildingException {
		recordMapperBuilder =
				new RecordMapperBuilder<E, R>(classMeta, mapperConfig, recordClass);
	}

	public JooqUnmapperBuilder<E, R> addField(JooqFieldKey key) {
		recordMapperBuilder.addColumn(key, FieldMapperColumnDefinition.<JooqFieldKey>identity());
		return this;
	}

	public ContextualSourceMapper<E, R> mapper() {
		return recordMapperBuilder.mapper();
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
