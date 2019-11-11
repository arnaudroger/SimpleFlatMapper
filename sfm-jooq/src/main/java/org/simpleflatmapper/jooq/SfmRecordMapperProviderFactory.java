package org.simpleflatmapper.jooq;

import org.jooq.Configuration;
import org.jooq.Record;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.AbstractColumnNameDiscriminatorMapperFactory;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.util.Function;

import java.lang.reflect.Type;

/**
 * use JooqMapperFactory
 */
@Deprecated
public class SfmRecordMapperProviderFactory
        extends AbstractColumnNameDiscriminatorMapperFactory<JooqFieldKey, SfmRecordMapperProviderFactory, Record> {

    public static SfmRecordMapperProviderFactory newInstance() {
        return new SfmRecordMapperProviderFactory();
    }

    public static SfmRecordMapperProviderFactory newInstance(
            AbstractMapperFactory<JooqFieldKey, ?, Record> config) {
        return new SfmRecordMapperProviderFactory(config);
    }

    public SfmRecordMapperProviderFactory(AbstractMapperFactory<JooqFieldKey, ?, Record> config) {
        super(config);
    }

    public SfmRecordMapperProviderFactory(AbstractColumnDefinitionProvider<JooqFieldKey> columnDefinitions, FieldMapperColumnDefinition<JooqFieldKey> identity) {
        super(columnDefinitions, identity, new ContextualGetterFactoryAdapter<Record, JooqFieldKey>(new RecordGetterFactory()));
    }

    private SfmRecordMapperProviderFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<JooqFieldKey>(), FieldMapperColumnDefinition.<JooqFieldKey>identity(), new ContextualGetterFactoryAdapter<Record, JooqFieldKey>(new RecordGetterFactory()));
    }

    public SfmRecordMapperProvider newProvider() {
        return new SfmRecordMapperProvider(new Function<Type, MapperConfig<JooqFieldKey, Record>>() {
            @Override
            public MapperConfig<JooqFieldKey, Record> apply(Type type) {
                return mapperConfig(type);
            }
        }, getReflectionService());
    }


}
