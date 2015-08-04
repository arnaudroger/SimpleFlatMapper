package org.sfm.map.mapper;

import org.sfm.map.FieldKey;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MapperConfig;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.reflect.meta.ClassMeta;

/**
 * @param <T> the targeted type of the mapper
 */
public abstract class AbstractMapperBuilder<S, T, K extends FieldKey<K>, M, B extends AbstractMapperBuilder<S, T, K, M, B>> {


    private final FieldMapperMapperBuilder<S, T, K> fieldMapperMapperBuilder;

    protected final MapperConfig<K, FieldMapperColumnDefinition<K, S>> mapperConfig;
    protected final MappingContextFactoryBuilder<? super S, K> mappingContextFactoryBuilder;

    private int calculatedIndex;

    /**
     * @param mapperSource               the Mapper source.
     * @param classMeta                  the meta for the target class.
     * @param parentBuilder              the parent builder, null if none.
     * @param mapperConfig               the mapperConfig.
     * @param startIndex                 the first column index
     */
    public AbstractMapperBuilder(
            final ClassMeta<T> classMeta,
            MappingContextFactoryBuilder<? super S, K> parentBuilder,
            MapperConfig<K, FieldMapperColumnDefinition<K, S>> mapperConfig,
            MapperSource<? super S, K> mapperSource, int startIndex) {
        this.fieldMapperMapperBuilder =
                new FieldMapperMapperBuilder<S, T, K>(
                        mapperSource,
                        classMeta,
                        mapperConfig,
                        parentBuilder
                );
        this.mapperConfig = mapperConfig;
        this.mappingContextFactoryBuilder = parentBuilder;
        this.calculatedIndex = startIndex;
    }

    /**
     * @return a new instance of the jdbcMapper based on the current state of the builder.
     */
    public final M mapper() {
        Mapper<S, T> mapper = fieldMapperMapperBuilder.mapper();

        if (fieldMapperMapperBuilder.hasJoin()) {
            return newJoinJdbcMapper(mapper);
        } else {
            return newStaticJdbcMapper(mapper);
        }
    }

    /**
     * add a new mapping to the specified column with a key column definition and an undefined type.
     * The index is incremented for each non indexed column mapping.
     *
     * @param column the column name
     * @return the current builder
     */
    public final B addKey(String column) {
        return addMapping(column, calculatedIndex++, FieldMapperColumnDefinition.<K, S>key());
    }

    /**
     * add a new mapping to the specified column with an undefined type. The index is incremented for each non indexed column mapping.
     *
     * @param column the column name
     * @return the current builder
     */
    public final B addMapping(String column) {
        return addMapping(column, calculatedIndex++);
    }

    /**
     * add a new mapping to the specified column with the specified columnDefinition and an undefined type. The index is incremented for each non indexed column mapping.
     *
     * @param column           the column name
     * @param columnDefinition the definition
     * @return the current builder
     */
    public final B addMapping(final String column, final FieldMapperColumnDefinition<K, S> columnDefinition) {
        return addMapping(column, calculatedIndex++, columnDefinition);
    }

    /**
     * add a new mapping to the specified column with the specified columnDefinition and an undefined type. The index is incremented for each non indexed column mapping.
     *
     * @param column           the column name
     * @param properties the definition
     * @return the current builder
     */
    public final B addMapping(final String column, final ColumnProperty... properties) {
        return addMapping(column, calculatedIndex++, properties);
    }

    /**
     * add a new mapping to the specified column with the specified index and an undefined type.
     *
     * @param column the column name
     * @param index  the column index
     * @return the current builder
     */
    public final B addMapping(String column, int index) {
        return addMapping(key(column, index));
    }

    /**
     * add a new mapping to the specified column with the specified index, specified column definition and an undefined type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param columnDefinition the column definition
     * @return the current builder
     */
    public final B addMapping(String column, int index, final FieldMapperColumnDefinition<K, S> columnDefinition) {
        return addMapping(key(column, index), columnDefinition);
    }

    /**
     * add a new mapping to the specified column with the specified index, specified column definition and an undefined type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param properties the column properties
     * @return the current builder
     */
    public final B addMapping(String column, int index, final ColumnProperty... properties) {
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
        fieldMapperMapperBuilder.addMapper(mapper);
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    public final B addMapping(K key, FieldMapperColumnDefinition<K, S> columnDefinition) {
        fieldMapperMapperBuilder.addMapping(key, columnDefinition);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public final B addMapping(K key, ColumnProperty... properties) {
        fieldMapperMapperBuilder.addMapping(key, FieldMapperColumnDefinition.<K, S>of(properties));
        return (B) this;
    }

    protected abstract K key(String column, int index);

    protected abstract M newJoinJdbcMapper(Mapper<S, T> mapper);

    protected abstract M newStaticJdbcMapper(Mapper<S, T> mapper);
}