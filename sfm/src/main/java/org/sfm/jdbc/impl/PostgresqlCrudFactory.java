package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.QueryPreparer;
import org.sfm.jdbc.named.NamedSqlQuery;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlCrudFactory {
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
        sb.append(") ON CONFLICT (");

        first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (cm.isKey()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(cm.getColumn());
                first = false;
            }
        }

        sb.append(") DO UPDATE SET ");
        first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (!cm.isKey()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(cm.getColumn());
                sb.append(" = EXCLUDED.").append(cm.getColumn()).append("");
                first = false;
            }
        }

        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb), generatedKeys.toArray(new String[generatedKeys.size()]));
    }
}
