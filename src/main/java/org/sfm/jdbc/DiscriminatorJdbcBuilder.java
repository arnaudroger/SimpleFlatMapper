package org.sfm.jdbc;


import org.sfm.jdbc.impl.DiscriminatorJdbcMapper;
import org.sfm.map.Mapper;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.reflect.TypeReference;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DiscriminatorJdbcBuilder<T> {


    private final String column;
    private final JdbcMapperFactory jdbcMapperFactory;
    private List<DiscriminatorJdbcSubBuilder> builders = new ArrayList<DiscriminatorJdbcSubBuilder>();

    public DiscriminatorJdbcBuilder(String column, Type type, JdbcMapperFactory jdbcMapperFactory) {
        this.column = column;
        this.jdbcMapperFactory = jdbcMapperFactory;
    }

    public DiscriminatorJdbcSubBuilder when(String value, Type type) {
        final DiscriminatorJdbcSubBuilder subBuilder = new DiscriminatorJdbcSubBuilder(value, type);
        builders.add(subBuilder);
        return subBuilder;
    }
    public DiscriminatorJdbcSubBuilder when(String value, Class<T> clazz) {
        return when(value, (Type)clazz);
    }

    public DiscriminatorJdbcSubBuilder when(String value, TypeReference<T> clazz) {
        return when(value, clazz.getType());
    }



    public JdbcMapper<T> mapper() {

        List<Tuple2<Predicate<String>, Mapper<ResultSet, T>>> mappers =
                new ArrayList<Tuple2<Predicate<String>, Mapper<ResultSet, T>>>();

        for(DiscriminatorJdbcSubBuilder subBuilder : builders) {
            Mapper<ResultSet, T> mapper;

            if (subBuilder.builder != null) {
                mapper = subBuilder.builder.mapper();
            } else {
                mapper = jdbcMapperFactory.newMapper(subBuilder.type);
            }

            Predicate<String> predicate = new DiscriminatorPredicate(subBuilder.value);

            mappers.add(new Tuple2<Predicate<String>, Mapper<ResultSet, T>>(predicate, mapper));
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
    }

    public class DiscriminatorJdbcSubBuilder {

        private final Type type;
        private final String value;
        private JdbcMapperBuilder<T> builder = null;

        public DiscriminatorJdbcSubBuilder(String value, Type type) {
            this.type = type;
            this.value = value;
        }

        public DiscriminatorJdbcSubBuilder addMapping(String column) {
            return addMapping(column, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>identity());
        }

        public DiscriminatorJdbcSubBuilder addMapping(String column, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
            if (builder == null) {
                builder = jdbcMapperFactory.newBuilder(type);
            }
            builder.addMapping(column, columnDefinition);
            return this;
        }

        public DiscriminatorJdbcSubBuilder addMapping(String column, int index, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
            if (builder == null) {
                builder = jdbcMapperFactory.newBuilder(type);
            }
            builder.addMapping(column, index, columnDefinition);
            return this;
        }

        public JdbcMapper<T> mapper() {
            return DiscriminatorJdbcBuilder.this.mapper();
        }

        public DiscriminatorJdbcSubBuilder when(String value, Type type) {
            return DiscriminatorJdbcBuilder.this.when(value, type);
        }
        public DiscriminatorJdbcSubBuilder when(String value, Class<T> type) {
            return DiscriminatorJdbcBuilder.this.when(value, type);
        }
        public DiscriminatorJdbcSubBuilder when(String value, TypeReference<T> type) {
            return DiscriminatorJdbcBuilder.this.when(value, type);
        }
    }

}
