package org.sfm.jdbc;

import org.sfm.jdbc.impl.BatchInsertQueryExecutor;
import org.sfm.jdbc.impl.ColumnMeta;
import org.sfm.jdbc.impl.CrudMeta;
import org.sfm.jdbc.impl.KeyTupleQueryPreparer;
import org.sfm.jdbc.impl.MysqlBatchInsertQueryExecutor;
import org.sfm.jdbc.impl.MysqlCrud;
import org.sfm.jdbc.impl.SizeAdjusterBatchInsertQueryExecutor;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.map.column.KeyProperty;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CrudDSL<T, K> {
    private final Type target;
    private final Type keyTarget;
    private final JdbcMapperFactory jdbcMapperFactory;

    public CrudDSL(Type target, Type keyTarget, JdbcMapperFactory jdbcMapperFactory) {
        this.target = target;
        this.keyTarget = keyTarget;
        this.jdbcMapperFactory = jdbcMapperFactory;
    }

    public Crud<T, K> table(Connection connection, String table) throws SQLException {
        CrudMeta<T, K> crudMeta = CrudMeta.of(connection, table);

        JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance(jdbcMapperFactory);
        crudMeta.addColumnProperties(mapperFactory);

        return newInstance(crudMeta, jdbcMapperFactory);
    }



    public Crud<T, K> newInstance(
            CrudMeta<T, K> crudMeta,
            JdbcMapperFactory jdbcMapperFactory) throws SQLException {

        QueryPreparer<T> insert = buildInsert(crudMeta, jdbcMapperFactory);
        QueryPreparer<T> update = buildUpdate(crudMeta, jdbcMapperFactory);
        QueryPreparer<K> select = buildSelect(crudMeta, jdbcMapperFactory);
        QueryPreparer<K> delete = buildDelete(crudMeta, jdbcMapperFactory);

        KeyTupleQueryPreparer<K> keyTupleQueryPreparer = buildKeyTupleQueryPreparer(crudMeta, jdbcMapperFactory);

        JdbcMapper<T> selectMapper = buildSelectMapper(crudMeta, jdbcMapperFactory);
        JdbcMapper<K> keyMapper = buildKeyMapper(crudMeta, jdbcMapperFactory);

        boolean hasGeneratedKeys = crudMeta.hasGeneratedKeys();

        if (!crudMeta.getDatabaseMeta().isMysql()) {
            return new Crud<T, K>(
                    insert,
                    update,
                    select,
                    keyTupleQueryPreparer,
                    selectMapper,
                    delete,
                    keyMapper,
                    crudMeta.getTable(),
                    hasGeneratedKeys);
        } else {
            return new MysqlCrud<T, K>(
                    insert,
                    update,
                    select,
                    keyTupleQueryPreparer,
                    selectMapper,
                    delete,
                    keyMapper,
                    crudMeta.getTable(),
                    hasGeneratedKeys,
                    buildMysqlBatchInsert(crudMeta, jdbcMapperFactory));
        }
    }


    private KeyTupleQueryPreparer<K> buildKeyTupleQueryPreparer(CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) {
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

    private JdbcMapper<K> buildKeyMapper(CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) {
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

    private JdbcMapper<T> buildSelectMapper(CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        JdbcMapperBuilder<T> mapperBuilder = jdbcMapperFactory.<T>newBuilder(target);

        int i = 1;
        for(ColumnMeta cm : crudMeta.getColumnMetas()) {
            mapperBuilder.addMapping(cm.toJdbcColumnKey(i));
            i++;
        }
        return mapperBuilder.mapper();
    }


    private BatchInsertQueryExecutor<T> buildMysqlBatchInsert(CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
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

    private QueryPreparer<T> buildInsert(CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
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

    private QueryPreparer<T> buildUpdate(CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
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

    private QueryPreparer<K> buildSelect(CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(crudMeta.getTable());
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<K>from(keyTarget).to(NamedSqlQuery.parse(sb));
    }

    private QueryPreparer<K> buildDelete(CrudMeta<T, K> crudMeta, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(crudMeta.getTable());
        addWhereOnPrimaryKeys(crudMeta, sb);
        return jdbcMapperFactory.<K>from(keyTarget).to(NamedSqlQuery.parse(sb));
    }

    private void addWhereOnPrimaryKeys(CrudMeta<T, K> crudMeta, StringBuilder sb) {
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
