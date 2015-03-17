package org.sfm.jdbc.impl;


import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.SQLMappingException;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcMapperHelper {
    public static <H extends RowHandler<? super T>, T> H forEach(ResultSet rs,
                                                                 H handler,
                                                                 MappingContext<ResultSet> context,
                                                                 JdbcMapper<T> mapper,
                                                                 RowHandlerErrorHandler errorHandler) throws SQLException {
        while(rs.next()) {
            T t = mapper.map(rs, context);
            try {
                handler.handle(t);
            } catch(Throwable error) {
                errorHandler.handlerError(error, t);
            }
        }
        return handler;

    }

    public static <T> T rethrowOnlyRuntime(Exception e) {
        if (e instanceof RuntimeException) {
            throw ((RuntimeException) e);
        } else if (e instanceof  SQLException) {
            throw new SQLMappingException(e.getMessage(), e);
        } else {
            throw new MappingException(e.getMessage(), e);
        }

    }

    public static <T> T rethrowSQLException(Exception e) throws SQLException {
        if (e instanceof RuntimeException) {
            throw ((RuntimeException) e);
        } else if(e instanceof SQLException) {
            throw ((SQLException)e);
        } else {
            throw new MappingException(e.getMessage(), e);
        }
    }
}
