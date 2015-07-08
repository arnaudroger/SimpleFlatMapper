package org.sfm.datastax;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableData;
import com.datastax.driver.core.Row;
import org.sfm.datastax.impl.DatastaxMappingContextFactoryBuilder;
import org.sfm.datastax.impl.JoinDatastaxMapper;
import org.sfm.datastax.impl.RowGetterFactory;
import org.sfm.datastax.impl.StaticDatastaxMapper;
import org.sfm.map.FieldMapper;
import org.sfm.map.GetterFactory;
import org.sfm.map.Mapper;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.FieldMapperMapperBuilder;
import org.sfm.map.impl.MapperConfig;
import org.sfm.map.impl.MapperSourceImpl;
import org.sfm.map.impl.context.MappingContextFactoryBuilder;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.sql.SQLException;

/**
 * @param <T> the targeted type of the jdbcMapper
 */
public final class DatastaxMapperBuilder<T> {

    public static final MapperSourceImpl<GettableData, DatastaxColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<GettableData, DatastaxColumnKey>(GettableData.class, new RowGetterFactory());

    private final FieldMapperMapperBuilder<Row, T, DatastaxColumnKey> fieldMapperMapperBuilder;

    private final MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey, Row>> mapperConfig;
    private final MappingContextFactoryBuilder<Row, DatastaxColumnKey> mappingContextFactoryBuilder;

    private int calculatedIndex = 1;

    /**
     * Build a new DatastaxMapperBuilder targeting the type specified by the TypeReference. The TypeReference
     * allow you to provide a generic type with check of T<br>
     * <code>new TypeReference&lt;List&lt;String&gt;&gt;() {}</code>
     *
     * @param target the TypeReference to the type T to map to
     */
    public DatastaxMapperBuilder(final TypeReference<T> target) {
        this(target.getType());
    }

    /**
     * Build a new DatastaxMapperBuilder targeting the specified type.
     *
     * @param target the type
     */
    public DatastaxMapperBuilder(final Type target) {
        this(target, ReflectionService.newInstance());
    }

    /**
     * Build a new DatastaxMapperBuilder targeting the specified type with the specified ReflectionService.
     *
     * @param target         the type
     * @param reflectService the ReflectionService
     */
    public DatastaxMapperBuilder(final Type target, ReflectionService reflectService) {
        this(reflectService.<T>getClassMeta(target),
                MapperConfig.<Row, DatastaxColumnKey>fieldMapperConfig(),
                new RowGetterFactory(),
                new DatastaxMappingContextFactoryBuilder());
    }

    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    public DatastaxMapperBuilder(
            final ClassMeta<T> classMeta,
            MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey, Row>> mapperConfig,
            GetterFactory<GettableData, DatastaxColumnKey> getterFactory,
            MappingContextFactoryBuilder<Row, DatastaxColumnKey> parentBuilder) {
        this.fieldMapperMapperBuilder =
                new FieldMapperMapperBuilder<Row, T, DatastaxColumnKey>(
                        FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                        classMeta,
                        mapperConfig,
                        parentBuilder
                );
        this.mapperConfig = mapperConfig;
        this.mappingContextFactoryBuilder = parentBuilder;
    }

    /**
     * @return a new instance of the jdbcMapper based on the current state of the builder.
     */
    public DatastaxMapper<T> mapper() {
        Mapper<Row, T> mapper = fieldMapperMapperBuilder.mapper();

        if (fieldMapperMapperBuilder.hasJoin()) {
            return new JoinDatastaxMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
        } else {
            return new StaticDatastaxMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
        }
    }

    /**
     * add a new mapping to the specified column with a key column definition and an undefined type.
     * The index is incremented for each non indexed column mapping.
     *
     * @param column the column name
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addKey(String column) {
        return addMapping(column, calculatedIndex++, new ColumnProperty[0]);
    }

    /**
     * add a new mapping to the specified column with an undefined type. The index is incremented for each non indexed column mapping.
     *
     * @param column the column name
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(String column) {
        return addMapping(column, calculatedIndex++);
    }

    /**
     * add a new mapping to the specified column with the specified columnDefinition and an undefined type. The index is incremented for each non indexed column mapping.
     *
     * @param column           the column name
     * @param properties the definition
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(final String column, final ColumnProperty... properties) {
        return addMapping(column, calculatedIndex++, properties);
    }

    /**
     * add a new mapping to the specified column with the specified index and an undefined type.
     *
     * @param column the column name
     * @param index  the column index
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(String column, int index) {
        return addMapping(column, index, (DataType)null);
    }

    /**
     * add a new mapping to the specified column with the specified index, specified column definition and an undefined type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param properties the column properties
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(String column, int index, final ColumnProperty... properties) {
        return addMapping(column, index, null, properties);
    }

    /**
     * append a FieldMapper to the mapping list.
     *
     * @param mapper the field jdbcMapper
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapper(FieldMapper<Row, T> mapper) {
        fieldMapperMapperBuilder.addMapper(mapper);
        return this;
    }

    /**
     * add a new mapping to the specified column with the specified index and the specified type.
     *
     * @param column  the column name
     * @param index   the column index
     * @param dataType the column type, @see java.sql.Types
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(final String column, final int index, final DataType dataType) {
        addMapping(column, index, dataType, new ColumnProperty[0]);
        return this;
    }

    /**
     * add a new mapping to the specified column with the specified index,  the specified type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param dataType          the column type, @see java.sql.Types
     * @param properties the column properties
     * @return the current builder
     */
    public DatastaxMapperBuilder<T> addMapping(final String column, final int index, final DataType dataType, ColumnProperty... properties) {
        return addMapping(new DatastaxColumnKey(column, index, dataType), properties);
    }

    /**
     * add the all the column present in the metaData
     *
     * @param metaData the metaDAta
     * @return the current builder
     * @throws SQLException when an error occurs getting the metaData
     */
    public DatastaxMapperBuilder<T> addMapping(final ColumnDefinitions metaData) throws SQLException {
        for (int i = 1; i <= metaData.size(); i++) {
            addMapping(metaData.getName(i), i, metaData.getType(i));
        }

        return this;
    }

    public DatastaxMapperBuilder<T> addMapping(DatastaxColumnKey key, ColumnProperty... properties) {
        fieldMapperMapperBuilder.addMapping(key, FieldMapperColumnDefinition.<DatastaxColumnKey, Row>of(properties));
        return this;
    }
}