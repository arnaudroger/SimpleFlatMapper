package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ForEachCallBack;

import java.util.List;

public abstract class ConstantSourceMapperBuilder<S, T, K extends FieldKey<K>> {

    @SuppressWarnings("unchecked")
    public abstract ConstantSourceMapperBuilder<S, T, K> addMapping(K key, ColumnDefinition<K, ?> columnDefinition);

    protected abstract <P> void addMapping(K columnKey, ColumnDefinition<K, ?> columnDefinition,  PropertyMeta<T, P> prop);

        @SuppressWarnings("unchecked")
    public abstract ContextualSourceFieldMapperImpl<S, T> mapper();

    public abstract SourceFieldMapper<S, T> sourceFieldMapper();

    public abstract boolean isRootAggregate();

    public abstract MappingContextFactory<? super S> contextFactory();

    public abstract void addMapper(FieldMapper<S, T> mapper);

    public abstract List<K> getKeys();

    public abstract <H extends ForEachCallBack<PropertyMapping<T, ?, K>>> H forEachProperties(H handler);

    public static <S, T, K extends FieldKey<K>> ConstantSourceMapperBuilder<S, T, K> newConstantSourceMapperBuilder(
            MapperSource<? super S, K> mapperSource, 
            ClassMeta<T> classMeta, 
            MapperConfig<K, ? extends S> config, 
            MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder, 
            KeyFactory<K> keyFactory) {
        return newConstantSourceMapperBuilder(mapperSource, classMeta, config, mappingContextFactoryBuilder, keyFactory, null);
    }

    public static <S, T, K extends FieldKey<K>> ConstantSourceMapperBuilder<S, T, K> newConstantSourceMapperBuilder(
            MapperSource<? super S, K> mapperSource,
            ClassMeta<T> classMeta,
            MapperConfig<K, ? extends S> config,
            MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder,
            KeyFactory<K> keyFactory,
            PropertyFinder<T> propertyFinder) {
        return newConstantSourceMapperBuilder(mapperSource, null, classMeta, config, mappingContextFactoryBuilder, keyFactory, propertyFinder);
    }

    public static <S, T, K extends FieldKey<K>> ConstantSourceMapperBuilder<S, T, K> newConstantSourceMapperBuilder(
            MapperSource<? super S, K> mapperSource,
            PropertyMeta<?, T> owner,
            MapperConfig<K, ? extends S> config,
            MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder,
            KeyFactory<K> keyFactory,
            PropertyFinder<T> propertyFinder) {
        return newConstantSourceMapperBuilder(mapperSource, owner, owner.getPropertyClassMeta(), config, mappingContextFactoryBuilder, keyFactory, propertyFinder);
    }

    private static <S, T, K extends FieldKey<K>> ConstantSourceMapperBuilder<S, T, K> newConstantSourceMapperBuilder(
            MapperSource<? super S, K> mapperSource,
            PropertyMeta<? , T> owner,
            ClassMeta<T> classMeta,
            MapperConfig<K, ? extends S> config,
            MappingContextFactoryBuilder<S, K> mappingContextFactoryBuilder, 
            KeyFactory<K> keyFactory, 
            PropertyFinder<T> propertyFinder) {
        
        MapperConfig.Discriminator<S, K, T>[] discriminators = config.getDiscriminators(classMeta);
        if (discriminators == null || discriminators.length == 0) {
            return new DefaultConstantSourceMapperBuilder<S, T, K>(
                    mapperSource,
                    classMeta,
                    config,
                    mappingContextFactoryBuilder,
                    keyFactory,
                    propertyFinder);
        } else {
            return new DiscriminatorConstantSourceMapperBuilder<S, T, K>(
              discriminators,
              mapperSource,
              owner,
              classMeta, 
              config, 
              mappingContextFactoryBuilder,
              keyFactory, 
              propertyFinder      
            );
        }
    }


}
