package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;

public final class MysqlBatchInsertQueryExecutor<T> extends AbstractBatchInsertQueryExecutor<T> {

    public MysqlBatchInsertQueryExecutor(
            CrudMeta meta,
            String[] insertColumns,
            String[] insertColumnExpressions,
            String[] updateColumns,
            String[] generatedKeys,
            MultiIndexFieldMapper<T>[] multiIndexFieldMappers,
            ContextFactory contextFactory) {
        super(meta, insertColumns, insertColumnExpressions, updateColumns, generatedKeys, multiIndexFieldMappers, contextFactory);
    }

    protected void appendInsertInto(StringBuilder sb) {
        if (updateColumns != null && updateColumns.length == 0) {
            sb.append("INSERT IGNORE INTO ");
        } else {
            sb.append("INSERT INTO ");
        }
    }

    protected void onDuplicateKeys(StringBuilder sb) {
        if (updateColumns.length > 0) {
            sb.append(" ON DUPLICATE KEY UPDATE ");
            for (int i = 0; i < updateColumns.length; i++) {
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

}
