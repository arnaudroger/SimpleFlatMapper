package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.PreparedStatementMapperBuilder;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlCrudFactory {

    public static <T, K> Crud<T, K> newInstance(ClassMeta<T> target, ClassMeta<K>  keyTarget, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory, DefaultCrud<T, K> defaultCrud) throws SQLException {
        return new MultiRowsBatchInsertCrud<T, K>(
                defaultCrud,
                buildBatchInsert(target, crudMeta, jdbcMapperFactory, false),
                buildBatchInsert(target, crudMeta, jdbcMapperFactory, true));

    }

    private static <T, K> BatchQueryExecutor<T> buildBatchInsert(
            ClassMeta<T> target,
            CrudMeta crudMeta,
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
                insertColumns.toArray(new String[0]),
                onDuplicateKeyUpdate ? updateColumns.toArray(new String[0]) : null,
                generatedKeys.toArray(new String[0]),
                statementMapperBuilder.buildIndexFieldMappers());

        return
                new SizeAdjusterBatchQueryExecutor<T>(queryExecutor);
    }

    public static <T, K> QueryPreparer<T> buildUpsert(ClassMeta<T> target, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory) {
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

        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb), generatedKeys.toArray(new String[0]));
    }
}
