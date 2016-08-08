package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.DiscriminatorJdbcBuilder;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.RowHandlerErrorHandler;
import org.simpleflatmapper.map.mapper.AbstractEnumarableDelegateMapper;
import org.simpleflatmapper.map.DiscriminatorEnumerable;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.converter.Converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



public final class DiscriminatorJdbcMapper<T> extends AbstractEnumarableDelegateMapper<ResultSet, ResultSet, T, SQLException> implements JdbcMapper<T> {


    private final String discriminatorColumn;
    private final List<DiscriminatorJdbcBuilder.PredicatedJdbcMapper<T>> mappers;

    public DiscriminatorJdbcMapper(String discriminatorColumn, List<DiscriminatorJdbcBuilder.PredicatedJdbcMapper<T>> mappers, RowHandlerErrorHandler rowHandlerErrorHandler) {
        super(rowHandlerErrorHandler);
        this.discriminatorColumn = discriminatorColumn;
        this.mappers = mappers;
    }

    @Override
    protected JdbcMapper<T> getMapper(final ResultSet rs) throws MappingException {
        String value = getDiscriminatorValue(rs);

        for (DiscriminatorJdbcBuilder.PredicatedJdbcMapper<T> tm : mappers) {
            if (tm.getPredicate().test(value)) {
                return tm.getJdbcMapper();
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

    @SuppressWarnings("unchecked")
    protected DiscriminatorEnumerable<ResultSet, T> newEnumarableOfT(ResultSet rs) throws SQLException {
        DiscriminatorEnumerable.PredicatedMapper<ResultSet, T>[] mapperDiscriminators =
                new DiscriminatorEnumerable.PredicatedMapper[this.mappers.size()];

        for(int i = 0; i < mapperDiscriminators.length; i++) {

            DiscriminatorJdbcBuilder.PredicatedJdbcMapper<T> mapper = mappers.get(i);

            Predicate<ResultSet> discriminatorPredicate = new DiscriminatorPredicate(discriminatorColumn, mapper.getPredicate());

            mapperDiscriminators[i] =
                    new DiscriminatorEnumerable.PredicatedMapper<ResultSet, T>(
                            discriminatorPredicate,
                            mapper.getJdbcMapper(),
                            mapper.getJdbcMapper().newMappingContext(rs));
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
            return " property " + discriminatorColumn + " = " + in.getObject(discriminatorColumn);
        }
    }

}
