package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.jdbc.impl.JdbcKeySourceGetter;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.DefaultSetRowMapperBuilder;
import org.simpleflatmapper.map.mapper.MapperBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import java.util.List;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END
/**
 * @param <T> the targeted type of the jdbcMapper
 */
public final class JdbcMapperBuilder<T> extends MapperBuilder<ResultSet, ResultSet, T, JdbcColumnKey, SQLException, SetRowMapper<ResultSet, ResultSet, T, SQLException>, JdbcMapper<T>, JdbcMapperBuilder<T>> {

    private static final MapperSourceImpl<ResultSet, JdbcColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<ResultSet, JdbcColumnKey>(ResultSet.class,  new ContextualGetterFactoryAdapter<ResultSet, JdbcColumnKey>(ResultSetGetterFactory.INSTANCE));
    private static final KeyFactory<JdbcColumnKey> KEY_FACTORY = new KeyFactory<JdbcColumnKey>() {
        @Override
        public JdbcColumnKey newKey(String name, int i) {
            return new JdbcColumnKey(name, i);
        }
    };
    public static final Function<Object[], ColumnDefinition<JdbcColumnKey, ?>> COLUMN_DEFINITION_FACTORY = FieldMapperColumnDefinition.factory();

    private final MappingContextFactoryBuilder<ResultSet, JdbcColumnKey> mappingContextFactoryBuilder;


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
                MapperConfig.<JdbcColumnKey, ResultSet>fieldMapperConfig(),
                (ContextualGetterFactory<ResultSet, JdbcColumnKey>)ResultSetGetterFactory.INSTANCE,
                new JdbcMappingContextFactoryBuilder(!MapperConfig.<JdbcColumnKey, ResultSet>fieldMapperConfig().unorderedJoin()));
    }



    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    public JdbcMapperBuilder(
            final ClassMeta<T> classMeta,
            final MapperConfig<JdbcColumnKey, ResultSet> mapperConfig,
            final GetterFactory<ResultSet, JdbcColumnKey> getterFactory,
            final MappingContextFactoryBuilder<ResultSet, JdbcColumnKey> parentBuilder) {
        this(classMeta, mapperConfig, new ContextualGetterFactoryAdapter<ResultSet, JdbcColumnKey>(getterFactory), parentBuilder);
    }

    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    public JdbcMapperBuilder(
             final ClassMeta<T> classMeta,
             final MapperConfig<JdbcColumnKey, ResultSet> mapperConfig,
             final ContextualGetterFactory<? super ResultSet, JdbcColumnKey> getterFactory,
             final MappingContextFactoryBuilder<ResultSet, JdbcColumnKey> parentBuilder) {

        super(KEY_FACTORY, 
                new DefaultSetRowMapperBuilder<ResultSet, ResultSet, T, JdbcColumnKey, SQLException>(
                        classMeta,
                        parentBuilder,
                        mapperConfig,
                        FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                        KEY_FACTORY,
                        new ResultSetEnumerableFactory(),
                        JdbcKeySourceGetter.INSTANCE),
                new BiFunction<SetRowMapper<ResultSet, ResultSet, T, SQLException>, List<JdbcColumnKey>, JdbcMapper<T>>() {
                    @Override
                    public JdbcMapper<T> apply(SetRowMapper<ResultSet, ResultSet, T, SQLException> setRowMapper, List<JdbcColumnKey> keys) {
                        return new JdbcMapperImpl<T>(setRowMapper, parentBuilder.build());
                    }
                }, 
                COLUMN_DEFINITION_FACTORY,
                1 );
        this.mappingContextFactoryBuilder = parentBuilder;   
    }


    /**
     * add a new mapping to the specified property with the specified index and the specified type.
     *
     * @param column  the property name
     * @param index   the property index
     * @param sqlType the property type, @see java.sql.Types
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType) {
        addMapping(column, index, sqlType, FieldMapperColumnDefinition.<JdbcColumnKey>identity());
        return this;
    }

    /**
     * add a new mapping to the specified property with the specified index,  the specified type.
     *
     * @param column           the property name
     * @param index            the property index
     * @param sqlType          the property type, @see java.sql.Types
     * @param columnDefinition the property definition
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
        return addMapping(new JdbcColumnKey(column, index, sqlType), columnDefinition);
    }

    /**
     * add a new mapping to the specified property with the specified index,  the specified type.
     *
     * @param column           the property name
     * @param index            the property index
     * @param sqlType          the property type, @see java.sql.Types
     * @param properties the property properties
     * @return the current builder
     */
    public JdbcMapperBuilder<T> addMapping(final String column, final int index, final int sqlType, Object... properties) {
        return addMapping(new JdbcColumnKey(column, index, sqlType), properties);
    }

    /**
     * add the all the property present in the metaData
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


    public JdbcSourceFieldMapper<T> newSourceFieldMapper() {
        return new JdbcSourceFieldMapperImpl<T>(super.sourceFieldMapper(), mappingContextFactoryBuilder.build());
    }
    
    private static class JdbcSourceFieldMapperImpl<T> implements JdbcSourceFieldMapper<T> {
        private final SourceFieldMapper<ResultSet, T> sourceFieldMapper;
        private final MappingContextFactory<? super ResultSet> mappingContextFactory;

        private JdbcSourceFieldMapperImpl(SourceFieldMapper<ResultSet, T> sourceFieldMapper, MappingContextFactory<? super ResultSet> mappingContextFactory) {
            this.sourceFieldMapper = sourceFieldMapper;
            this.mappingContextFactory = mappingContextFactory;
        }

        @Override
        public void mapTo(ResultSet source, T target, MappingContext<? super ResultSet> context) throws Exception {
            sourceFieldMapper.mapTo(source, target, context);
        }

        @Override
        public T map(ResultSet source, MappingContext<? super ResultSet> context) throws MappingException {
            return sourceFieldMapper.map(source, context);
        }

        @Override
        public T map(ResultSet source) throws MappingException {
            return sourceFieldMapper.map(source, mappingContextFactory.newContext());
        }

        @Override
        public MappingContext<? super ResultSet> newMappingContext(ResultSet resultSet) throws SQLException {
            return mappingContextFactory.newContext();
        }

    }
    
    private static class JdbcMapperImpl<T> implements JdbcMapper<T> {
        private final SetRowMapper<ResultSet, ResultSet, T, SQLException> setRowMapper;
        private final MappingContextFactory<? super ResultSet> mappingContextFactory;

        private JdbcMapperImpl(SetRowMapper<ResultSet, ResultSet, T, SQLException> setRowMapper, MappingContextFactory<? super ResultSet> mappingContextFactory) {
            this.setRowMapper = setRowMapper;
            this.mappingContextFactory = mappingContextFactory;
        }

        @Override
        public T map(ResultSet source) throws MappingException {
            return setRowMapper.map(source);
        }

        @Override
        public T map(ResultSet source, MappingContext<? super ResultSet> context) throws MappingException {
            return setRowMapper.map(source, context);
        }

        @Override
        public <H extends CheckedConsumer<? super T>> H forEach(ResultSet source, H handler) throws SQLException, MappingException {
            return setRowMapper.forEach(source, handler);
        }

        @Override
        public Iterator<T> iterator(ResultSet source) throws SQLException, MappingException {
            return setRowMapper.iterator(source);
        }

        @Override
        public Enumerable<T> enumerate(ResultSet source) throws SQLException, MappingException {
            return setRowMapper.enumerate(source);
        }

        //IFJAVA8_START
        @Override
        public Stream<T> stream(ResultSet source) throws SQLException, MappingException {
            return setRowMapper.stream(source);
        }
        //IFJAVA8_END

        @Override
        public MappingContext<? super ResultSet> newMappingContext(ResultSet resultSet) throws SQLException {
            return mappingContextFactory.newContext();
        }

        @Override
        public String toString() {
            return "JdbcMapperImpl{" +
                    "setRowMapper=" + setRowMapper +
                    ", mappingContextFactory=" + mappingContextFactory +
                    '}';
        }
    }

    private static class ResultSetEnumerableFactory implements UnaryFactory<ResultSet, Enumerable<ResultSet>> {
        @Override
        public Enumerable<ResultSet> newInstance(ResultSet rows) {
            return new ResultSetEnumerable(rows);
        }
    }
    
}