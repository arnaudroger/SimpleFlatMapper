package org.sfm.jdbc.impl;

import org.sfm.jdbc.Crud;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.PreparedStatementMapperBuilder;
import org.sfm.jdbc.QueryPreparer;
import org.sfm.jdbc.named.NamedSqlQuery;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlCrudFactory {

    public static <T, K> Crud<T, K> newInstance(Type target, Type keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory, DefaultCrud<T, K> defaultCrud) throws SQLException {
        return new MysqlCrud<T, K>(
                defaultCrud,
                buildMysqlBatchInsert(target, crudMeta, jdbcMapperFactory, false),
                buildMysqlBatchInsert(target, crudMeta, jdbcMapperFactory, true));

    }

    private static <T, K> BatchQueryExecutor<T> buildMysqlBatchInsert(
            Type target,
            CrudMeta<T, K> crudMeta,
            JdbcMapperFactory jdbcMapperFactory,
            boolean onDuplicateKeyUpdate) throws SQLException {

        List<String> generatedKeys = new ArrayList<String>();
        List<String> insertColumns = new ArrayList<String>();
        List<String> updateColumns = new ArrayList<String>();

        PreparedStatementMapperBuilder<T> statementMapperBuilder = jdbcMapperFactory.<T>from(target);
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            String columnName = cm.getColumn();
            if (cm.isGenerated()) {
                generatedKeys.add(columnName);
            } else {
                insertColumns.add(columnName);
                statementMapperBuilder.addColumn(columnName);
            }
            if (!cm.isKey()) {
                updateColumns.add(columnName);
            }
        }

        MysqlBatchInsertQueryExecutor<T> queryExecutor = new MysqlBatchInsertQueryExecutor<T>(
                crudMeta.getTable(),
                insertColumns.toArray(new String[insertColumns.size()]),
                onDuplicateKeyUpdate ? updateColumns.toArray(new String[updateColumns.size()]) : null,
                generatedKeys.toArray(new String[generatedKeys.size()]),
                statementMapperBuilder.buildIndexFieldMappers());

        return
                new SizeAdjusterBatchQueryExecutor<T>(queryExecutor);
    }

    public static <T, K> QueryPreparer<T> buildMysqlUpsert(Type target, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) {
        List<String> generatedKeys = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(crudMeta.getTable()).append("(");

        boolean first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (!cm.isGenerated()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(cm.getColumn());
                first = false;
            } else {
                generatedKeys.add(cm.getColumn());
            }
        }

        sb.append(") VALUES(");

        first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (!cm.isGenerated()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append("?");
                first = false;
            }
        }
        sb.append(") ON DUPLICATE KEY UPDATE ");

        first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (!cm.isKey()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(cm.getColumn());
                sb.append(" = VALUES(").append(cm.getColumn()).append(")");
                first = false;
            }
        }

        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb), generatedKeys.toArray(new String[generatedKeys.size()]));
    }
}
