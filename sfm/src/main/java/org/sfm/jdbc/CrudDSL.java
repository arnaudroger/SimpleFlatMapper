package org.sfm.jdbc;

import org.sfm.jdbc.impl.BatchInsertQueryExecutor;
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
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE 1 = 2");
            try {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                List<String> primaryKeys = getPrimaryKeys(connection, resultSetMetaData);

                JdbcMapperFactory mapperFactory = JdbcMapperFactory.newInstance(jdbcMapperFactory);

                for(int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    String columnName = resultSetMetaData.getColumnName(i + 1);
                    mapperFactory.addColumnProperty(columnName, SqlTypeColumnProperty.of(resultSetMetaData.getColumnType(i + 1)));
                    if (primaryKeys.contains(columnName)) {
                        mapperFactory.addColumnProperty(columnName, new KeyProperty());
                    }
                }

                if (primaryKeys.isEmpty()) throw new IllegalArgumentException("No primary keys defined on " + table);
                return newInstance(target, keyTarget, mapperFactory, resultSetMetaData, primaryKeys, connection.getMetaData());

            } finally {
                resultSet.close();
            }
        } finally {
            statement.close();
        }
    }

    public List<String> getPrimaryKeys(Connection connection, ResultSetMetaData resultSetMetaData) throws SQLException {
        List<String> primaryKeys = new ArrayList<String>();
        ResultSet set = connection.getMetaData().getPrimaryKeys(resultSetMetaData.getCatalogName(1), resultSetMetaData.getSchemaName(1), resultSetMetaData.getTableName(1));
        try {
            while (set.next()) {
                primaryKeys.add(set.getString("COLUMN_NAME"));
            }
        } finally {
            set.close();
        }
        return primaryKeys;
    }

    public Crud<T, K> newInstance(
            Type target,
            Type keyTarget,
            JdbcMapperFactory jdbcMapperFactory,
            ResultSetMetaData resultSetMetaData,
            List<String> primaryKeys,
            DatabaseMetaData metaData) throws SQLException {
        QueryPreparer<T> insert = buildInsert(target, resultSetMetaData, jdbcMapperFactory);
        QueryPreparer<T> update = buildUpdate(target, resultSetMetaData, primaryKeys, jdbcMapperFactory);
        QueryPreparer<K> select = buildSelect(keyTarget, resultSetMetaData, primaryKeys, jdbcMapperFactory);
        KeyTupleQueryPreparer<K> keyTupleQueryPreparer = buildKeyTupleQueryPreparer(keyTarget, primaryKeys, jdbcMapperFactory);
        JdbcMapper<T> selectMapper = buildSelectMapper(target, resultSetMetaData, jdbcMapperFactory);
        QueryPreparer<K> delete = buildDelete(keyTarget, resultSetMetaData, primaryKeys, jdbcMapperFactory);
        JdbcMapper<K> keyMapper = buildKeyMapper(keyTarget, primaryKeys, jdbcMapperFactory);
        boolean hasGeneratedKeys = hasGeneratedKeys(resultSetMetaData);

        if (!isMysql(metaData)) {

            return new Crud<T, K>(
                    insert,
                    update,
                    select,
                    keyTupleQueryPreparer,
                    selectMapper,
                    delete,
                    keyMapper,
                    resultSetMetaData.getTableName(1),
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
                    resultSetMetaData.getTableName(1),
                    hasGeneratedKeys,
                    buildMysqlBatchInsert(target, resultSetMetaData, jdbcMapperFactory));
        }
    }

    private boolean isMysql(DatabaseMetaData metaData) throws SQLException {
        return "MySQL".equals(metaData.getDatabaseProductName());
    }

    private boolean hasGeneratedKeys(ResultSetMetaData resultSetMetaData) throws SQLException {
        for(int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            if (resultSetMetaData.isAutoIncrement(i + 1)) {
                return true;
            }
        }
        return false;
    }

    private KeyTupleQueryPreparer<K> buildKeyTupleQueryPreparer(Type keyTarget, List<String> primaryKeys, JdbcMapperFactory jdbcMapperFactory) {
        PreparedStatementMapperBuilder<K> builder = jdbcMapperFactory.from(keyTarget);
        for(String str : primaryKeys) {
            builder.addColumn(str);
        }
        return new KeyTupleQueryPreparer<K>(builder.buildIndexFieldMappers(), primaryKeys.toArray(new String[primaryKeys.size()]));
    }

    private JdbcMapper<K> buildKeyMapper(Type keyTarget, List<String> primaryKeys, JdbcMapperFactory jdbcMapperFactory) {
        JdbcMapperBuilder<K> mapperBuilder = jdbcMapperFactory.newBuilder(keyTarget);
        for(String column : primaryKeys) {
            mapperBuilder.addMapping(column);
        }
        return mapperBuilder.mapper();
    }

    private JdbcMapper<T> buildSelectMapper(Type target, ResultSetMetaData resultSetMetaData, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        return jdbcMapperFactory.<T>newBuilder(target).addMapping(resultSetMetaData).mapper();
    }


    private BatchInsertQueryExecutor<T> buildMysqlBatchInsert(Type target, ResultSetMetaData resultSetMetaData, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        List<String> generatedKeys = new ArrayList<String>();
        List<String> insertColumns = new ArrayList<String>();
        PreparedStatementMapperBuilder<T> statementMapperBuilder = jdbcMapperFactory.<T>from(target);
        for(int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            if (!resultSetMetaData.isAutoIncrement(i + 1)) {
                String columnName = resultSetMetaData.getColumnName(i + 1);
                insertColumns.add(columnName);
                statementMapperBuilder.addColumn(columnName);
            } else {
                generatedKeys.add(resultSetMetaData.getColumnName(i + 1));
            }
        }

        MysqlBatchInsertQueryExecutor<T> queryExecutor = new MysqlBatchInsertQueryExecutor<T>(
                resultSetMetaData.getTableName(1),
                insertColumns.toArray(new String[insertColumns.size()]),
                generatedKeys.toArray(new String[generatedKeys.size()]),
                statementMapperBuilder.buildIndexFieldMappers());
        return
                new SizeAdjusterBatchInsertQueryExecutor<T>(queryExecutor);
    }

    private QueryPreparer<T> buildInsert(Type target, ResultSetMetaData resultSetMetaData, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        List<String> generatedKeys = new ArrayList<String>();

        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(resultSetMetaData.getTableName(1));
        sb.append("(");
        boolean first = true;
        for(int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            if (!resultSetMetaData.isAutoIncrement(i + 1)) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(resultSetMetaData.getColumnName(i + 1));
                first = false;
            } else {
                generatedKeys.add(resultSetMetaData.getColumnName(i + 1));
            }
        }
        sb.append(") VALUES(");
        first = true;
        for(int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            if (!resultSetMetaData.isAutoIncrement(i + 1)) {
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

    private QueryPreparer<T> buildUpdate(Type target, ResultSetMetaData resultSetMetaData, List<String> primaryKeys, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(resultSetMetaData.getTableName(1));
        sb.append(" SET ");
        boolean first = true;
        for(int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            String columnName = resultSetMetaData.getColumnName(i + 1);
            if (!primaryKeys.contains(columnName)) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(columnName);
                sb.append(" = ?");
                first = false;
            }
        }
        addWhereOnPrimaryKeys(primaryKeys, sb);
        return jdbcMapperFactory.<T>from(target).to(NamedSqlQuery.parse(sb));
    }

    private QueryPreparer<K> buildSelect(Type target, ResultSetMetaData resultSetMetaData, List<String> primaryKeys, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(resultSetMetaData.getTableName(1));
        addWhereOnPrimaryKeys(primaryKeys, sb);
        return jdbcMapperFactory.<K>from(target).to(NamedSqlQuery.parse(sb));
    }

    private QueryPreparer<K> buildDelete(Type target, ResultSetMetaData resultSetMetaData, List<String> primaryKeys, JdbcMapperFactory jdbcMapperFactory) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(resultSetMetaData.getTableName(1));
        addWhereOnPrimaryKeys(primaryKeys, sb);
        return jdbcMapperFactory.<K>from(target).to(NamedSqlQuery.parse(sb));
    }

    private void addWhereOnPrimaryKeys(List<String> primaryKeys, StringBuilder sb) {
        sb.append(" WHERE ");
        for(int i = 0; i < primaryKeys.size(); i++) {
            if (i > 0) {
                sb.append("AND ");
            }
            sb.append(primaryKeys.get(i));
            sb.append(" = ? ");
        }
    }
}
