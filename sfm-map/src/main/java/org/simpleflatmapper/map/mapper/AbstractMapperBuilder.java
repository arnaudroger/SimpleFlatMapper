package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.meta.ClassMeta;

/**
 * @param <T> the targeted type of the mapper
 */
public abstract class AbstractMapperBuilder<S, T, K extends FieldKey<K>, M, B extends AbstractMapperBuilder<S, T, K, M, B>> {


    private final ConstantSourceMapperBuilder<S, T, K> constantSourceMapperBuilder;

    protected final MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig;
    protected final MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder;
    private final KeyFactory<K> keyFactory;

    private int calculatedIndex;

    /**
     * @param classMeta                  the meta for the target class.
     * @param parentBuilder              the parent builder, null if none.
     * @param mapperConfig               the mapperConfig.
     * @param mapperSource               the Mapper source.
     * @param keyFactory
     * @param startIndex                 the first property index
     */
    public AbstractMapperBuilder(
            final ClassMeta<T> classMeta,
            MappingContextFactoryBuilder<? super S, K> parentBuilder,
            MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig,
            MapperSource<? super S, K> mapperSource,
            KeyFactory<K> keyFactory,
            int startIndex) {
        this.constantSourceMapperBuilder =
                new ConstantSourceMapperBuilder<S, T, K>(
                        mapperSource,
                        classMeta,
                        mapperConfig,
                        parentBuilder,
                        keyFactory);
        this.keyFactory = keyFactory;
        this.mapperConfig = mapperConfig;
        this.mappingContextFactoryBuilder = parentBuilder;
        this.calculatedIndex = startIndex;
    }

    /**
     * @return a new newInstance of the jdbcMapper based on the current state of the builder.
     */
    public final M mapper() {
        Mapper<S, T> mapper = constantSourceMapperBuilder.mapper();

        if (constantSourceMapperBuilder.hasJoin()) {
            return newJoinMapper(mapper);
        } else {
            return newStaticMapper(mapper);
        }
    }

    /**
     * add a new mapping to the specified property with a key property definition and an undefined type.
     * The index is incremented for each non indexed property mapping.
     *
     * @param column the property name
     * @return the current builder
     */
    public final B addKey(String column) {
        return addMapping(column, calculatedIndex++, FieldMapperColumnDefinition.<K>key());
    }

    /**
     * add a new mapping to the specified property with an undefined type. The index is incremented for each non indexed property mapping.
     *
     * @param column the property name
     * @return the current builder
     */
    public final B addMapping(String column) {
        return addMapping(column, calculatedIndex++);
    }

    /**
     * add a new mapping to the specified property with the specified columnDefinition and an undefined type. The index is incremented for each non indexed property mapping.
     *
     * @param column           the property name
     * @param columnDefinition the definition
     * @return the current builder
     */
    public final B addMapping(final String column, final FieldMapperColumnDefinition<K> columnDefinition) {
        return addMapping(column, calculatedIndex++, columnDefinition);
    }

    /**
     * add a new mapping to the specified property with the specified columnDefinition and an undefined type. The index is incremented for each non indexed property mapping.
     *
     * @param column           the property name
     * @param properties the definition
     * @return the current builder
     */
    public final B addMapping(final String column, final Object... properties) {
        return addMapping(column, calculatedIndex++, properties);
    }

    /**
     * add a new mapping to the specified property with the specified index and an undefined type.
     *
     * @param column the property name
     * @param index  the property index
     * @return the current builder
     */
    public final B addMapping(String column, int index) {
        return addMapping(key(column, index));
    }

    /**
     * add a new mapping to the specified property with the specified index, specified property definition and an undefined type.
     *
     * @param column           the property name
     * @param index            the property index
     * @param columnDefinition the property definition
     * @return the current builder
     */
    public final B addMapping(String column, int index, final FieldMapperColumnDefinition<K> columnDefinition) {
        return addMapping(key(column, index), columnDefinition);
    }

    /**
     * add a new mapping to the specified property with the specified index, specified property definition and an undefined type.
     *
     * @param column           the property name
     * @param index            the property index
     * @param properties the property properties
     * @return the current builder
     */
    public final B addMapping(String column, int index, final Object... properties) {
        return addMapping(key(column, index), properties);
    }

    /**
     * append a FieldMapper to the mapping list.
     *
     * @param mapper the field jdbcMapper
     * @return the current builder
     */
    @SuppressWarnings("unchecked")
    public final B addMapper(FieldMapper<S, T> mapper) {
        constantSourceMapperBuilder.addMapper(mapper);
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    public final B addMapping(K key, FieldMapperColumnDefinition<K> columnDefinition) {
        constantSourceMapperBuilder.addMapping(key, columnDefinition);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public final B addMapping(K key, Object... properties) {
        if (properties.length == 1) { // catch Object... on column definition
            if (properties[0] instanceof ColumnDefinition) {
                constantSourceMapperBuilder.addMapping(key, (FieldMapperColumnDefinition<K>) properties[0]);
                return (B) this;
            }
        }
        constantSourceMapperBuilder.addMapping(key, FieldMapperColumnDefinition.<K>of(properties));
        return (B) this;
    }

    private final K key(String column, int index) {
        return keyFactory.newKey(column, index);
    }

    protected abstract M newJoinMapper(Mapper<S, T> mapper);

    protected abstract M newStaticMapper(Mapper<S, T> mapper);
}