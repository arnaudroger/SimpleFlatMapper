package org.sfm.jdbc.impl;

import org.sfm.jdbc.named.NamedParameter;
import org.sfm.jdbc.named.NamedSqlQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MysqlBatchInsertQueryPreparer<T> {

    private final String table;
    private final String[] columns;
    private final String[] generatedKeys;
    private final MultiIndexFieldMapper<T>[] multiIndexFieldMappers;


    public MysqlBatchInsertQueryPreparer(String table, String[] columns, String[] generatedKeys, MultiIndexFieldMapper<T>[] multiIndexFieldMappers) {
        this.table = table;
        this.columns = columns;
        this.generatedKeys = generatedKeys;
        this.multiIndexFieldMappers = multiIndexFieldMappers;
    }

    public PreparedStatement prepareStatement(Connection connection, int size) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(table).append("(");

        for(int j = 0; j < columns.length; j++) {
            if (j > 0) {
                sb.append(", ");
            }
            sb.append(columns[j]);
        }

        sb.append(") VALUES");

        for(int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("(");

            for(int j = 0; j < columns.length; j++) {
                if (j > 0) {
                    sb.append(", ");
                }
                sb.append("?");
            }

            sb.append(")");

        }
        if (generatedKeys.length == 0) {
            return connection.prepareStatement(sb.toString());
        } else {
            return connection.prepareStatement(sb.toString(), generatedKeys);
        }
    }

    public void bindTo(PreparedStatement preparedStatement, Collection<T> values) throws Exception {
        int i = 0;
        for(T value : values) {
            for(int j = 0; j < multiIndexFieldMappers.length; j++ ) {
                multiIndexFieldMappers[j].map(preparedStatement, value, i);
                i++;
            }
        }
    }
}
