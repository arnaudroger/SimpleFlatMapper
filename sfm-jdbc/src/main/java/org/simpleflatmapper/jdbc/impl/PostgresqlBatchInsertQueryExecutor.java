package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;

public final class PostgresqlBatchInsertQueryExecutor<T> extends AbstractBatchInsertQueryExecutor<T> {

    private final String[] keys;

    public PostgresqlBatchInsertQueryExecutor(
            String table,
            String[] insertColumns,
            String[] insertColumnExpressions,
            String[] updateColumns,
            String[] generatedKeys,
            String[] keys,
            MultiIndexFieldMapper<T>[] multiIndexFieldMappers) {
        super(table, insertColumns, insertColumnExpressions, updateColumns, generatedKeys, multiIndexFieldMappers);
        this.keys = keys;
    }

    protected void onDuplicateKeys(StringBuilder sb) {
        sb.append(" ON CONFLICT (");
        for(int i = 0; i < keys.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(keys[i]);
        }
        sb.append(") DO ");
        
        if (updateColumns.length > 0) {
            sb.append("UPDATE SET ");
            for (int i = 0; i < updateColumns.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(updateColumns[i])
                        .append(" = EXCLUDED.")
                        .append(updateColumns[i]);
            }
        } else {
            sb.append("NOTHING");
        }

    }

}
