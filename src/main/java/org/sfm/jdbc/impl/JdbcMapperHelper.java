package org.sfm.jdbc.impl;


import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcMapperHelper {
    public static <H extends RowHandler<? super T>, T> H forEach(ResultSet rs,
                                                                 H handler,
                                                                 MappingContext<ResultSet> context,
                                                                 Mapper<ResultSet, T> mapper,
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
}
