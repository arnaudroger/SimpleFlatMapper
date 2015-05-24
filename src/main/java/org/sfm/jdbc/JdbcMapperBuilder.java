package org.sfm.jdbc;

import org.sfm.jdbc.impl.*;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.*;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.FieldMapperFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @param <T> the targeted type of the mapper
 */
public final class JdbcMapperBuilder<T> {

    private int calculatedIndex = 1;

    private RowHandlerErrorHandler jdbcMapperErrorHandler = new RethrowRowHandlerErrorHandler();
    private final FieldMapperMapperBuilder<ResultSet, T, JdbcColumnKey> fieldMapperMapperBuilder;

    /**
     * Build a new JdbcMapperBuilder targeting the type specified by the TypeReference. The TypeReference
     * allow you to provide a generic type with check of T<br>
     * <code>new TypeReference&lt;List&lt;String&gt;&gt;() {}</code>
     *
     * @param target the TypeReference to the type T to map to
     */
    public JdbcMapperBuilder(final TypeReference<T> target) {
        this(target.getType());
    }

    /**
     * Build a new JdbcMapperBuilder targeting the specified type.
     *
     * @param target the type
     */
    public JdbcMapperBuilder(final Type target) {
        this(target, ReflectionService.newInstance());
    }

    /**
     * Build a new JdbcMapperBuilder targeting the specified type with the specified ReflectionService.
     *
     * @param target         the type
     * @param reflectService the ReflectionService
     */
    public JdbcMapperBuilder(final Type target, ReflectionService reflectService) {
        this(reflectService.<T>getClassMeta(target),
                new RethrowMapperBuilderErrorHandler(),
                new IdentityFieldMapperColumnDefinitionProvider<JdbcColumnKey, ResultSet>(),
                new DefaultPropertyNameMatcherFactory(),
                new ResultSetGetterFactory(),
                false,
                FieldMapperMapperBuilder.NO_ASM_MAPPER_THRESHOLD,
                new JdbcMappingContextFactoryBuilder());
    }

    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperBuilderErrorHandler  the error handler.
     * @param columnDefinitions          the predefined ColumnDefinition.
     * @param propertyNameMatcherFactory the PropertyNameMatcher factory.
     * @param getterFactory              the Getter factory.
     * @param failOnAsm                  should we fail on asm generation.
     * @param parentBuilder              the parent builder, null if none.
     */
    public JdbcMapperBuilder(final ClassMeta<T> classMeta, final MapperBuilderErrorHandler mapperBuilderErrorHandler,
                             final ColumnDefinitionProvider<FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>, JdbcColumnKey> columnDefinitions,
                             PropertyNameMatcherFactory propertyNameMatcherFactory,
                             GetterFactory<ResultSet, JdbcColumnKey> getterFactory, boolean failOnAsm, int asmMapperNbFieldsLimit,
                             MappingContextFactoryBuilder<ResultSet, JdbcColumnKey> parentBuilder) {
        this.fieldMapperMapperBuilder =
                new FieldMapperMapperBuilder<ResultSet, T, JdbcColumnKey>(
                        ResultSet.class,
                        classMeta,
                        getterFactory,
                        columnDefinitions,
                        propertyNameMatcherFactory,
                        mapperBuilderErrorHandler,
                        parentBuilder,
                        failOnAsm, asmMapperNbFieldsLimit
                );
    }

    /**
     * @return a new instance of the mapper based on the current state of the builder.
     */
    public JdbcMapper<T> mapper() {
        Mapper<ResultSet, T> mapper = fieldMapperMapperBuilder.mapper();

        if (fieldMapperMapperBuilder.hasJoin()) {
            return new JoinJdbcMapper<T>(mapper, jdbcMapperErrorHandler);
        } else {
            return new JdbcMapperImpl<T>(mapper, jdbcMapperErrorHandler);
        }
    }

    /**
     * add a new mapping to the specified column with a key column definition and an undefined type.
     * The index is incremented for each non indexed column mapping.
     *
     * @param column the column name
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addKey(String column) {
        return addMapping(column, calculatedIndex++, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>key());
    }

    /**
     * add a new mapping to the specified column with an undefined type. The index is incremented for each non indexed column mapping.
     *
     * @param column the column name
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(String column) {
        return addMapping(column, calculatedIndex++);
    }

    /**
     * add a new mapping to the specified column with the specified columnDefinition and an undefined type. The index is incremented for each non indexed column mapping.
     *
     * @param column           the column name
     * @param columnDefinition the definition
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
        return addMapping(column, calculatedIndex++, columnDefinition);
    }

    /**
     * add a new mapping to the specified column with the specified index and an undefined type.
     *
     * @param column the column name
     * @param index  the column index
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(String column, int index) {
        return addMapping(column, index, JdbcColumnKey.UNDEFINED_TYPE);
    }

    /**
     * add a new mapping to the specified column with the specified index, specified column definition and an undefined type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param columnDefinition the column definition
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(String column, int index, final FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
        return addMapping(column, index, JdbcColumnKey.UNDEFINED_TYPE, columnDefinition);
    }

    /**
     * append a FieldMapper to the mapping list.
     *
     * @param mapper the field mapper
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapper(FieldMapper<ResultSet, T> mapper) {
        fieldMapperMapperBuilder.addMapper(mapper);
        return this;
    }

    /**
     * add a new mapping to the specified column with the specified index and the specified type.
     *
     * @param column  the column name
     * @param index   the column index
     * @param sqlType the column type, @see java.sql.Types
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType) {
        addMapping(column, index, sqlType, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>identity());
        return this;
    }

    /**
     * add a new mapping to the specified column with the specified index,  the specified type.
     *
     * @param column           the column name
     * @param index            the column index
     * @param sqlType          the column type, @see java.sql.Types
     * @param columnDefinition the column definition
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
        fieldMapperMapperBuilder.addMapping(new JdbcColumnKey(column, index, sqlType), columnDefinition);
        return this;
    }

    /**
     * add the all the column present in the metaData
     *
     * @param metaData the metaDAta
     * @return the current builder
     * @throws SQLException when an error occurs getting the metaData
     */
    public JdbcMapperBuilder<T> addMapping(final ResultSetMetaData metaData) throws SQLException {
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            addMapping(metaData.getColumnLabel(i), i, metaData.getColumnType(i));
        }

        return this;
    }

    /**
     * the FieldMapperErrorHandler is called when a error occurred when mapping a field from the source to the target.
     * By default it just throw the Exception.
     * @param errorHandler the new FieldMapperErrorHandler
     * @return the current builder
     */
    public JdbcMapperBuilder<T> fieldMapperErrorHandler(FieldMapperErrorHandler<JdbcColumnKey> errorHandler) {
        fieldMapperMapperBuilder.setFieldMapperErrorHandler(errorHandler);
        return this;
    }

    /**
     * the RowHandlerErrorHandler is called when an exception is thrown by the RowHandler in the forEach call.
     * @param jdbcMapperErrorHandler the new RowHandlerErrorHandler
     * @return the current builder
     */
    public JdbcMapperBuilder<T> jdbcMapperErrorHandler(RowHandlerErrorHandler jdbcMapperErrorHandler) {
        this.jdbcMapperErrorHandler = jdbcMapperErrorHandler;
        return this;
    }

}