package org.sfm.map.impl;

import org.sfm.map.*;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

import static org.sfm.utils.Asserts.requireNonNull;

public final class MapperConfig<S, K extends FieldKey<K>> {
    public static final int NO_ASM_MAPPER_THRESHOLD = 792; // see https://github.com/arnaudroger/SimpleFlatMapper/issues/152


    public static <S, K extends FieldKey<K>> MapperConfig<S, K> config() {
        return new MapperConfig<S, K>(
                new IdentityFieldMapperColumnDefinitionProvider<K, S>(),
                new DefaultPropertyNameMatcherFactory(),
                new RethrowMapperBuilderErrorHandler(),
                false,
                NO_ASM_MAPPER_THRESHOLD);
    }

    private final ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions;
    private final PropertyNameMatcherFactory propertyNameMatcherFactory;
    private final MapperBuilderErrorHandler mapperBuilderErrorHandler;
    private final boolean failOnAsm;
    private final int asmMapperNbFieldsLimit;


    private MapperConfig(
            ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions,
            PropertyNameMatcherFactory propertyNameMatcherFactory,
            MapperBuilderErrorHandler mapperBuilderErrorHandler,
            boolean failOnAsm,
            int asmMapperNbFieldsLimit) {
        this.columnDefinitions = columnDefinitions;
        this.propertyNameMatcherFactory = propertyNameMatcherFactory;
        this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
        this.failOnAsm = failOnAsm;
        this.asmMapperNbFieldsLimit = asmMapperNbFieldsLimit;
    }

    public ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions() {
        return columnDefinitions;
    }

    public PropertyNameMatcherFactory propertyNameMatcherFactory() {
        return propertyNameMatcherFactory;
    }

    public MapperBuilderErrorHandler mapperBuilderErrorHandler() {
        return mapperBuilderErrorHandler;
    }

    public boolean failOnAsm() {
        return failOnAsm;
    }

    public int asmMapperNbFieldsLimit() {
        return asmMapperNbFieldsLimit;
    }

    public MapperConfig<S, K> columnDefinitions(ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> columnDefinitions) {
        return new MapperConfig<S, K>(
            requireNonNull("columnDefinitions", columnDefinitions),
            propertyNameMatcherFactory,
            mapperBuilderErrorHandler,
            failOnAsm,
            asmMapperNbFieldsLimit
        );
    }

    public MapperConfig<S, K> propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
        return new MapperConfig<S, K>(
                columnDefinitions,
                requireNonNull("propertyNameMatcherFactory", propertyNameMatcherFactory),
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit
        );
    }

    public MapperConfig<S, K> mapperBuilderErrorHandler(MapperBuilderErrorHandler mapperBuilderErrorHandler) {
        return new MapperConfig<S, K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                requireNonNull("mapperBuilderErrorHandler", mapperBuilderErrorHandler),
                failOnAsm,
                asmMapperNbFieldsLimit
        );
    }

    public MapperConfig<S, K> failOnAsm(boolean failOnAsm) {
        return new MapperConfig<S, K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit
        );
    }

    public MapperConfig<S, K> asmMapperNbFieldsLimit(int asmMapperNbFieldsLimit) {
        return new MapperConfig<S, K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit
        );
    }
}