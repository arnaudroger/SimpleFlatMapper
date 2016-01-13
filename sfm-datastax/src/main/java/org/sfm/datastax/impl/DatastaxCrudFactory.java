package org.sfm.datastax.impl;

import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import org.sfm.datastax.BoundStatementMapper;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.datastax.DatastaxCrud;
import org.sfm.datastax.DatastaxMapper;
import org.sfm.datastax.DatastaxMapperBuilder;
import org.sfm.datastax.DatastaxMapperFactory;
import org.sfm.datastax.SettableDataMapperBuilder;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

public class DatastaxCrudFactory {
    public static <T, K> DatastaxCrud<T, K> newInstance(
            Type target,
            Type keyTarget,
            TableMetadata tableMetadata, Session session,
            DatastaxMapperFactory datastaxMapperFactory) {
        DatastaxMapperFactory mapperFactory = DatastaxMapperFactory.newInstance(datastaxMapperFactory);
        return createCrud(target, keyTarget, tableMetadata, session, mapperFactory);

    }

    private static <T, K> DatastaxCrud<T, K> createCrud(Type target, Type keyTarget,
                                                        TableMetadata tableMetadata,
                                                        Session session,
                                                        DatastaxMapperFactory mapperFactory) {
        BoundStatementMapper<T> insertSetter = insertSetter(target, tableMetadata, mapperFactory);
        BoundStatementMapper<K> keySetter = keySetter(keyTarget, tableMetadata, mapperFactory);
        DatastaxMapper<T> selectMapper = selectMapper(target, tableMetadata, mapperFactory);
        return new DatastaxCrud<T, K>(
                session.prepare(insertQuery(tableMetadata)),
                session.prepare(readQuery(tableMetadata)),
                session.prepare(deleteQuery(tableMetadata)),
                insertSetter,
                keySetter,
                selectMapper);
    }

    private static String deleteQuery(TableMetadata tableMetadata) {
        StringBuilder sb = new StringBuilder("DELETE FROM ");

        sb.append(tableMetadata.getName());

        sb.append(" WHERE ");

        List<ColumnMetadata> columns = tableMetadata.getPrimaryKey();

        boolean first = true;
        for(ColumnMetadata column : columns) {
            if (! first) {
                sb.append(" and ");
            }
            sb.append(column.getName()).append(" = ?");
            first = false;
        }

        return sb.toString();
    }

    private static String readQuery(TableMetadata tableMetadata) {
        StringBuilder sb = new StringBuilder("SELECT ");


        List<ColumnMetadata> columns = tableMetadata.getColumns();

        boolean first = true;
        for(ColumnMetadata column : columns) {
            if (! first) {
                sb.append(", ");
            }
            sb.append(column.getName());

            first = false;
        }

        sb.append(" FROM ");
        sb.append(tableMetadata.getName());

        sb.append(" WHERE ");

        columns = tableMetadata.getPrimaryKey();

        first = true;
        for(ColumnMetadata column : columns) {
            if (! first) {
                sb.append(", ");
            }
            sb.append(column.getName()).append(" = ?");
            first = false;
        }

        return sb.toString();
    }

    private static String insertQuery(TableMetadata tableMetadata) {
        StringBuilder sb = new StringBuilder("INSERT INTO ");

        sb.append(tableMetadata.getName()).append("(");

        List<ColumnMetadata> columns = tableMetadata.getColumns();

        boolean first = true;
        for(ColumnMetadata column : columns) {
            if (! first) {
                sb.append(", ");
            }
            sb.append(column.getName());

            first = false;
        }

        sb.append(") VALUES(");

        for(int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?");
        }
        sb.append(")");
        return sb.toString();
    }


    private static <T> DatastaxMapper<T> selectMapper(Type target, TableMetadata tableMetadata, DatastaxMapperFactory mapperFactory) {
        DatastaxMapperBuilder<T> mapperBuilder = mapperFactory.newBuilder(target);
        int i = 0;
        for(ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
            mapperBuilder.addMapping(DatastaxColumnKey.of(columnMetadata, i++));
        }
        return mapperBuilder.mapper();
    }

    private static <K> BoundStatementMapper<K> keySetter(Type keyTarget, TableMetadata tableMetadata, DatastaxMapperFactory mapperFactory) {
        SettableDataMapperBuilder<K> mapperBuilder = mapperFactory.newBuilderFrom(keyTarget);
        int i = 0;
        for(ColumnMetadata columnMetadata : tableMetadata.getPrimaryKey()) {
            mapperBuilder.addColumn(DatastaxColumnKey.of(columnMetadata, i++));
        }
        return new BoundStatementMapper<K>(mapperBuilder.mapper());
    }

    private static <T> BoundStatementMapper<T> insertSetter(Type target, TableMetadata tableMetadata, DatastaxMapperFactory mapperFactory) {
        SettableDataMapperBuilder<T> mapperBuilder = mapperFactory.newBuilderFrom(target);
        int i = 0;
        for(ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
            mapperBuilder.addColumn(DatastaxColumnKey.of(columnMetadata, i++));
        }
        return new BoundStatementMapper<T>(mapperBuilder.mapper());
    }


}
