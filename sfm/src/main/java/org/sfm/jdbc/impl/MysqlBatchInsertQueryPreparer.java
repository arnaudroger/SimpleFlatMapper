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
    private final MultiIndexFieldMapper<T>[] multiIndexFieldMappers;


    public MysqlBatchInsertQueryPreparer(String table, String[] columns, MultiIndexFieldMapper<T>[] multiIndexFieldMappers) {
        this.table = table;
        this.columns = columns;
        this.multiIndexFieldMappers = multiIndexFieldMappers;
    }

    public PreparedStatement prepareStatement(Connection connection, int size) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO TABLE ");
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

        return connection.prepareStatement(sb.toString());
    }

    public void bindTo(PreparedStatement preparedStatement, Collection<T> values) throws Exception {
        int i = 1;
        for(T value : values) {
            for(int j = 0; j < multiIndexFieldMappers.length; j++ ) {
                multiIndexFieldMappers[j].map(preparedStatement, value, i);
                i++;
            }
        }
    }
}
