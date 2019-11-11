package org.simpleflatmapper.jooq;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.ResultSetEnumerable;
import org.simpleflatmapper.jdbc.ResultSetGetterFactory;
import org.simpleflatmapper.map.*;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.*;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
/**
 * @param <T> the targeted type of the jdbcMapper
 */
final class JooqJdbcMapperBuilder<T> extends MapperBuilder<ResultSet, ResultSet, T, JooqFieldKey, SQLException, SetRowMapper<ResultSet, ResultSet, T, SQLException>, SetRowMapper<ResultSet, ResultSet, T, SQLException>, JooqJdbcMapperBuilder<T>> {

    public static final ResultSetJooqFieldKeyGetterFactory GETTER_FACTORY = new ResultSetJooqFieldKeyGetterFactory();
    private static final MapperSourceImpl<ResultSet, JooqFieldKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<ResultSet, JooqFieldKey>(ResultSet.class,  new ContextualGetterFactoryAdapter<ResultSet, JooqFieldKey>(
                    GETTER_FACTORY
            ));
    public static final KeySourceGetter<JooqFieldKey, ResultSet> KEY_SOURCE_GETTER = new KeySourceGetter<JooqFieldKey, ResultSet>() {
        @Override
        public Object getValue(JooqFieldKey key, ResultSet source) throws Exception {
            return source.getObject(key.getIndex());
        }
    };

    static JdbcColumnKey toJdbcColumnKey(JooqFieldKey key) {
        return new JdbcColumnKey(key.getName(), key.getIndex(), key.getField().getDataType().getSQLType(), key.getParent() != null ? toJdbcColumnKey(key.getParent()) : null);
    }


    public static final Function<Object[], ColumnDefinition<JooqFieldKey, ?>> COLUMN_DEFINITION_FACTORY = FieldMapperColumnDefinition.factory();
    private static final KeyFactory<JooqFieldKey> KEY_FACTORY = new KeyFactory<JooqFieldKey>() {
        @Override
        public JooqFieldKey newKey(String name, int i) {
            throw new UnsupportedOperationException();
        }
    };


    JooqJdbcMapperBuilder(
            final ClassMeta<T> classMeta,
            final MapperConfig<JooqFieldKey, ResultSet> mapperConfig) {
        this(classMeta, mapperConfig,
                (ContextualGetterFactory<ResultSet, JooqFieldKey>)GETTER_FACTORY,
                new JdbcMappingContextFactoryBuilder(!MapperConfig.<JdbcColumnKey, ResultSet>fieldMapperConfig().unorderedJoin())
                );

    }

    /**
     * @param classMeta                  the meta for the target class.
     * @param mapperConfig               the mapperConfig.
     * @param getterFactory              the Getter factory.
     * @param parentBuilder              the parent builder, null if none.
     */
    JooqJdbcMapperBuilder(
             final ClassMeta<T> classMeta,
             final MapperConfig<JooqFieldKey, ResultSet> mapperConfig,
             final ContextualGetterFactory<? super ResultSet, JooqFieldKey> getterFactory,
             final MappingContextFactoryBuilder<ResultSet, JooqFieldKey> parentBuilder) {

        super(KEY_FACTORY, 
                new DefaultSetRowMapperBuilder<ResultSet, ResultSet, T, JooqFieldKey, SQLException>(
                        classMeta,
                        parentBuilder,
                        mapperConfig,
                        FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                        KEY_FACTORY,
                        new ResultSetEnumerableFactory(),
                        KEY_SOURCE_GETTER),
                new BiFunction<SetRowMapper<ResultSet, ResultSet, T, SQLException>, List<JooqFieldKey>, SetRowMapper<ResultSet, ResultSet, T, SQLException>>() {
                    @Override
                    public SetRowMapper<ResultSet, ResultSet, T, SQLException> apply(SetRowMapper<ResultSet, ResultSet, T, SQLException> setRowMapper, List<JooqFieldKey> keys) {
                        return setRowMapper;
                    }
                }, 
                COLUMN_DEFINITION_FACTORY,
                1 );
    }



    private static class ResultSetEnumerableFactory implements UnaryFactory<ResultSet, Enumerable<ResultSet>> {
        @Override
        public Enumerable<ResultSet> newInstance(ResultSet rows) {
            return new ResultSetEnumerable(rows);
        }
    }


    private static class JdbcMappingContextFactoryBuilder extends MappingContextFactoryBuilder<ResultSet, JooqFieldKey> {
        private JdbcMappingContextFactoryBuilder(boolean ignoreRootKey) {
            super(KEY_SOURCE_GETTER, ignoreRootKey);
        }

    }

    private static class ResultSetJooqFieldKeyGetterFactory implements GetterFactory<ResultSet, JooqFieldKey>, ContextualGetterFactory<ResultSet, JooqFieldKey> {

        private final ResultSetGetterFactory delegate = ResultSetGetterFactory.INSTANCE;

        @Override
        public <P> Getter<ResultSet, P> newGetter(Type target, JooqFieldKey key, Object... properties) {
            return delegate.newGetter(target, toJdbcColumnKey(key), properties);
        }

        @Override
        public <P> ContextualGetter<ResultSet, P> newGetter(Type target, JooqFieldKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
            ContextualGetterFactory<ResultSet, JdbcColumnKey> delegate = this.delegate;
            return delegate.newGetter(target, toJdbcColumnKey(key), mappingContextFactoryBuilder, properties);
        }

    }
}