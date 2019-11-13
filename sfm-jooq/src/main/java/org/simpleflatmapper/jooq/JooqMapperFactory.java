package org.simpleflatmapper.jooq;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.AbstractColumnNameDiscriminatorMapperFactory;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.util.Function;

import java.lang.reflect.Type;

public class JooqMapperFactory
        extends AbstractColumnNameDiscriminatorMapperFactory<JooqFieldKey, JooqMapperFactory, Record> {

    public static JooqMapperFactory newInstance() {
        return new JooqMapperFactory();
    }

    public static JooqMapperFactory newInstance(
            AbstractMapperFactory<JooqFieldKey, ?, Record> config) {
        return new JooqMapperFactory(config);
    }


    private JooqMapperFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<JooqFieldKey>(), FieldMapperColumnDefinition.<JooqFieldKey>identity(), new ContextualGetterFactoryAdapter<Record, JooqFieldKey>(new RecordGetterFactory()));
    }

    private JooqMapperFactory(AbstractMapperFactory<JooqFieldKey, ?, Record> config) {
        super(config);
    }

    private JooqMapperFactory(AbstractColumnDefinitionProvider<JooqFieldKey> columnDefinitions, FieldMapperColumnDefinition<JooqFieldKey> identity) {
        super(columnDefinitions, identity, new ContextualGetterFactoryAdapter<Record, JooqFieldKey>(new RecordGetterFactory()));
    }


    public SfmRecordMapperProvider newRecordMapperProvider() {
        return new SfmRecordMapperProvider(new Function<Type, MapperConfig<JooqFieldKey, Record>>() {
            @Override
            public MapperConfig<JooqFieldKey, Record> apply(Type type) {
                return mapperConfig(type);
            }
        }, getReflectionService());
    }

    //IFJAVA8_START
    public SfmRecordUnmapperProvider newRecordUnmapperProvider(final Configuration configuration) {
        return newRecordUnmapperProvider(new DSLContextProvider() {
            @Override
            public DSLContext provide() {
                return DSL.using(configuration);
            }
        });
    }

    public SfmRecordUnmapperProvider newRecordUnmapperProvider(DSLContextProvider dslContextProvider) {
        return new SfmRecordUnmapperProvider(new Function<Type, MapperConfig<JooqFieldKey, Record>>() {
            @Override
            public MapperConfig<JooqFieldKey, Record> apply(Type type) {
                return mapperConfig(type);
            }
        }, getReflectionService(), dslContextProvider);
    }
    //IFJAVA8_END

}
