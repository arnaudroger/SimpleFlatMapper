package org.sfm.map.impl;

import org.sfm.map.*;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

import static org.sfm.utils.Asserts.requireNonNull;

public final class MapperConfig<K extends FieldKey<K>, CD extends ColumnDefinition<K, CD>> {
    public static final int NO_ASM_MAPPER_THRESHOLD = 792; // see https://github.com/arnaudroger/SimpleFlatMapper/issues/152
    public static final int MAX_METHOD_SIZE = 128;


    public static <S, K extends FieldKey<K>> MapperConfig<K, FieldMapperColumnDefinition<K, S>> fieldMapperConfig() {
        return new MapperConfig<K, FieldMapperColumnDefinition<K, S>>(
                new IdentityFieldMapperColumnDefinitionProvider<K, S>(),
                new DefaultPropertyNameMatcherFactory(),
                new RethrowMapperBuilderErrorHandler(),
                false,
                NO_ASM_MAPPER_THRESHOLD,
                new RethrowFieldMapperErrorHandler<K>(),
                new RethrowRowHandlerErrorHandler(), MAX_METHOD_SIZE);
    }

    public static <K extends FieldKey<K>, CD extends ColumnDefinition<K, CD>> MapperConfig<K, CD> config(ColumnDefinitionProvider<CD, K> columnDefinitionProvider) {
        return new MapperConfig<K, CD>(
                columnDefinitionProvider,
                new DefaultPropertyNameMatcherFactory(),
                new RethrowMapperBuilderErrorHandler(),
                false,
                NO_ASM_MAPPER_THRESHOLD,
                new RethrowFieldMapperErrorHandler<K>(),
                new RethrowRowHandlerErrorHandler(), MAX_METHOD_SIZE);
    }

    private final ColumnDefinitionProvider<CD, K> columnDefinitions;
    private final PropertyNameMatcherFactory propertyNameMatcherFactory;
    private final MapperBuilderErrorHandler mapperBuilderErrorHandler;
    private final boolean failOnAsm;
    private final int asmMapperNbFieldsLimit;
    private final FieldMapperErrorHandler<K> fieldMapperErrorHandler;
    private final RowHandlerErrorHandler rowHandlerErrorHandler;
    private final int maxMethodSize;


    private MapperConfig(
            ColumnDefinitionProvider<CD, K> columnDefinitions,
            PropertyNameMatcherFactory propertyNameMatcherFactory,
            MapperBuilderErrorHandler mapperBuilderErrorHandler,
            boolean failOnAsm,
            int asmMapperNbFieldsLimit,
            FieldMapperErrorHandler<K> fieldMapperErrorHandler,
            RowHandlerErrorHandler rowHandlerErrorHandler, int maxMethodSize) {
        this.columnDefinitions = columnDefinitions;
        this.propertyNameMatcherFactory = propertyNameMatcherFactory;
        this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
        this.failOnAsm = failOnAsm;
        this.asmMapperNbFieldsLimit = asmMapperNbFieldsLimit;
        this.fieldMapperErrorHandler = fieldMapperErrorHandler;
        this.rowHandlerErrorHandler = rowHandlerErrorHandler;
        this.maxMethodSize = maxMethodSize;
    }

    public ColumnDefinitionProvider<CD, K> columnDefinitions() {
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

    public <CD  extends ColumnDefinition<K, CD>> MapperConfig<K, CD> columnDefinitions(ColumnDefinitionProvider<CD, K> columnDefinitions) {
        requireNonNull("columnDefinitions", columnDefinitions);
        return
            new MapperConfig<K, CD>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                rowHandlerErrorHandler, maxMethodSize);
    }

    public MapperConfig<K, CD> propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
        return new MapperConfig<K, CD>(
                columnDefinitions,
                requireNonNull("propertyNameMatcherFactory", propertyNameMatcherFactory),
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                rowHandlerErrorHandler, maxMethodSize);
    }

    public MapperConfig<K, CD> mapperBuilderErrorHandler(MapperBuilderErrorHandler mapperBuilderErrorHandler) {
        return new MapperConfig<K, CD>(
                columnDefinitions,
                propertyNameMatcherFactory,
                requireNonNull("mapperBuilderErrorHandler", mapperBuilderErrorHandler),
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                rowHandlerErrorHandler, maxMethodSize);
    }

    public MapperConfig<K, CD> failOnAsm(boolean failOnAsm) {
        return new MapperConfig<K, CD>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                rowHandlerErrorHandler, maxMethodSize);
    }

    public MapperConfig<K, CD> asmMapperNbFieldsLimit(int asmMapperNbFieldsLimit) {
        return new MapperConfig<K, CD>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                rowHandlerErrorHandler, maxMethodSize);
    }

    public MapperConfig<K, CD> fieldMapperErrorHandler(FieldMapperErrorHandler<K> fieldMapperErrorHandler) {
        return new MapperConfig<K, CD>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                rowHandlerErrorHandler, maxMethodSize);
    }

    public MapperConfig<K,CD> rowHandlerErrorHandler(RowHandlerErrorHandler rowHandlerErrorHandler) {
        return new MapperConfig<K, CD>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                rowHandlerErrorHandler,
                maxMethodSize);
    }

    public RowHandlerErrorHandler rowHandlerErrorHandler() {
        return rowHandlerErrorHandler;
    }

    public boolean hasFieldMapperErrorHandler() {
        return fieldMapperErrorHandler != null
                && !(fieldMapperErrorHandler instanceof RethrowFieldMapperErrorHandler);
    }

    public FieldMapperErrorHandler<K> fieldMapperErrorHandler() {
        return fieldMapperErrorHandler;
    }

    public int maxMethodSize() {
        return maxMethodSize;
    }

    public MapperConfig<K,CD> maxMethodSize(int maxMethodSize) {
        return new MapperConfig<K, CD>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                rowHandlerErrorHandler,
                maxMethodSize);
    }


}