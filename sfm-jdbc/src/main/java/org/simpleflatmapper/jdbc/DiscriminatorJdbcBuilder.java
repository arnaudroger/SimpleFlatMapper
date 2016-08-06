package org.simpleflatmapper.jdbc;


import org.simpleflatmapper.jdbc.impl.DiscriminatorJdbcMapper;
import org.simpleflatmapper.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * The builder is used to build a DiscriminatorJdbcMapper that will instantiate
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

        List<PredicatedJdbcMapper<T>> mappers =
                new ArrayList<PredicatedJdbcMapper<T>>();

        for(DiscriminatorJdbcSubBuilder subBuilder : builders) {
            JdbcMapper<T> mapper;

            if (subBuilder.builder != null) {
                mapper = subBuilder.builder.mapper();
            } else {
                mapper = jdbcMapperFactory.newMapper(subBuilder.type);
            }

            mappers.add(new PredicatedJdbcMapper<T>(subBuilder.predicate, mapper));
        }


        return new DiscriminatorJdbcMapper<T>(column, mappers, jdbcMapperFactory.rowHandlerErrorHandler());
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
            return "DiscriminatorPredicate{" +
                    "value='" + value + '\'' +
                    '}';
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
         * Static column definition.
         * @see JdbcMapperBuilder
         * @param column the column
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder addMapping(String column) {
            return addMapping(column, FieldMapperColumnDefinition.<JdbcColumnKey>identity());
        }

        /**
         * Static column definition.
         * @see JdbcMapperBuilder
         * @param column the column
         * @param columnDefinition the column definition
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder addMapping(String column, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
            if (builder == null) {
                builder = jdbcMapperFactory.newBuilder(type);
            }
            builder.addMapping(column, columnDefinition);
            return this;
        }

        /**
         * Static column definition.
         * @see JdbcMapperBuilder
         * @param column the column
         * @param index the column index
         * @param columnDefinition the column definition
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder addMapping(String column, int index, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
            if (builder == null) {
                builder = jdbcMapperFactory.newBuilder(type);
            }
            builder.addMapping(column, index, columnDefinition);
            return this;
        }

        /**
         * @see DiscriminatorJdbcBuilder
         * @return return a DiscriminatorJdbcMapper based on the current state of the builder
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
            return DiscriminatorJdbcBuilder.this.when(value, type);
        }

        /**
         * Add a discriminator value with its associated type.
         * @param value the value
         * @param type the type
         * @return the current builder
         */
        public DiscriminatorJdbcSubBuilder when(String value, TypeReference<? extends T> type) {
            return DiscriminatorJdbcBuilder.this.when(value, type);
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
    }

    //Tuple2<Predicate<String>, JdbcMapper<T>>
    public static class PredicatedJdbcMapper<T> {
        private final Predicate<String> predicate;
        private final JdbcMapper<T> jdbcMapper;

        private PredicatedJdbcMapper(Predicate<String> predicate, JdbcMapper<T> jdbcMapper) {
            this.predicate = predicate;
            this.jdbcMapper = jdbcMapper;
        }

        public Predicate<String> getPredicate() {
            return predicate;
        }

        public JdbcMapper<T> getJdbcMapper() {
            return jdbcMapper;
        }
    }
 }
