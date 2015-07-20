package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.*;
import org.sfm.map.mapper.AbstractEnumarableDelegateMapper;
import org.sfm.map.impl.DiscriminatorEnumerable;
import org.sfm.tuples.Tuple2;
import org.sfm.tuples.Tuple3;
import org.sfm.utils.*;
import org.sfm.utils.conv.Converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



public final class DiscriminatorJdbcMapper<T> extends AbstractEnumarableDelegateMapper<ResultSet, ResultSet, T, SQLException> implements JdbcMapper<T> {


    private final String discriminatorColumn;
    private final List<Tuple2<Predicate<String>, JdbcMapper<T>>> mappers;

    public DiscriminatorJdbcMapper(String discriminatorColumn, List<Tuple2<Predicate<String>, JdbcMapper<T>>> mappers, RowHandlerErrorHandler rowHandlerErrorHandler) {
        super(rowHandlerErrorHandler);
        this.discriminatorColumn = discriminatorColumn;
        this.mappers = mappers;
    }

    @Override
    protected JdbcMapper<T> getMapper(final ResultSet rs) throws MappingException {
        String value = getDiscriminatorValue(rs);

        for (Tuple2<Predicate<String>, JdbcMapper<T>> tm : mappers) {
            if (tm.first().test(value)) {
                return tm.second();
            }
        }
        throw new MappingException("No jdbcMapper found for " + discriminatorColumn + " = " + value);
    }

    private String getDiscriminatorValue(ResultSet rs) {
        try {
            return rs.getString(discriminatorColumn);
        } catch(SQLException e) {
            return ErrorHelper.rethrow(e);
        }
    }

    protected DiscriminatorEnumerable<ResultSet, T> newEnumarableOfT(ResultSet rs) throws SQLException {
        @SuppressWarnings("unchecked") Tuple3<Predicate<ResultSet>, Mapper<ResultSet, T>, MappingContext<? super ResultSet>>[] mapperDiscriminators =
                new Tuple3[this.mappers.size()];

        for(int i = 0; i < mapperDiscriminators.length; i++) {

            Tuple2<Predicate<String>, JdbcMapper<T>> mapper = mappers.get(i);

            Predicate<ResultSet> discriminatorPredicate = new DiscriminatorPredicate(discriminatorColumn, mapper.first());

            mapperDiscriminators[i] =
                    new Tuple3<Predicate<ResultSet>, Mapper<ResultSet, T>, MappingContext<? super ResultSet>>(
                            discriminatorPredicate,
                            mapper.second(),
                            mapper.second().newMappingContext(rs));
        }

        return new DiscriminatorEnumerable<ResultSet, T>(
                mapperDiscriminators,
                new ResultSetEnumarable(rs),
                new ErrorMessageConverter(discriminatorColumn));
    }

    @Override
    public String toString() {
        return "DiscriminatorJdbcMapper{" +
                "discriminatorColumn='" + discriminatorColumn + '\'' +
                ", mappers=" + mappers +
                '}';
    }

    @Override
    public MappingContext<? super ResultSet> newMappingContext(ResultSet rs) throws SQLException {
        return getMapper(rs).newMappingContext(rs);
    }

    private static class DiscriminatorPredicate implements Predicate<ResultSet> {
        private final String discriminatorColumn;
        private final Predicate<String> predicate;

        public DiscriminatorPredicate(String discriminatorColumn, Predicate<String> predicate) {
            this.discriminatorColumn = discriminatorColumn;
            this.predicate = predicate;
        }

        @Override
        public boolean test(ResultSet resultSet) {
            try {
                return predicate.test(resultSet.getString(discriminatorColumn));
            } catch (SQLException e) {
                ErrorHelper.rethrow(e);
                return false;
            }
        }
    }

    private static class ErrorMessageConverter implements Converter<ResultSet, String> {
        private final String discriminatorColumn;

        private ErrorMessageConverter(String discriminatorColumn) {
            this.discriminatorColumn = discriminatorColumn;
        }

        @Override
        public String convert(ResultSet in) throws Exception {
            return " column " + discriminatorColumn + " = " + in.getObject(discriminatorColumn);
        }
    }

}
