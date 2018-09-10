package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.Function;

import java.util.List;

/**
 * @param <T> the targeted type of the mapper
 */
public class MapperBuilder<ROW, SET, T, K extends FieldKey<K>, E extends Exception, IM extends SetRowMapper<ROW, SET, T, E>, OM extends SetRowMapper<ROW, SET, T, E>, B extends MapperBuilder<ROW, SET, T, K, E, IM, OM, B>>  {


    protected final KeyFactory<K> keyFactory;
    protected final SetRowMapperBuilder<IM, ROW, SET, T, K, E> setRowMapperBuilder;
    protected final BiFunction<IM, List<K>, OM> specialisedMapper;
    protected final Function<Object[], ColumnDefinition<K, ?>> columnDefinitionFactory;
    
    private int calculatedIndex;
    
    public MapperBuilder(
            KeyFactory<K> keyFactory,
            SetRowMapperBuilder<IM, ROW, SET, T, K, E> setRowMapperBuilder,
            BiFunction<IM, List<K>, OM> specialisedMapper,
            Function<Object[], ColumnDefinition<K, ?>> columnDefinitionFactory, int calculatedIndex) {
        this.keyFactory = keyFactory;
        this.setRowMapperBuilder = setRowMapperBuilder;
        this.specialisedMapper = specialisedMapper;
        this.columnDefinitionFactory = columnDefinitionFactory;
        this.calculatedIndex = calculatedIndex;
    }

    /**
     * @return a new newInstance of the jdbcMapper based on the current state of the builder.
     */
    public final OM mapper() {
        return specialisedMapper.apply(setRowMapperBuilder.mapper(), setRowMapperBuilder.getKeys());
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
    public final B addMapping(final String column, final ColumnDefinition<K, ?> columnDefinition) {
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
    public final B addMapping(String column, int index, final ColumnDefinition<K, ?> columnDefinition) {
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
    public final B addMapping(K key, ColumnDefinition<K, ?> columnDefinition) {
        setRowMapperBuilder.addMapping(key, columnDefinition);
        return (B) this;
    }

    public final B addMapping(K key, Object... properties) {
        if (properties.length == 1) { // catch Object... on column definition
            if (properties[0] instanceof ColumnDefinition) {
                return  addMapping(key, (ColumnDefinition<K, ?>) properties[0]);
            }
        }
        return addMapping(key, columnDefinitionFactory.apply(properties));
    }

    private K key(String column, int index) {
        return keyFactory.newKey(column, index);
    }
}