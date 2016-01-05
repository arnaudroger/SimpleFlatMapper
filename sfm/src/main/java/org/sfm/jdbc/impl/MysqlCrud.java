package org.sfm.jdbc.impl;

import org.sfm.jdbc.Crud;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.QueryPreparer;
import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MysqlCrud<T, K> extends Crud<T, K> {
    private final BatchInsertQueryExecutor<T> batchInsertQueryExecutor;

    public MysqlCrud(QueryPreparer<T> insertQueryPreparer,
                     QueryPreparer<T> updateQueryPreparer,
                     QueryPreparer<K> selectQueryPreparer,
                     KeyTupleQueryPreparer<K> keyTupleQueryPreparer,
                     JdbcMapper<T> selectQueryMapper,
                     QueryPreparer<K> deleteQueryPreparer,
                     JdbcMapper<K> keyMapper,
                     String table,
                     boolean hasGeneratedKeys,
                     BatchInsertQueryExecutor<T> batchInsertQueryPreparer) {
        super(insertQueryPreparer,
                updateQueryPreparer,
                selectQueryPreparer,
                keyTupleQueryPreparer,
                selectQueryMapper,
                deleteQueryPreparer,
                keyMapper,
                table,
                hasGeneratedKeys);
        this.batchInsertQueryExecutor = batchInsertQueryPreparer;
    }

    @Override
    public <RH extends RowHandler<? super K>> RH create(Connection connection, Collection<T> values, final RH keyConsumer) throws SQLException {
        batchInsertQueryExecutor.insert(connection, values, new RowHandler<PreparedStatement>() {
            @Override
            public void handle(PreparedStatement preparedStatement) throws Exception {
                if (hasGeneratedKeys && keyConsumer != null) {
                    handleGeneratedKeys(keyConsumer, preparedStatement);
                }
            }
        });
        return keyConsumer;
    }
}
