package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;

public final class MysqlBatchInsertQueryExecutor<T> extends AbstractBatchInsertQueryExecutor<T> {

    public MysqlBatchInsertQueryExecutor(
            String table,
            String[] insertColumns,
            String[] insertColumnExpressions,
            String[] updateColumns,
            String[] generatedKeys,
            MultiIndexFieldMapper<T>[] multiIndexFieldMappers) {
        super(table, insertColumns, insertColumnExpressions, updateColumns, generatedKeys, multiIndexFieldMappers);
    }

    protected void onDuplicateKeys(StringBuilder sb) {
        sb.append(" ON DUPLICATE KEY UPDATE ");
        for(int i = 0; i < updateColumns.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(updateColumns[i])
              .append(" = VALUES(")
              .append(updateColumns[i])
              .append(")");
        }
    }

}
