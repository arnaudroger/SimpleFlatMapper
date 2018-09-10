package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.converter.DefaultContextFactoryBuilder;
import org.simpleflatmapper.jdbc.Crud;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.jdbc.PreparedStatementMapperBuilder;
import org.simpleflatmapper.jdbc.QueryPreparer;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CrudFactory {
    public static <T, K> Crud<T, K> newInstance(
            ClassMeta<T> target,
            ClassMeta<K>  keyTarget,
            CrudMeta crudMeta,
            JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance(jdbcMapperFactory);
        return createCrud(target, keyTarget, crudMeta, mapperFactory);

    }

    private static <T, K> Crud<T, K> createCrud(ClassMeta<T> target, ClassMeta<K> keyTarget, CrudMeta crudMeta, JdbcMapperFactory mapperFactory) throws SQLException {
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
                crudMeta,
                hasGeneratedKeys,
                new SelectQueryWhereFactory<T>(crudMeta, selectMapper, mapperFactory));

        if (crudMeta.getDatabaseMeta().isMysql()) {
            return MysqlCrudFactory.newInstance(target, keyTarget, crudMeta, mapperFactory, defaultCrud);
        } else if (crudMeta.getDatabaseMeta().isPostgresSql()) {
            return PostgresqlCrudFactory.newInstance(target, keyTarget, crudMeta, mapperFactory, defaultCrud);
        }

        return defaultCrud;
    }

    private static <T, K> QueryPreparer<T> buildUpsert(ClassMeta<T>  target, CrudMeta crudMeta, JdbcMapperFactory mapperFactory) {
        if (crudMeta.getDatabaseMeta().isMysql()) {
            return MysqlCrudFactory.buildUpsert(target, crudMeta, mapperFactory);
        } else if (crudMeta.getDatabaseMeta().isPostgresSql() && crudMeta.getDatabaseMeta().isVersionMet(9, 5)) {
            return PostgresqlCrudFactory.buildUpsert(target, crudMeta, mapperFactory);
        }
        return new UnsupportedQueryPreparer<T>("Upsert Not Supported on " + crudMeta.getDatabaseMeta());
    }


    private static <T, K> KeyTupleQueryPreparer<K> buildKeyTupleQueryPreparer(ClassMeta<K>  keyTarget, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory) {
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
        DefaultContextFactoryBuilder contextFactoryBuilder = new DefaultContextFactoryBuilder();
        MultiIndexFieldMapper<K>[] multiIndexFieldMappers = builder.buildIndexFieldMappers(contextFactoryBuilder);
        return new KeyTupleQueryPreparer<K>(multiIndexFieldMappers, contextFactoryBuilder.build(), primaryKeys.toArray(new String[0]));
    }

    private static <T, K>JdbcMapper<K> buildKeyMapper(ClassMeta<K>  keyTarget, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory) {
        JdbcMapperBuilder<K> mapperBuilder = jdbcMapperFactory.newBuilder(keyTarget);

        int i = 1;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (cm.isKey()) {
                mapperBuilder.addMapping(cm.toJdbcColumnKey(i));
                i++;
            }
        }
        if (i == 1) {
            throw new IllegalArgumentException("No key defined to map to " + keyTarget.getType() + ", specify key using DSL or add a primary key to the table");
        }
        return mapperBuilder.mapper();
    }

    private static <T, K> JdbcMapper<T> buildSelectMapper(ClassMeta<T>  target, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        JdbcMapperBuilder<T> mapperBuilder = jdbcMapperFactory.<T>newBuilder(target);

        int i = 1;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            mapperBuilder.addMapping(cm.toJdbcColumnKey(i));
            i++;
        }
        return mapperBuilder.mapper();
    }

    private static <T, K> QueryPreparer<T> buildInsert(ClassMeta<T> target, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        List<String> generatedKeys = new ArrayList<String>();

        StringBuilder sb = new StringBuilder("INSERT INTO ");
        appendTableName(sb, crudMeta);
        sb.append("(");
        boolean first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (cm.isInsertable()) {
                if (!first) {
                    sb.append(", ");
                }
                crudMeta.appendProtectedField(sb, cm.getColumn());
                first = false;
            } 
            
            if (cm.isGenerated()) {
                generatedKeys.add(cm.getColumn());
            }
        }
        sb.append(") VALUES(");
        first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (cm.isInsertable()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(cm.getInsertExpression());
                first = false;
            }
        }
        sb.append(")");
        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb), generatedKeys.isEmpty() ? null :  generatedKeys.toArray(new String[0]));
    }

    private static <T, K> QueryPreparer<T> buildUpdate(ClassMeta<T> target, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        appendTableName(sb, crudMeta);
        sb.append(" SET ");
        boolean first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            String columnName = cm.getColumn();
            if (!cm.isKey()) {
                if (!first) {
                    sb.append(", ");
                }
                crudMeta.appendProtectedField(sb, columnName);
                sb.append(" = ?");
                first = false;
            }
        }
        if (first) {
            // no field to update
            return null;
        }
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb));
    }

    private static <T, K> QueryPreparer<K> buildSelect(ClassMeta<K> keyTarget, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT ");

        boolean first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (!first) {
                sb.append(", ");
            }
            crudMeta.appendProtectedField(sb, cm.getColumn());
            first = false;
        }

        sb.append(" FROM ");
        appendTableName(sb, crudMeta);
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<K>from(keyTarget).to(NamedSqlQuery.parse(sb));
    }

    private static void appendTableName(StringBuilder sb, CrudMeta crudMeta) {
        crudMeta.appendTableName(sb);
    }

    private static <T, K> QueryPreparer<K> buildDelete(ClassMeta<K> keyTarget, CrudMeta crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        appendTableName(sb, crudMeta);
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<K>from(keyTarget).to(NamedSqlQuery.parse(sb));
    }

    private static <T, K> void addWhereOnPrimaryKeys(CrudMeta crudMeta, StringBuilder sb) {
        sb.append(" WHERE ");
        boolean first = true;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            if (cm.isKey()) {
                if (!first) {
                    sb.append("AND ");
                }
                crudMeta.appendProtectedField(sb, cm.getColumn());
                sb.append(" = ? ");
                first = false;
            }
        }
    }
}
