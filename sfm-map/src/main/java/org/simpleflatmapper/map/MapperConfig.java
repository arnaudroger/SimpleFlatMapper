package org.simpleflatmapper.map;

import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.error.RethrowFieldMapperErrorHandler;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.impl.IdentityFieldMapperColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.DefaultPropertyNameMatcherFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperConfig<K extends FieldKey<K>> {
    public static final int NO_ASM_MAPPER_THRESHOLD = 792; // see https://github.com/arnaudroger/SimpleFlatMapper/issues/152
    public static final int MAX_METHOD_SIZE = 128;


    public static <K extends FieldKey<K>> MapperConfig<K> fieldMapperConfig() {
        return new MapperConfig<K>(
                new IdentityFieldMapperColumnDefinitionProvider<K>(),
                DefaultPropertyNameMatcherFactory.DEFAULT,
                RethrowMapperBuilderErrorHandler.INSTANCE,
                false,
                NO_ASM_MAPPER_THRESHOLD,
                RethrowFieldMapperErrorHandler.INSTANCE,
                RethrowConsumerErrorHandler.INSTANCE, MAX_METHOD_SIZE, false, Collections.emptyList());
    }

    public static <K extends FieldKey<K>> MapperConfig<K> config(ColumnDefinitionProvider<K> columnDefinitionProvider) {
        return new MapperConfig<K>(
                columnDefinitionProvider,
                DefaultPropertyNameMatcherFactory.DEFAULT,
                RethrowMapperBuilderErrorHandler.INSTANCE,
                false,
                NO_ASM_MAPPER_THRESHOLD,
                RethrowFieldMapperErrorHandler.INSTANCE,
                RethrowConsumerErrorHandler.INSTANCE, MAX_METHOD_SIZE, false, Collections.emptyList());
    }

    private final ColumnDefinitionProvider<K> columnDefinitions;
    private final PropertyNameMatcherFactory propertyNameMatcherFactory;
    private final MapperBuilderErrorHandler mapperBuilderErrorHandler;
    private final boolean failOnAsm;
    private final int asmMapperNbFieldsLimit;
    private final FieldMapperErrorHandler<? super K> fieldMapperErrorHandler;
    private final ConsumerErrorHandler consumerErrorHandler;
    private final int maxMethodSize;
    private final boolean assumeInjectionModifiesValues;
    private final List<Discriminator<?, ?>> discriminators;


    private MapperConfig(
            ColumnDefinitionProvider<K> columnDefinitions,
            PropertyNameMatcherFactory propertyNameMatcherFactory,
            MapperBuilderErrorHandler mapperBuilderErrorHandler,
            boolean failOnAsm,
            int asmMapperNbFieldsLimit,
            FieldMapperErrorHandler<? super K> fieldMapperErrorHandler,
            ConsumerErrorHandler consumerErrorHandler,
            int maxMethodSize, boolean assumeInjectionModifiesValues, 
            List<Discriminator<?, ?>> discriminators) {
        this.columnDefinitions = columnDefinitions;
        this.propertyNameMatcherFactory = propertyNameMatcherFactory;
        this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
        this.failOnAsm = failOnAsm;
        this.asmMapperNbFieldsLimit = asmMapperNbFieldsLimit;
        this.fieldMapperErrorHandler = fieldMapperErrorHandler;
        this.consumerErrorHandler = consumerErrorHandler;
        this.maxMethodSize = maxMethodSize;
        this.assumeInjectionModifiesValues = assumeInjectionModifiesValues;
        this.discriminators = discriminators;
    }

    public ColumnDefinitionProvider<K> columnDefinitions() {
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
    
    public boolean assumeInjectionModifiesValues() {
        return assumeInjectionModifiesValues;
    }

    public int asmMapperNbFieldsLimit() {
        return asmMapperNbFieldsLimit;
    }

    public MapperConfig<K> columnDefinitions(ColumnDefinitionProvider<K> columnDefinitions) {
        requireNonNull("columnDefinitions", columnDefinitions);
        return
            new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                    consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }

    public MapperConfig<K> propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
        return new MapperConfig<K>(
                columnDefinitions,
                requireNonNull("propertyNameMatcherFactory", propertyNameMatcherFactory),
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }

    public MapperConfig<K> mapperBuilderErrorHandler(MapperBuilderErrorHandler mapperBuilderErrorHandler) {
        return new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                requireNonNull("mapperBuilderErrorHandler", mapperBuilderErrorHandler),
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }

    public MapperConfig<K> failOnAsm(boolean failOnAsm) {
        return new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }



    public MapperConfig<K> assumeInjectionModifiesValues(boolean assumeInjectionModifiesValues) {
        return new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }

    public MapperConfig<K> asmMapperNbFieldsLimit(int asmMapperNbFieldsLimit) {
        return new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }

    public MapperConfig<K> fieldMapperErrorHandler(FieldMapperErrorHandler<? super K> fieldMapperErrorHandler) {
        return new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }

    public MapperConfig<K> consumerErrorHandler(ConsumerErrorHandler consumerErrorHandler) {
        return new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }

    public ConsumerErrorHandler consumerErrorHandler() {
        return consumerErrorHandler;
    }

    @Deprecated
    public MapperConfig<K> rowHandlerErrorHandler(ConsumerErrorHandler rowHandlerErrorHandler) {
        return consumerErrorHandler(rowHandlerErrorHandler);
    }

    public ConsumerErrorHandler rowHandlerErrorHandler() {
        return consumerErrorHandler();
    }

    public boolean hasFieldMapperErrorHandler() {
        return fieldMapperErrorHandler != null
                && !(fieldMapperErrorHandler instanceof RethrowFieldMapperErrorHandler);
    }

    public FieldMapperErrorHandler<? super K> fieldMapperErrorHandler() {
        return fieldMapperErrorHandler;
    }

    public int maxMethodSize() {
        return maxMethodSize;
    }

    public MapperConfig<K> maxMethodSize(int maxMethodSize) {
        return new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize, assumeInjectionModifiesValues, discriminators);
    }

    public <ROW, T> MapperConfig<K> discriminator(Class<T> rootClass, DiscrimnatorCase<ROW, T>... cases) {
        return discriminator((Type)rootClass, cases);
    }
    public <ROW, T> MapperConfig<K> discriminator(Type rootClass, DiscrimnatorCase<ROW, T>... cases) {
        List<Discriminator<?, ?>> discriminators = new ArrayList<Discriminator<?, ?>>(this.discriminators);
        discriminators.add(new Discriminator<ROW, T>(rootClass, cases));
        return new MapperConfig<K>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize,
                assumeInjectionModifiesValues,
                discriminators);
    }

    public <S, T> Discriminator<S, T> getDiscriminator(ClassMeta<T> classMeta) {
        for(Discriminator<?, ?> d : discriminators) {
            if (TypeHelper.areEquals(classMeta.getType(), d.type)) {
                return (Discriminator<S, T>) d;
            }
        }
        return null;
    }


    public static final class DiscrimnatorCase<ROW, T> {
        public final Predicate<ROW> predicate;
        public final ClassMeta<? extends T> classMeta;

        public DiscrimnatorCase(Predicate<ROW> predicate, ClassMeta<? extends T> classMeta) {
            this.predicate = predicate;
            this.classMeta = classMeta;
        }
    }
    
    public static final class Discriminator<ROW, T> {
        public final Type type;
        public final DiscrimnatorCase<ROW, T>[] cases;

        public Discriminator(Type type, DiscrimnatorCase<ROW, T>[] cases) {
            this.type = type;
            this.cases = cases;
        }
    }

}