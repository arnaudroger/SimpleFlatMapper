package org.simpleflatmapper.jdbc;


import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.DiscriminatorMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The builder is used to build a DiscriminatorMapper that will instantiate
 * different types depending on the value of a specified field.
 * @param <T> the root type of the jdbcMapper
 */
public class DiscriminatorJdbcBuilder<T> {


    private final String column;
    private final JdbcMapperFactory jdbcMapperFactory;
    private final List<DiscriminatorJdbcSubBuilder> builders = new ArrayList<DiscriminatorJdbcSubBuilder>();

    public DiscriminatorJdbcBuilder(String column,JdbcMapperFactory jdbcMapperFactory) {
        this.column = column;
        this.jdbcMapperFactory = jdbcMapperFactory;
    }

    /**
     * Add a discriminator value with its associated type.
     * @param value the value
     * @param type the type
     * @return the current builder
     */
    public DiscriminatorJdbcSubBuilder when(String value, Type type) {
        return when(new DiscriminatorPredicate(value), type);
    }

    /**
     * Add a discriminator matching predicate with its associated type.
     * @param predicate the predicate
     * @param type the type
     * @return the current builder
     */
    public DiscriminatorJdbcSubBuilder when(Predicate<String> predicate, Type type) {
        final DiscriminatorJdbcSubBuilder subBuilder = new DiscriminatorJdbcSubBuilder(predicate, type);
        builders.add(subBuilder);
        return subBuilder;
    }

    /**
     * Add a discriminator value with its associated class.
     * @param value the value
     * @param type the class
     * @return the current builder
     */
    public DiscriminatorJdbcSubBuilder when(String value, Class<? extends T> type) {
        return when(value, (Type)type);
    }

    /**
     * Add a discriminator value with its associated type specified by the type reference.
     * @param value the value
     * @param type the type reference
     * @return the current builder
     */
    public DiscriminatorJdbcSubBuilder when(String value, TypeReference<? extends T> type) {
        return when(value, type.getType());
    }

    /**
     *
     * @return a new jdbcMapper based on the current state
     */
    public JdbcMapper<T> mapper() {

        List<DiscriminatorMapper.PredicatedMapper<ResultSet, ResultSet, T, SQLException>> mappers =
                new ArrayList<DiscriminatorMapper.PredicatedMapper<ResultSet, ResultSet, T, SQLException>>();

        for(DiscriminatorJdbcSubBuilder subBuilder : builders) {
            JdbcSourceFieldMapper<T> mapper = subBuilder.createMapper();
            
            Predicate<ResultSet> predicate = new ResultSetDiscriminatorPredicate(column, subBuilder.predicate);
            mappers.add(new DiscriminatorMapper.PredicatedMapper<ResultSet, ResultSet, T, SQLException>(predicate, mapper, mapper));
        }


        DiscriminatorJdbcMapper<T> discriminatorMapper = new DiscriminatorJdbcMapper<T>(
                mappers,
                new UnaryFactory<ResultSet, Enumerable<ResultSet>>() {
                    @Override
                    public Enumerable<ResultSet> newInstance(ResultSet resultSet) {
                        return new ResultSetEnumerable(resultSet);
                    }
                },
                new Function<ResultSet, String>() {
                    @Override
                    public String apply(ResultSet in) {
                        try {
                            return column + ":" + in.getObject(column);
                        } catch (SQLException e) {
                            return ErrorHelper.rethrow(e);
                        }
                    }
                },
                jdbcMapperFactory.consumerErrorHandler());
        return discriminatorMapper;
    }


    private static class DiscriminatorJdbcMapper<T> extends DiscriminatorMapper<ResultSet, ResultSet, T, SQLException>
            implements JdbcMapper<T> {

        public DiscriminatorJdbcMapper(List<PredicatedMapper<ResultSet, ResultSet, T, SQLException>> predicatedMappers,
                                       UnaryFactory<ResultSet, Enumerable<ResultSet>> rowEnumerableFactory,
                                       Function<ResultSet, String> errorConverter,
                                       ConsumerErrorHandler consumerErrorHandler) {
            super(predicatedMappers, rowEnumerableFactory, errorConverter, consumerErrorHandler);
        }

        @Override
        public MappingContext<? super ResultSet> newMappingContext(ResultSet resultSet) throws SQLException {
            return ((JdbcSourceFieldMapper<T>)getMapper(resultSet)).newMappingContext(resultSet);
        }
    }

    private static class DiscriminatorPredicate implements Predicate<String> {
        private final String value;

        private DiscriminatorPredicate(String value) {
            this.value = value;
        }

        @Override
        public boolean test(String discriminatorValue) {
            return value == null ? discriminatorValue == null : value.equals(discriminatorValue);
        }

        @Override
        public String toString() {
            return "DiscriminatorPredicate{value='" + value + '\'' + '}';
        }
    }

    private static class ResultSetDiscriminatorPredicate implements Predicate<ResultSet> {
        private final String discriminatorColumn;
        private final Predicate<String> predicate;

        public ResultSetDiscriminatorPredicate(String discriminatorColumn, Predicate<String> predicate) {
            this.discriminatorColumn = discriminatorColumn;
            this.predicate = predicate;
        }

        @Override
        public boolean test(ResultSet resultSet) {
            try {
                return predicate.test(resultSet.getString(discriminatorColumn));
            } catch (SQLException e) {
                return ErrorHelper.<Boolean>rethrow(e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ResultSetDiscriminatorPredicate that = (ResultSetDiscriminatorPredicate) o;

            if (discriminatorColumn != null ? !discriminatorColumn.equals(that.discriminatorColumn) : that.discriminatorColumn != null)
                return false;
            return predicate != null ? predicate.equals(that.predicate) : that.predicate == null;
        }

        @Override
        public int hashCode() {
            int result = discriminatorColumn != null ? discriminatorColumn.hashCode() : 0;
            result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
            return result;
        }
    }

    public class DiscriminatorJdbcSubBuilder {

        private final Type type;
        private final Predicate<String> predicate;
        private JdbcMapperBuilder<T> builder = null;

        public DiscriminatorJdbcSubBuilder(Predicate<String> predicate, Type type) {
            this.type = type;
            this.predicate = predicate;
        }

        /**
         * Static property definition.
         * @see JdbcMapperBuilder
         * @param column the property
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder addMapping(String column) {
            return addMapping(column, FieldMapperColumnDefinition.<JdbcColumnKey>identity());
        }

        /**
         * Static property definition.
         * @see JdbcMapperBuilder
         * @param column the property
         * @param columnDefinition the property definition
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder addMapping(String column, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
            initBuilder();
            builder.addMapping(column, columnDefinition);
            return this;
        }

        private void initBuilder() {
            if (builder == null) {
                builder = jdbcMapperFactory.newBuilder(type);
            }
        }

        /**
         * Static property definition.
         * @see JdbcMapperBuilder
         * @param column the property
         * @param index the property index
         * @param columnDefinition the property definition
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder addMapping(String column, int index, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
            initBuilder();
            builder.addMapping(column, index, columnDefinition);
            return this;
        }

        /**
         * @see DiscriminatorJdbcBuilder
         * @return return a DiscriminatorMapper based on the current state of the builder
         */
        public JdbcMapper<T> mapper() {
            return DiscriminatorJdbcBuilder.this.mapper();
        }

        /**
         * Add a discriminator matching predicate with its associated type.
         * @param value the value
         * @param type the type
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder when(String value, Type type) {
            return DiscriminatorJdbcBuilder.this.when(value, type);
        }

        /**
         * Add a discriminator value with its associated type.
         * @param value the value
         * @param type the type
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder when(String value, Class<? extends T> type) {
            return when(value, (Type)type);
        }

        /**
         * Add a discriminator value with its associated type.
         * @param value the value
         * @param type the type
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder when(String value, TypeReference<? extends T> type) {
            return when(value, type.getType());
        }

        /**
         * Add a discriminator matching predicate with its associated type.
         * @param predicate the predicate
         * @param type the type
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder when(Predicate<String> predicate, Type type) {
            return DiscriminatorJdbcBuilder.this.when(predicate, type);
        }

        private JdbcSourceFieldMapper<T> createMapper() {
            if (builder != null) {
                return builder.newSourceFieldMapper();
            } else {
                return jdbcMapperFactory.newSourceFieldMapper(type);
            }
        }
    }
 }
