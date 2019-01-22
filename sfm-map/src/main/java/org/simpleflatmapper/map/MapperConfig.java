package org.simpleflatmapper.map;

import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.error.RethrowFieldMapperErrorHandler;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.impl.IdentityFieldMapperColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.DefaultPropertyNameMatcherFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.PredicatedEnumerable;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class MapperConfig<K extends FieldKey<K>, S> {
    public static final int NO_ASM_MAPPER_THRESHOLD = 792; // see https://github.com/arnaudroger/SimpleFlatMapper/issues/152
    public static final int MAX_METHOD_SIZE = 128;


    public static <K extends FieldKey<K>, S> MapperConfig<K, S> fieldMapperConfig() {
        return new MapperConfig<K, S>(
                new IdentityFieldMapperColumnDefinitionProvider<K>(),
                DefaultPropertyNameMatcherFactory.DEFAULT,
                RethrowMapperBuilderErrorHandler.INSTANCE,
                false,
                NO_ASM_MAPPER_THRESHOLD,
                RethrowFieldMapperErrorHandler.INSTANCE,
                RethrowConsumerErrorHandler.INSTANCE, MAX_METHOD_SIZE, 
                false, Collections.<Discriminator<S, ?>>emptyList(), null, false);
    }

    public static <K extends FieldKey<K>, S> MapperConfig<K, S> config(ColumnDefinitionProvider<K> columnDefinitionProvider) {
        return new MapperConfig<K, S>(
                columnDefinitionProvider,
                DefaultPropertyNameMatcherFactory.DEFAULT,
                RethrowMapperBuilderErrorHandler.INSTANCE,
                false,
                NO_ASM_MAPPER_THRESHOLD,
                RethrowFieldMapperErrorHandler.INSTANCE,
                RethrowConsumerErrorHandler.INSTANCE, MAX_METHOD_SIZE, false, Collections.<Discriminator<S, ?>>emptyList(), null, false);
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
    private final List<Discriminator<S, ?>> discriminators;
    private final Predicate<? super S> rowFilter;
    
    private final boolean unorderedJoin;

    private MapperConfig(
            ColumnDefinitionProvider<K> columnDefinitions,
            PropertyNameMatcherFactory propertyNameMatcherFactory,
            MapperBuilderErrorHandler mapperBuilderErrorHandler,
            boolean failOnAsm,
            int asmMapperNbFieldsLimit,
            FieldMapperErrorHandler<? super K> fieldMapperErrorHandler,
            ConsumerErrorHandler consumerErrorHandler,
            int maxMethodSize, boolean assumeInjectionModifiesValues,
            List<Discriminator<S, ?>> discriminators, Predicate<? super S> rowFilter, boolean unorderedJoin) {
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
        this.rowFilter = rowFilter;
        this.unorderedJoin = unorderedJoin;
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

    public MapperConfig<K, S> columnDefinitions(ColumnDefinitionProvider<K> columnDefinitions) {
        requireNonNull("columnDefinitions", columnDefinitions);
        return
            new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                    consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }

    public MapperConfig<K, S> propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                requireNonNull("propertyNameMatcherFactory", propertyNameMatcherFactory),
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }

    public MapperConfig<K, S> mapperBuilderErrorHandler(MapperBuilderErrorHandler mapperBuilderErrorHandler) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                requireNonNull("mapperBuilderErrorHandler", mapperBuilderErrorHandler),
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }

    public MapperConfig<K, S> failOnAsm(boolean failOnAsm) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }



    public MapperConfig<K, S> assumeInjectionModifiesValues(boolean assumeInjectionModifiesValues) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }

    public MapperConfig<K, S> asmMapperNbFieldsLimit(int asmMapperNbFieldsLimit) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }

    public MapperConfig<K, S> fieldMapperErrorHandler(FieldMapperErrorHandler<? super K> fieldMapperErrorHandler) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler, maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }

    public MapperConfig<K, S> consumerErrorHandler(ConsumerErrorHandler consumerErrorHandler) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }

    public MapperConfig<K, S> rowFilter(Predicate<? super S> rowFilter) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }
    
    public MapperConfig<K, S> unorderedJoin(boolean unorderedJoin) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }
    
    public boolean unorderedJoin() {
        return unorderedJoin;
    }

    public ConsumerErrorHandler consumerErrorHandler() {
        return consumerErrorHandler;
    }

    @Deprecated
    public MapperConfig<K, S> rowHandlerErrorHandler(ConsumerErrorHandler rowHandlerErrorHandler) {
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

    public MapperConfig<K, S> maxMethodSize(int maxMethodSize) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize, assumeInjectionModifiesValues, discriminators, rowFilter, unorderedJoin);
    }

    public <T> MapperConfig<K, S> discriminator(Class<T> rootClass, DiscriminatorCase<S, T>... cases) {
        return discriminator((Type)rootClass, cases);
    }
    public <T> MapperConfig<K, S> discriminator(Type rootClass, DiscriminatorCase<S, T>... cases) {
        List<Discriminator<S, ?>> discriminators = new ArrayList<Discriminator<S, ?>>(this.discriminators);
        discriminators.add(new Discriminator<S, T>(rootClass, cases));
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize,
                assumeInjectionModifiesValues,
                discriminators, rowFilter, unorderedJoin);
    }

    public <S, T> Discriminator<S, T> getDiscriminator(ClassMeta<T> classMeta) {
        return getDiscriminator(classMeta.getType());
    }

    public <S, T> Discriminator<S, T> getDiscriminator(Type type) {
        for(Discriminator<?, ?> d : discriminators) {
            if (TypeHelper.areEquals(type, d.type)) {
                return (Discriminator<S, T>) d;
            }
        }
        return null;
    }

    public List<Discriminator<S, ?>> getDiscriminators() {
        return discriminators;
    }

    public MapperConfig<K, S> discriminators(List<Discriminator<S, ?>> discriminators) {
        return new MapperConfig<K, S>(
                columnDefinitions,
                propertyNameMatcherFactory,
                mapperBuilderErrorHandler,
                failOnAsm,
                asmMapperNbFieldsLimit,
                fieldMapperErrorHandler,
                consumerErrorHandler,
                maxMethodSize,
                assumeInjectionModifiesValues,
                discriminators, 
                rowFilter, unorderedJoin);
    }

    public DiscriminatorCase<S, ?> getDiscriminatorCase(Type type) {
        return null;
    }

    public <SET> UnaryFactory<SET, Enumerable<S>> applyEnumerableFilter(final UnaryFactory<SET, Enumerable<S>> enumerableFactory) {
        final Predicate<? super S> rowFilter = this.rowFilter;
        if (rowFilter == null) return enumerableFactory;
        return new UnaryFactory<SET, Enumerable<S>>() {
            @Override
            public Enumerable<S> newInstance(SET set) {
                Enumerable<S> enumerable = enumerableFactory.newInstance(set);
                return new PredicatedEnumerable<S>(enumerable, rowFilter);
            }
        };
    }


    public static final class DiscriminatorCase<ROW, T> {
        public final Predicate<ROW> predicate;
        public final ClassMeta<? extends T> classMeta;

        public DiscriminatorCase(Predicate<ROW> predicate, ClassMeta<? extends T> classMeta) {
            this.predicate = predicate;
            this.classMeta = classMeta;
        }
    }
    
    public static final class Discriminator<ROW, T> {
        public final Type type;
        public final DiscriminatorCase<ROW, T>[] cases;

        public Discriminator(Type type, DiscriminatorCase<ROW, T>[] cases) {
            this.type = type;
            this.cases = cases;
        }

        public DiscriminatorCase<ROW, T> getCase(Type type) {
            for(DiscriminatorCase<ROW, T> c : cases) {
                if (TypeHelper.areEquals(type, c.classMeta.getType())) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Not cases for type " + type);
        }
    }

}