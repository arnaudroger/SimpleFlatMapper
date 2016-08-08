package org.simpleflatmapper.jooq;

import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;

public class SfmRecordMapperProviderFactory
        extends AbstractMapperFactory<JooqFieldKey, FieldMapperColumnDefinition<JooqFieldKey>, SfmRecordMapperProviderFactory> {


    public static SfmRecordMapperProviderFactory newInstance() {
        return new SfmRecordMapperProviderFactory();
    }

    public static SfmRecordMapperProviderFactory newInstance(
            AbstractMapperFactory<JooqFieldKey, FieldMapperColumnDefinition<JooqFieldKey>, ?> config) {
        return new SfmRecordMapperProviderFactory(config);
    }

    public SfmRecordMapperProviderFactory(AbstractMapperFactory<JooqFieldKey, FieldMapperColumnDefinition<JooqFieldKey>, ?> config) {
        super(config);
    }

    public SfmRecordMapperProviderFactory(AbstractColumnDefinitionProvider<FieldMapperColumnDefinition<JooqFieldKey>, JooqFieldKey> columnDefinitions, FieldMapperColumnDefinition<JooqFieldKey> identity) {
        super(columnDefinitions, identity);
    }

    private SfmRecordMapperProviderFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<JooqFieldKey>(), FieldMapperColumnDefinition.<JooqFieldKey>identity());
    }

    public SfmRecordMapperProvider newProvider() {
        return new SfmRecordMapperProvider(mapperConfig(), getReflectionService());
    }

}
