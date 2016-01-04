package org.sfm.jdbc.impl;

import org.sfm.jdbc.Crud;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.QueryPreparer;
import org.sfm.jdbc.impl.KeyTupleQueryPreparer;
import org.sfm.jdbc.impl.MysqlBatchInsertQueryPreparer;
import org.sfm.map.Mapper;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.RowHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MysqlCrud<T, K> extends Crud<T, K> {
    private final MysqlBatchInsertQueryPreparer<T> mysqlBatchInsertQueryPreparer;

    public MysqlCrud(QueryPreparer<T> insertQueryPreparer,
                     QueryPreparer<T> updateQueryPreparer,
                     QueryPreparer<K> selectQueryPreparer,
                     KeyTupleQueryPreparer<K> keyTupleQueryPreparer,
                     JdbcMapper<T> selectQueryMapper,
                     QueryPreparer<K> deleteQueryPreparer,
                     JdbcMapper<K> keyMapper,
                     String table,
                     boolean hasGeneratedKeys,
                     MysqlBatchInsertQueryPreparer<T> mysqlBatchInsertQueryPreparer) {
        super(insertQueryPreparer,
                updateQueryPreparer,
                selectQueryPreparer,
                keyTupleQueryPreparer,
                selectQueryMapper,
                deleteQueryPreparer,
                keyMapper,
                table,
                hasGeneratedKeys);
        this.mysqlBatchInsertQueryPreparer = mysqlBatchInsertQueryPreparer;
    }

    @Override
    public <RH extends RowHandler<? super K>> RH create(Connection connection, Collection<T> values, RH keyConsumer) throws SQLException {
        PreparedStatement preparedStatement = mysqlBatchInsertQueryPreparer.prepareStatement(connection, values.size());
        try {
            mysqlBatchInsertQueryPreparer.bindTo(preparedStatement, values);
            preparedStatement.executeUpdate();
            if (keyConsumer != null) {
                handeGeneratedKeys(keyConsumer, preparedStatement);
            }
            return keyConsumer;
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // IGNORE
            }
        }
        return keyConsumer;

    }
}
