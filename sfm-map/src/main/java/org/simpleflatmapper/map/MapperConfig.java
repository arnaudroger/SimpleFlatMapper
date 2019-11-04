package org.simpleflatmapper.map;

import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.error.RethrowFieldMapperErrorHandler;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.impl.IdentityFieldMapperColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.DefaultPropertyNameMatcherFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.*;

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
                false, Collections.<Discriminator<S, K, ?>>emptyList(), null, false);
    }

    public static <K extends FieldKey<K>, S> MapperConfig<K, S> config(ColumnDefinitionProvider<K> columnDefinitionProvider) {
        return new MapperConfig<K, S>(
                columnDefinitionProvider,
                DefaultPropertyNameMatcherFactory.DEFAULT,
                RethrowMapperBuilderErrorHandler.INSTANCE,
                false,
                NO_ASM_MAPPER_THRESHOLD,
                RethrowFieldMapperErrorHandler.INSTANCE,
                RethrowConsumerErrorHandler.INSTANCE, MAX_METHOD_SIZE, false, Collections.<Discriminator<S, K, ?>>emptyList(), null, false);
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
    private final List<Discriminator<S, K, ?>> discriminators;
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
            List<Discriminator<S, K, ?>> discriminators, Predicate<? super S> rowFilter, boolean unorderedJoin) {
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


    @Deprecated
    public <T> MapperConfig<K, S> discriminator(Class<T> rootClass, Predicate<? super K> discriminatorPredicate, DiscriminatorCase<S, K, T>... cases) {
        return discriminator((Type)rootClass, discriminatorPredicate, cases);
    }
    @Deprecated
    public <T> MapperConfig<K, S> discriminator(Type rootClass, Predicate<? super K> discriminatorPredicate, DiscriminatorCase<S, K, T>... cases) {
        List<Discriminator<S, K, ?>> discriminators = new ArrayList<Discriminator<S, K, ?>>(this.discriminators);
        discriminators.add(new Discriminator<S, K, T>(rootClass, cases, discriminatorPredicate, null));
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
                rowFilter,
                unorderedJoin);
    }

    public <S, T> Discriminator<S, K, T>[] getDiscriminators(ClassMeta<T> classMeta) {
        return getDiscriminators(classMeta.getType());
    }

    public <S, T> Discriminator<S,  K, T>[] getDiscriminators(Type type) {
        List<Discriminator<S, K, T>> list = new ArrayList<Discriminator<S, K, T>>();
        for(Discriminator<?, ?, ?> d : discriminators) {
            if (TypeHelper.areEquals(type, d.type)) {
                list.add((Discriminator<S,  K,T>) d);
            }
        }
        return list.toArray(new Discriminator[0]);
    }

    public List<Discriminator<S, K, ?>> getDiscriminators() {
        return discriminators;
    }

    public MapperConfig<K, S> discriminators(List<Discriminator<S, K, ?>> discriminators) {
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

    public DiscriminatorCase<S, K, ?> getDiscriminatorCase(Type type) {
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


    public static final class DiscriminatorCase<ROW, K extends  FieldKey<K>, T> {
        public final Function<List<K>, Predicate<ROW>> predicateFactory;
        public final ClassMeta<? extends T> classMeta;

        public DiscriminatorCase(Function<List<K>, Predicate<ROW>> predicateFactory, ClassMeta<? extends T> classMeta) {
            this.predicateFactory = predicateFactory;
            this.classMeta = classMeta;
        }
    }
    
    public static final class Discriminator<ROW, K extends  FieldKey<K>, T> {
        public final Type type;
        public final DiscriminatorCase<ROW, K, T>[] cases;
        public final Predicate<? super K> discriminatorPredicate;
        public final Object discriminatorId;

        public Discriminator(Type type, DiscriminatorCase<ROW, K, T>[] cases, Predicate<? super K> discriminatorPredicate, Object discriminatorId) {
            this.type = type;
            this.cases = cases;
            this.discriminatorPredicate = discriminatorPredicate;
            this.discriminatorId = discriminatorId;
        }


        public DiscriminatorCase<ROW, K, T> getCase(Type type) {
            for(DiscriminatorCase<ROW, K, T> c : cases) {
                if (TypeHelper.areEquals(type, c.classMeta.getType())) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Not cases for type " + type);
        }

        public boolean isCompatibleWithKeys(List<K> allDiscriminatoryKeys) {
            if (discriminatorPredicate == null) return true;
            for(K k : allDiscriminatoryKeys) {
                if (!discriminatorPredicate.test(k)) return false;
            }
            return !allDiscriminatoryKeys.isEmpty();
        }
    }

    public static boolean sameDiscriminatorId(Object discriminatorId, Object discriminatorId1) {
        return  (discriminatorId == null) || (discriminatorId1 == null) || discriminatorId == discriminatorId1;
    }


}