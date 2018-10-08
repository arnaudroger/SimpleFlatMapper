package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.impl.GenericBuilder;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscriminatorConstantSourceMapperBuilder<S, T, K extends FieldKey<K>>  implements ConstantSourceMapperBuilder<S, T, K> {

    
    private final DiscriminatedBuilder<S, T, K>[] builders;
    private final ReflectionService reflectionService;
    private final MapperSource<? super S, K> mapperSource;
    private final MapperConfig<K> mapperConfig;
    private final MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder;


    @SuppressWarnings("unchecked")
    public DiscriminatorConstantSourceMapperBuilder(
            MapperConfig.Discriminator<? super S, T> discriminator,
            final MapperSource<? super S, K> mapperSource,
            final ClassMeta<T> classMeta,
            final MapperConfig<K> mapperConfig,
            MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder,
            KeyFactory<K> keyFactory,
            PropertyFinder<T> propertyFinder) throws MapperBuildingException {
        this.reflectionService = classMeta.getReflectionService();
        this.mapperSource = mapperSource;
        this.mapperConfig = mapperConfig;
        this.mappingContextFactoryBuilder = mappingContextFactoryBuilder;
        
        builders = new DiscriminatedBuilder[discriminator.cases.length];
        
        for(int i = 0; i < discriminator.cases.length; i++) {
            builders[i] = new DiscriminatedBuilder<S, T, K>(discriminator.cases[i], 
                    new DefaultConstantSourceMapperBuilder<>(mapperSource, classMeta, mapperConfig, mappingContextFactoryBuilder, keyFactory, propertyFinder));
        }
    }


    
    @Override
    public ConstantSourceMapperBuilder<S, T, K> addMapping(K key, ColumnDefinition<K, ?> columnDefinition) {
        for(DiscriminatedBuilder<S, T, K> builder : builders) {
            builder.builder.addMapping(key, columnDefinition);
        }
        return this;
    }

    @Override
    public ContextualSourceFieldMapperImpl<S, T> mapper() {
        SourceFieldMapper<S, T> mapper = sourceFieldMapper();
        return new ContextualSourceFieldMapperImpl<S, T>(mappingContextFactoryBuilder.build(), mapper);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SourceFieldMapper<S, T> sourceFieldMapper() {
        PredicatedInstantiator<S, T>[] predicatedInstantiator = new PredicatedInstantiator[builders.length];
        
        for(int i = 0; i < builders.length; i++) {
            DiscriminatedBuilder<S, T, K> builder = builders[i];
            DefaultConstantSourceMapperBuilder.GenericBuilderMapping genericBuilderMapping = builder.builder.getGenericBuilderMapping();
            predicatedInstantiator[i] = new PredicatedInstantiator<S, T>(builder.discrimnatorCase.predicate, genericBuilderMapping.genericBuilderInstantiator);
        }
        GenericBuildBiInstantiator gbi = new GenericBuildBiInstantiator(predicatedInstantiator);

        MapperImpl<S, GenericBuilder<S, T>> mapper = new MapperImpl<S, GenericBuilder<S, T>>(new FieldMapper[0], new FieldMapper[0], gbi);
        
        return new TransformSourceFieldMapper<S, GenericBuilder<S, T>, T>(mapper, new FieldMapper[0], new DefaultConstantSourceMapperBuilder.GenericBuilderTransformFunction<S, T>());
    }

    @Override
    public boolean isRootAggregate() {
        return builders[0].builder.isRootAggregate();
    }

    @Override
    public MappingContextFactory<? super S> contextFactory() {
        return builders[0].builder.contextFactory();
    }

    @Override
    public void addMapper(FieldMapper<S, T> mapper) {
        for(DiscriminatedBuilder<S, T, K> builder : builders) {
            builder.builder.addMapper(mapper);
        }
    }
    
    private static class DiscriminatedBuilder<S, T, K extends FieldKey<K>> {
        private final MapperConfig.DiscrimnatorCase<? super S, T> discrimnatorCase;
        private final DefaultConstantSourceMapperBuilder<S, T, K> builder;

        private DiscriminatedBuilder(MapperConfig.DiscrimnatorCase<? super S, T> discrimnatorCase, DefaultConstantSourceMapperBuilder<S, T, K> builder) {
            this.discrimnatorCase = discrimnatorCase;
            this.builder = builder;
        }
    }

    private static class GenericBuildBiInstantiator<S, T> implements BiInstantiator<S, MappingContext<S>, GenericBuilder<S, T>> {
        private final PredicatedInstantiator<S, T>[] predicatedInstantiators;

        public GenericBuildBiInstantiator(PredicatedInstantiator<S, T>[] predicatedInstantiators) {
            this.predicatedInstantiators = predicatedInstantiators;
        }

        @SuppressWarnings("unchecked")
        @Override
        public GenericBuilder<S, T> newInstance(S o, MappingContext<S> o2) throws Exception {
            for(PredicatedInstantiator<S, T> pi : predicatedInstantiators) {
                //noinspection unchecked
                if (pi.predicate.test(o)) {
                    return pi.instantiator.newInstance(o, o2);
                }
            }
            throw new IllegalArgumentException("No discrimator matched " + o); 
        }

        private BiInstantiator<S, MappingContext<? super S>, GenericBuilder> getInstantiator(Object o) {
            for(PredicatedInstantiator pi : predicatedInstantiators) {
                //noinspection unchecked
                if (pi.predicate.test(o)) {
                    return pi.instantiator;
                }
            }
            throw new IllegalArgumentException("No discrimator matched " + o);
        }
    }

    private static class PredicatedInstantiator<S, T> {
        private final Predicate predicate;
        private final BiInstantiator<S, MappingContext<? super S>, GenericBuilder<S, T>> instantiator;

        private PredicatedInstantiator(Predicate predicate, BiInstantiator<S, MappingContext<? super S>, GenericBuilder<S, T>> instantiator) {
            this.predicate = predicate;
            this.instantiator = instantiator;
        }
    }
}
