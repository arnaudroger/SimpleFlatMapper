package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.*;
import org.sfm.utils.ForEachIterator;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class JoinJdbcMapper<T> extends AbstractForEachDynamicJdbcMapper<T> {

    private final JdbcMapper<T> mapper;

    public JoinJdbcMapper(JdbcMapper<T> mapper, RowHandlerErrorHandler errorHandler) {
        super(errorHandler);
        this.mapper = mapper;
    }

    @Override
    protected JdbcMapper<T> getMapper(ResultSet source) {
        return mapper;
    }

    @Override
    protected JoinForEach<T> newForEachIterator(ResultSet rs) throws SQLException {
        return new JoinForEach<T>(mapper, newMappingContext(rs), errorHandler, rs);
    }

    private static class JoinForEach<T> implements ForEachIterator<T> {

        private final JdbcMapper<T> mapper;
        private final MappingContext<ResultSet> mappingContext;
        private final RowHandlerErrorHandler rowHandlerErrorHandler;


        private final ResultSet resultSet;
        private T currentValue;

        private JoinForEach(JdbcMapper<T> mapper, MappingContext<ResultSet> mappingContext, RowHandlerErrorHandler rowHandlerErrorHandler, ResultSet resultSet) {
            this.mapper = mapper;
            this.mappingContext = mappingContext;
            this.rowHandlerErrorHandler = rowHandlerErrorHandler;
            this.resultSet = resultSet;
        }


        @Override
        public boolean next(RowHandler<? super T> rowHandler) throws Exception {
            return forEach(true, rowHandler);
        }

        @Override
        public void forEach(RowHandler<? super T> rowHandler) throws Exception {
            forEach(false, rowHandler);
        }

        private boolean forEach(boolean stopOnNext, RowHandler<? super T> rowHandler) throws Exception {
            while (resultSet.next()) {

                mappingContext.handle(resultSet);

                if (mappingContext.rootBroke()) {
                    if (currentValue != null) {
                        callHandler(rowHandler);
                        currentValue = mapper.map(resultSet, mappingContext);
                        if (stopOnNext) {
                            return true;
                        }
                    } else {
                        currentValue = mapper.map(resultSet, mappingContext);
                    }
                } else {
                    mapper.mapTo(resultSet, currentValue, mappingContext);
                }
            }

            if (currentValue != null) {
                callHandler(rowHandler);
                currentValue = null;
                return true;
            } else {
                return false;
            }
        }

        private void callHandler(RowHandler<? super T> rowHandler) throws Exception {
            try {
                rowHandler.handle(currentValue);
            } catch(Exception e) {
                rowHandlerErrorHandler.handlerError(e, currentValue);
            }

        }
    }

    @Override
    public String toString() {
        return "JoinJdbcMapper{" +
                "jdbcMapper=" + mapper +
                '}';
    }
}
