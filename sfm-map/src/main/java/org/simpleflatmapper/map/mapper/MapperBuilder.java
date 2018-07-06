package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.UnaryFactory;

/**
 * @param <T> the targeted type of the mapper
 */
public class MapperBuilder<ROW, SET, T, K extends FieldKey<K>, E extends Exception, M extends SetRowMapper<ROW, SET, T, E>, B extends MapperBuilder<ROW, SET, T, K, E, M, B>>  {


    protected final KeyFactory<K> keyFactory;
    protected final SetRowMapperBuilder<ROW, SET, T, K, E> setRowMapperBuilder;
    protected final Function<SetRowMapper<ROW, SET, T, E>, M> specialisedMapper;
    private int calculatedIndex;

    /**
     * @param classMeta                  the meta for the target class.
     * @param parentBuilder              the parent builder, null if none.
     * @param mapperConfig               the mapperConfig.
     * @param mapperSource               the Mapper source.
     * @param keyFactory
     * @param enumerableFactory
     * @param specialisedMapper
     * @param startIndex                 the first property index
     */
    public MapperBuilder(
            final ClassMeta<T> classMeta,
            MappingContextFactoryBuilder<? super ROW, K> parentBuilder,
            MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig,
            MapperSource<? super ROW, K> mapperSource,
            KeyFactory<K> keyFactory,
            UnaryFactory<SET, Enumerable<ROW>> enumerableFactory, 
            Function<SetRowMapper<ROW, SET, T, E>, M> specialisedMapper,
            int startIndex) {
        this.specialisedMapper = specialisedMapper;
        this.setRowMapperBuilder = new SetRowMapperBuilder<ROW, SET, T, K, E>(classMeta, parentBuilder, mapperConfig, mapperSource, keyFactory, enumerableFactory);
        this.keyFactory = keyFactory;
        this.calculatedIndex = startIndex;
    }

    /**
     * @return a new newInstance of the jdbcMapper based on the current state of the builder.
     */
    public M mapper() {
        return specialisedMapper.apply(setRowMapperBuilder.mapper());
    }
    
    protected final SourceFieldMapper<ROW, T> sourceFieldMapper() {
        return setRowMapperBuilder.sourceFieldMapper();
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
    public final B addMapper(FieldMapper<ROW, T> mapper) {
        setRowMapperBuilder.addMapper(mapper);
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    public final B addMapping(K key, FieldMapperColumnDefinition<K> columnDefinition) {
        setRowMapperBuilder.addMapping(key, columnDefinition);
        return (B) this;
    }

    public final B addMapping(K key, Object... properties) {
        if (properties.length == 1) { // catch Object... on column definition
            if (properties[0] instanceof ColumnDefinition) {
                return  addMapping(key, (FieldMapperColumnDefinition<K>) properties[0]);
            }
        }
        return addMapping(key, FieldMapperColumnDefinition.<K>of(properties));
    }

    private K key(String column, int index) {
        return keyFactory.newKey(column, index);
    }

}