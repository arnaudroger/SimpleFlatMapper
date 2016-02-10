package org.sfm.jdbc.impl;

import org.sfm.jdbc.Crud;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperBuilder;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.PreparedStatementMapperBuilder;
import org.sfm.jdbc.QueryPreparer;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrudFactory {
    public static <T, K> Crud<T, K> newInstance(
            ClassMeta<T> target,
            ClassMeta<K>  keyTarget,
            CrudMeta<T, K> crudMeta,
            JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance(jdbcMapperFactory);
        return createCrud(target, keyTarget, crudMeta, mapperFactory);

    }

    private static <T, K> Crud<T, K> createCrud(ClassMeta<T> target, ClassMeta<K> keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory mapperFactory) throws SQLException {
        crudMeta.addColumnProperties(mapperFactory);

        QueryPreparer<T> insert = buildInsert(target, crudMeta, mapperFactory);
        QueryPreparer<T> update = buildUpdate(target, crudMeta, mapperFactory);
        QueryPreparer<K> select = buildSelect(keyTarget, crudMeta, mapperFactory);
        QueryPreparer<K> delete = buildDelete(keyTarget, crudMeta, mapperFactory);
        QueryPreparer<T> upsert = buildUpsert(target, crudMeta, mapperFactory);

        KeyTupleQueryPreparer<K> keyTupleQueryPreparer = buildKeyTupleQueryPreparer(keyTarget, crudMeta, mapperFactory);

        JdbcMapper<T> selectMapper = buildSelectMapper(target, crudMeta, mapperFactory);
        JdbcMapper<K> keyMapper = buildKeyMapper(keyTarget, crudMeta, mapperFactory);

        boolean hasGeneratedKeys = crudMeta.hasGeneratedKeys();

        DefaultCrud<T, K> defaultCrud = new DefaultCrud<T, K>(
                insert,
                update,
                select,
                upsert,
                keyTupleQueryPreparer,
                selectMapper,
                delete,
                keyMapper,
                crudMeta.getTable(),
                hasGeneratedKeys);

        if (crudMeta.getDatabaseMeta().isMysql()) {
            return MysqlCrudFactory.newInstance(target, keyTarget, crudMeta, mapperFactory, defaultCrud);
        } else if (crudMeta.getDatabaseMeta().isPostgresSql()) {
            return PostgresqlCrudFactory.newInstance(target, keyTarget, crudMeta, mapperFactory, defaultCrud);
        }

        return defaultCrud;
    }

    private static <T, K> QueryPreparer<T> buildUpsert(ClassMeta<T>  target, CrudMeta<T, K> crudMeta, JdbcMapperFactory mapperFactory) {
        if (crudMeta.getDatabaseMeta().isMysql()) {
            return MysqlCrudFactory.buildUpsert(target, crudMeta, mapperFactory);
        } else if (crudMeta.getDatabaseMeta().isPostgresSql() && crudMeta.getDatabaseMeta().isVersionMet(9, 5)) {
            return PostgresqlCrudFactory.buildUpsert(target, crudMeta, mapperFactory);
        }
        return new UnsupportedQueryPreparer<T>("Upsert Not Supported on " + crudMeta.getDatabaseMeta());
    }


    private static <T, K> KeyTupleQueryPreparer<K> buildKeyTupleQueryPreparer(ClassMeta<K>  keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) {
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
        return new KeyTupleQueryPreparer<K>(builder.buildIndexFieldMappers(), primaryKeys.toArray(new String[0]));
    }

    private static <T, K>JdbcMapper<K> buildKeyMapper(ClassMeta<K>  keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) {
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

    private static <T, K> JdbcMapper<T> buildSelectMapper(ClassMeta<T>  target, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        JdbcMapperBuilder<T> mapperBuilder = jdbcMapperFactory.<T>newBuilder(target);

        int i = 1;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            mapperBuilder.addMapping(cm.toJdbcColumnKey(i));
            i++;
        }
        return mapperBuilder.mapper();
    }

    private static <T, K> QueryPreparer<T> buildInsert(ClassMeta<T> target, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
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
        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb), generatedKeys.isEmpty() ? null :  generatedKeys.toArray(new String[0]));
    }

    private static <T, K> QueryPreparer<T> buildUpdate(ClassMeta<T> target, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
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

    private static <T, K> QueryPreparer<K> buildSelect(ClassMeta<K> keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(crudMeta.getTable());
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<K>from(keyTarget).to(NamedSqlQuery.parse(sb));
    }

    private static <T, K> QueryPreparer<K> buildDelete(ClassMeta<K> keyTarget, CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
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
