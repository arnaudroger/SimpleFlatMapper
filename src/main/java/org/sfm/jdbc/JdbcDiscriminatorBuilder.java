package org.sfm.jdbc;


import org.sfm.jdbc.impl.DiscriminatorJdbcMapper;
import org.sfm.map.Mapper;
import org.sfm.reflect.TypeReference;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JdbcDiscriminatorBuilder<T> {


    private final String column;
    private final Type root;
    private final JdbcMapperFactory jdbcMapperFactory;
    private List<JdbcDiscriminatorSubBuilder> builders = new ArrayList<JdbcDiscriminatorSubBuilder>();

    public JdbcDiscriminatorBuilder(String column, Type type, JdbcMapperFactory jdbcMapperFactory) {
        this.column = column;
        this.root = type;
        this.jdbcMapperFactory = jdbcMapperFactory;
    }

    public JdbcDiscriminatorSubBuilder when(String value, Type type) {
        final JdbcDiscriminatorSubBuilder subBuilder = new JdbcDiscriminatorSubBuilder(value, type);
        builders.add(subBuilder);
        return subBuilder;
    }
    public JdbcDiscriminatorSubBuilder when(String value, Class<T> clazz) {
        return when(value, (Type)clazz);
    }
    public JdbcDiscriminatorSubBuilder when(String value, TypeReference<T> clazz) {
        return when(value, clazz.getType());
    }



    public JdbcMapper<T> mapper() {

        List<Tuple2<Predicate<String>, Mapper<ResultSet, T>>> mappers =
                new ArrayList<Tuple2<Predicate<String>, Mapper<ResultSet, T>>>();

        for(JdbcDiscriminatorSubBuilder subBuilder : builders) {
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

    public class JdbcDiscriminatorSubBuilder {

        private final Type type;
        private final String value;
        private JdbcMapperBuilder<T> builder = null;

        public JdbcDiscriminatorSubBuilder(String value, Type type) {
            this.type = type;
            this.value = value;
        }

        public JdbcDiscriminatorSubBuilder addMapping(String column) {
            if (builder == null) {
                builder = jdbcMapperFactory.newBuilder(type);
            }
            builder.addMapping(column);
            return this;
        }

        public JdbcMapper<T> mapper() {
            return JdbcDiscriminatorBuilder.this.mapper();
        }

        public JdbcDiscriminatorSubBuilder when(String value, Type type) {
            return JdbcDiscriminatorBuilder.this.when(value, type);
        }
        public JdbcDiscriminatorSubBuilder when(String value, Class<T> type) {
            return JdbcDiscriminatorBuilder.this.when(value, type);
        }
        public JdbcDiscriminatorSubBuilder when(String value, TypeReference<T> type) {
            return JdbcDiscriminatorBuilder.this.when(value, type);
        }
    }

}
