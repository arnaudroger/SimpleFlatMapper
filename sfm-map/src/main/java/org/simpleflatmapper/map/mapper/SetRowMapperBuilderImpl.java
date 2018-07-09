package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.UnaryFactory;

/**
 * @param <T> the targeted type of the mapper
 */
public class SetRowMapperBuilderImpl<M extends SetRowMapper<ROW, SET, T, E>, ROW, SET, T, K extends FieldKey<K>, E extends Exception> implements SetRowMapperBuilder<M, ROW, SET, T, K, E> {

    private final ConstantSourceMapperBuilder<ROW, T, K> constantSourceMapperBuilder;

    protected final MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig;
    protected final MappingContextFactoryBuilder<? super ROW, K> mappingContextFactoryBuilder;
    private final UnaryFactory<SET, Enumerable<ROW>> enumerableFactory;
    private final SetRowMapperFactory<M, ROW, SET, T, E> setRowMapperFactory;

    /**
     * @param classMeta                  the meta for the target class.
     * @param parentBuilder              the parent builder, null if none.
     * @param mapperConfig               the mapperConfig.
     * @param mapperSource               the Mapper source.
     * @param keyFactory
     * @param enumerableFactory
     */
    public SetRowMapperBuilderImpl(
            final ClassMeta<T> classMeta,
            MappingContextFactoryBuilder<? super ROW, K> parentBuilder,
            MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig,
            MapperSource<? super ROW, K> mapperSource,
            KeyFactory<K> keyFactory,
            UnaryFactory<SET, Enumerable<ROW>> enumerableFactory, 
            SetRowMapperFactory<M, ROW, SET, T, E> setRowMapperFactory) {
        this.setRowMapperFactory = setRowMapperFactory;
        this.enumerableFactory = enumerableFactory;
        this.constantSourceMapperBuilder =
                new ConstantSourceMapperBuilder<ROW, T, K>(
                        mapperSource,
                        classMeta,
                        mapperConfig,
                        parentBuilder,
                        keyFactory);
        this.mapperConfig = mapperConfig;
        this.mappingContextFactoryBuilder = parentBuilder;
    }

    /**
     * @return a new newInstance of the jdbcMapper based on the current state of the builder.
     */
    @Override
    public final M mapper() {
        SourceFieldMapper<ROW, T> mapper = sourceFieldMapper();

        if (mapper instanceof TransformSourceFieldMapper) {
            TransformSourceFieldMapper transformSourceFieldMapper = (TransformSourceFieldMapper) mapper;
            M m;

            if (constantSourceMapperBuilder.hasJoin()) {
                m = (M) setRowMapperFactory.newTransformer(setRowMapperFactory.newJoinMapper(transformSourceFieldMapper.delegate, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory(), enumerableFactory), transformSourceFieldMapper.transform);
            } else {
                m = (M) setRowMapperFactory.newTransformer(setRowMapperFactory.newStaticMapper(transformSourceFieldMapper.delegate, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory(), enumerableFactory), transformSourceFieldMapper.transform);
            }

            return m;
               
        } else {

            M m;

            if (constantSourceMapperBuilder.hasJoin()) {
                m = setRowMapperFactory.newJoinMapper(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory(), enumerableFactory);
            } else {
                m = setRowMapperFactory.newStaticMapper(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory(), enumerableFactory);
            }

            return m;
        }
        
        
    }
    
    @Override
    public final SourceFieldMapper<ROW, T> sourceFieldMapper() {
        return constantSourceMapperBuilder.mapper();
    }
    
    @Override
    public boolean hasJoin() {
        return constantSourceMapperBuilder.hasJoin();
    }


    @Override
    public final void addMapper(FieldMapper<ROW, T> mapper) {
        constantSourceMapperBuilder.addMapper(mapper);
    }


    @Override
    public final void addMapping(K key, FieldMapperColumnDefinition<K> columnDefinition) {
        constantSourceMapperBuilder.addMapping(key, columnDefinition);
    }


    @Override
    public MapperConfig<K, FieldMapperColumnDefinition<K>>  mapperConfig() {
        return mapperConfig;
    }

    @Override
    public MappingContextFactoryBuilder<? super ROW, K> getMappingContextFactoryBuilder() {
        return mappingContextFactoryBuilder;
    }

    public interface SetRowMapperFactory<M extends SetRowMapper<ROW, SET, T, E>, ROW, SET, T, E extends Exception> {

        M newJoinMapper(SourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<SET, Enumerable<ROW>> enumerableFactory);

        M newStaticMapper(SourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<SET, Enumerable<ROW>> enumerableFactory);

        <I> M newTransformer(SetRowMapper<ROW, SET, I, E> setRowMapper, Function<I, T> transform);
    }

}