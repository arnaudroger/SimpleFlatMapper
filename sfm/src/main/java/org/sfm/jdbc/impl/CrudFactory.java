package org.sfm.jdbc.impl;

import org.sfm.jdbc.Crud;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperBuilder;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.PreparedStatementMapperBuilder;
import org.sfm.jdbc.QueryPreparer;
import org.sfm.jdbc.named.NamedSqlQuery;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudFactory {
    public static <T, K> Crud<T, K> newInstance(
            Type target,
            Type keyTarget,
            CrudMeta<T, K> crudMeta,
            JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance(jdbcMapperFactory);
        crudMeta.addColumnProperties(mapperFactory);

        QueryPreparer<T> insert = buildInsert(target, crudMeta, jdbcMapperFactory);
        QueryPreparer<T> update = buildUpdate(target, crudMeta, jdbcMapperFactory);
        QueryPreparer<K> select = buildSelect(keyTarget, crudMeta, jdbcMapperFactory);
        QueryPreparer<K> delete = buildDelete(keyTarget, crudMeta, jdbcMapperFactory);

        KeyTupleQueryPreparer<K> keyTupleQueryPreparer = buildKeyTupleQueryPreparer(keyTarget, crudMeta, jdbcMapperFactory);

        JdbcMapper<T> selectMapper = buildSelectMapper(target, crudMeta, jdbcMapperFactory);
        JdbcMapper<K> keyMapper = buildKeyMapper(keyTarget, crudMeta, jdbcMapperFactory);

        boolean hasGeneratedKeys = crudMeta.hasGeneratedKeys();

        DefaultCrud<T, K> defaultCrud = new DefaultCrud<T, K>(
                insert,
                update,
                select,
                keyTupleQueryPreparer,
                selectMapper,
                delete,
                keyMapper,
                crudMeta.getTable(),
                hasGeneratedKeys);
        if (crudMeta.getDatabaseMeta().isMysql()) {
            return new MysqlCrud<T, K>(
                    defaultCrud,
                    buildMysqlBatchInsert(target, crudMeta, jdbcMapperFactory));
        }
        return defaultCrud;

    }


    private static <T, K> KeyTupleQueryPreparer<K> buildKeyTupleQueryPreparer(Type keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) {
        PreparedStatementMapperBuilder<K> builder = jdbcMapperFactory.from(keyTarget);
        List<String> primaryKeys = new ArrayList<String>();

        int i = 1;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (cm.isKey()) {
                primaryKeys.add(cm.getColumn());
                builder.addColumn(cm.toJdbcColumnKey(i));
                i++;
            }
        }
        return new KeyTupleQueryPreparer<K>(builder.buildIndexFieldMappers(), primaryKeys.toArray(new String[primaryKeys.size()]));
    }

    private static <T, K>JdbcMapper<K> buildKeyMapper(Type keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) {
        JdbcMapperBuilder<K> mapperBuilder = jdbcMapperFactory.newBuilder(keyTarget);

        int i = 1;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (cm.isKey()) {
                mapperBuilder.addMapping(cm.toJdbcColumnKey(i));
                i++;
            }
        }
        return mapperBuilder.mapper();
    }

    private static <T, K> JdbcMapper<T> buildSelectMapper(Type target, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        JdbcMapperBuilder<T> mapperBuilder = jdbcMapperFactory.<T>newBuilder(target);

        int i = 1;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            mapperBuilder.addMapping(cm.toJdbcColumnKey(i));
            i++;
        }
        return mapperBuilder.mapper();
    }


    private static <T, K> BatchInsertQueryExecutor<T> buildMysqlBatchInsert(Type target, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        List<String> generatedKeys = new ArrayList<String>();
        List<String> insertColumns = new ArrayList<String>();
        PreparedStatementMapperBuilder<T> statementMapperBuilder = jdbcMapperFactory.<T>from(target);
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (!cm.isGenerated()) {
                String columnName = cm.getColumn();
                insertColumns.add(columnName);
                statementMapperBuilder.addColumn(columnName);
            } else {
                generatedKeys.add(cm.getColumn());
            }
        }

        MysqlBatchInsertQueryExecutor<T> queryExecutor = new MysqlBatchInsertQueryExecutor<T>(
                crudMeta.getTable(),
                insertColumns.toArray(new String[insertColumns.size()]),
                generatedKeys.toArray(new String[generatedKeys.size()]),
                statementMapperBuilder.buildIndexFieldMappers());
        return
                new SizeAdjusterBatchInsertQueryExecutor<T>(queryExecutor);
    }

    private static <T, K> QueryPreparer<T> buildInsert(Type target, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        List<String> generatedKeys = new ArrayList<String>();

        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(crudMeta.getTable());
        sb.append("(");
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
        sb.append(")");
        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb), generatedKeys.isEmpty() ? null :  generatedKeys.toArray(new String[generatedKeys.size()]));
    }

    private static <T, K> QueryPreparer<T> buildUpdate(Type target, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(crudMeta.getTable());
        sb.append(" SET ");
        boolean first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            String columnName = cm.getColumn();
            if (!cm.isKey()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(columnName);
                sb.append(" = ?");
                first = false;
            }
        }
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb));
    }

    private static <T, K> QueryPreparer<K> buildSelect(Type keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(crudMeta.getTable());
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<K>from(keyTarget).to(NamedSqlQuery.parse(sb));
    }

    private static <T, K> QueryPreparer<K> buildDelete(Type keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(crudMeta.getTable());
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<K>from(keyTarget).to(NamedSqlQuery.parse(sb));
    }

    private static <T, K> void addWhereOnPrimaryKeys(CrudMeta<T, K> crudMeta, StringBuilder sb) {
        sb.append(" WHERE ");
        boolean first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (cm.isKey()) {
                if (!first) {
                    sb.append("AND ");
                }
                sb.append(cm.getColumn());
                sb.append(" = ? ");
                first = false;
            }
        }
    }
}
