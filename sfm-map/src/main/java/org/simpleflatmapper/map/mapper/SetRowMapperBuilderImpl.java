package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.impl.DiscriminatorReflectionService;
import org.simpleflatmapper.map.property.IgnoreRowIfNullProperty;
import org.simpleflatmapper.reflect.ClassMetaWithDiscriminatorId;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.PredicatedEnumerable;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param <T> the targeted type of the mapper
 */
public class SetRowMapperBuilderImpl<M extends SetRowMapper<ROW, SET, T, E>, ROW, SET, T, K extends FieldKey<K>, E extends Exception> implements SetRowMapperBuilder<M, ROW, SET, T, K, E> {

    private final ConstantSourceMapperBuilder<ROW, T, K> constantSourceMapperBuilder;

    protected final MapperConfig<K, ROW> mapperConfig;
    protected final MappingContextFactoryBuilder<? super ROW, K> mappingContextFactoryBuilder;
    private final UnaryFactory<SET, Enumerable<ROW>> enumerableFactory;
    private final SetRowMapperFactory<M, ROW, SET, T, E> setRowMapperFactory;
    private final KeySourceGetter<K, ? super ROW> keySourceGetter;

    /**
     * @param classMeta                  the meta for the target class.
     * @param parentBuilder              the parent builder, null if none.
     * @param mapperConfig               the mapperConfig.
     * @param mapperSource               the Mapper source.
     * @param keyFactory
     * @param enumerableFactory
     * @param keySourceGetter
     */
    public SetRowMapperBuilderImpl(
            final ClassMeta<T> classMeta,
            MappingContextFactoryBuilder<ROW, K> parentBuilder,
            MapperConfig<K, ROW> mapperConfig,
            MapperSource<? super ROW, K> mapperSource,
            KeyFactory<K> keyFactory,
            UnaryFactory<SET, Enumerable<ROW>> enumerableFactory,
            SetRowMapperFactory<M, ROW, SET, T, E> setRowMapperFactory, 
            KeySourceGetter<K, ? super ROW> keySourceGetter) {
        this.setRowMapperFactory = setRowMapperFactory;
        this.enumerableFactory = enumerableFactory;
        this.keySourceGetter = keySourceGetter;
        this.constantSourceMapperBuilder =
                ConstantSourceMapperBuilder.<ROW, T, K>newConstantSourceMapperBuilder(
                        mapperSource,
                        prepareClassMetaForDiscriminator(classMeta, mapperConfig),
                        mapperConfig,
                        parentBuilder,
                        keyFactory);
        this.mapperConfig = mapperConfig;
        this.mappingContextFactoryBuilder = parentBuilder;
    }

    private ClassMeta<T> prepareClassMetaForDiscriminator(ClassMeta<T> classMeta, MapperConfig<K, ROW> mapperConfig) {
        List<MapperConfig.Discriminator<ROW, K, ?>> discriminators = mapperConfig.getDiscriminators();

        if (discriminators.isEmpty()) {
            return classMeta;
        } else {
            Map<Class<?>, List<ClassMetaWithDiscriminatorId<?>>> discriminatorMap = new HashMap<Class<?>, List<ClassMetaWithDiscriminatorId<?>>>();
            for(MapperConfig.Discriminator<?, ?, ?> d : discriminators) {
                Class<Object> clazz = TypeHelper.toClass(d.type);
                List<ClassMetaWithDiscriminatorId<?>> implementations = discriminatorMap.get(clazz);

                if (implementations == null) {
                    implementations = new ArrayList<ClassMetaWithDiscriminatorId<?>>();
                    discriminatorMap.put(clazz, implementations);
                }

                for(MapperConfig.DiscriminatorCase<?, ?, ?> dc : d.cases) {
                    implementations.add(new ClassMetaWithDiscriminatorId(dc.classMeta, d.discriminatorId));
                }

            }
            DiscriminatorReflectionService dfs = new DiscriminatorReflectionService(classMeta.getReflectionService(), discriminatorMap);
            return classMeta.withReflectionService(dfs);
        }
    }

    /**
     * @return a new newInstance of the jdbcMapper based on the current state of the builder.
     */
    @Override
    public final M mapper() {
        ContextualSourceFieldMapperImpl<ROW, T> mapper = sourceFieldMapper();

        if (mapper.getDelegate() instanceof TransformSourceFieldMapper) {
            TransformSourceFieldMapper transformSourceFieldMapper = (TransformSourceFieldMapper) mapper.getDelegate();
            ContextualSourceFieldMapper<ROW, T> unwrappedMapper = new ContextualSourceFieldMapperImpl<ROW, T>(mapper.getMappingContextFactory(), transformSourceFieldMapper.delegate);
            M m;

            if (constantSourceMapperBuilder.isRootAggregate()) {
                m = (M) setRowMapperFactory.newTransformer(setRowMapperFactory.newJoinMapper(unwrappedMapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.build(), enumerableFactory()), transformSourceFieldMapper.transform);
            } else {
                m = (M) setRowMapperFactory.newTransformer(setRowMapperFactory.newStaticMapper(unwrappedMapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.build(), enumerableFactory()), transformSourceFieldMapper.transform);
            }

            return m;
               
        } else {

            M m;

            if (constantSourceMapperBuilder.isRootAggregate()) {
                if (mapperConfig.unorderedJoin()) {
                    m = setRowMapperFactory.newUnorderedJoinMapper(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.build(), enumerableFactory());
                } else {
                    m = setRowMapperFactory.newJoinMapper(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.build(), enumerableFactory());
                }
            } else {
                m = setRowMapperFactory.newStaticMapper(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.build(), enumerableFactory());
            }

            return m;
        }
        
        
    }

    private UnaryFactory<SET, Enumerable<ROW>> enumerableFactory() {
        final Predicate<ROW> filter = getRowPredicate();
        if (filter != null) {
            return new UnaryFactory<SET, Enumerable<ROW>>() {
                @Override
                public Enumerable<ROW> newInstance(SET set) {
                    return new PredicatedEnumerable<ROW>(enumerableFactory.newInstance(set), filter);
                }
            };
        }
        return enumerableFactory;
    }

    private Predicate<ROW> getRowPredicate() {
        final Set<K> mandatoryKeys = constantSourceMapperBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, K>>() {
            Set<K> mandatoryKeys = new HashSet<K>();

            @Override
            public void handle(PropertyMapping<T, ?, K> tkPropertyMapping) {
                if (tkPropertyMapping.getColumnDefinition().has(IgnoreRowIfNullProperty.class)) {
                    mandatoryKeys.add(tkPropertyMapping.getColumnKey());
                }
            }
        }).mandatoryKeys;
        
        if (mandatoryKeys.isEmpty()) {
            return null;
        } else {
            
            final K[] keys = (K[]) Array.newInstance(mandatoryKeys.iterator().next().getClass(), mandatoryKeys.size());
            int i = 0;
            for(K k : mandatoryKeys) {
                keys[i++] = k;
            }
            return new Predicate<ROW>() {
                @Override
                public boolean test(ROW row) {
                    for(K k : keys) {
                        try {
                            if (keySourceGetter.getValue(k, row) == null) return false;
                        } catch (Exception e) {
                            ErrorHelper.rethrow(e);
                        }
                    }
                    return true;
                }
            };
        }
    }

    @Override
    public final ContextualSourceFieldMapperImpl<ROW, T> sourceFieldMapper() {
        return constantSourceMapperBuilder.mapper();
    }
    
    @Override
    public boolean isRootAggregate() {
        return constantSourceMapperBuilder.isRootAggregate();
    }


    @Override
    public final void addMapper(FieldMapper<ROW, T> mapper) {
        constantSourceMapperBuilder.addMapper(mapper);
    }


    @Override
    public final void addMapping(K key, ColumnDefinition<K, ?> columnDefinition) {
        constantSourceMapperBuilder.addMapping(key, columnDefinition);
    }


    @Override
    public MapperConfig<K, ROW>  mapperConfig() {
        return mapperConfig;
    }

    @Override
    public MappingContextFactoryBuilder<? super ROW, K> getMappingContextFactoryBuilder() {
        return mappingContextFactoryBuilder;
    }

    @Override
    public List<K> getKeys() {
        return constantSourceMapperBuilder.getKeys();
    }

    public interface SetRowMapperFactory<M extends SetRowMapper<ROW, SET, T, E>, ROW, SET, T, E extends Exception> {

        M newJoinMapper(ContextualSourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<SET, Enumerable<ROW>> enumerableFactory);
        
        M newUnorderedJoinMapper(ContextualSourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<SET, Enumerable<ROW>> enumerableFactory);

        M newStaticMapper(ContextualSourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<SET, Enumerable<ROW>> enumerableFactory);

        <I> M newTransformer(SetRowMapper<ROW, SET, I, E> setRowMapper, Function<I, T> transform);
    }

}