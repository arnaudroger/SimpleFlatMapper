package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.querybuilder.*;
import org.simpleflatmapper.datastax.BoundStatementMapper;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.datastax.DatastaxCrud;
import org.simpleflatmapper.datastax.DatastaxMapper;
import org.simpleflatmapper.datastax.DatastaxMapperBuilder;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.datastax.SettableDataMapperBuilder;

import java.lang.reflect.Type;
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
        DatastaxMapper<T> selectMapper = selectMapper(target, tableMetadata, mapperFactory);
        return new DatastaxCrud<T, K>(
                session.prepare(insertQuery(tableMetadata)),
                session.prepare(insertQuery(tableMetadata, "TTL", "TIMESTAMP")),
                session.prepare(insertQuery(tableMetadata, "TTL" )),
                session.prepare(insertQuery(tableMetadata, "TIMESTAMP")),
                session.prepare(readQuery(tableMetadata)),
                session.prepare(deleteQuery(tableMetadata)),
                session.prepare(deleteQueryWithTimestamp(tableMetadata)),
                DatastaxCrudFactory.<T>insertSetter(target, tableMetadata, mapperFactory, 0),
                DatastaxCrudFactory.<K>keySetter(keyTarget, tableMetadata, mapperFactory, 0),
                DatastaxCrudFactory.<K>keySetter(keyTarget, tableMetadata, mapperFactory, 1),
                selectMapper,
                tableMetadata.getColumns().size(), session);
    }

    private static String deleteQuery(TableMetadata tableMetadata) {
        Delete delete = QueryBuilder.delete().from(tableMetadata);

        Delete.Where where = delete.where();

        List<ColumnMetadata> columns = tableMetadata.getPrimaryKey();

        for(ColumnMetadata column : columns) {
            where.and(QueryBuilder.eq(column.getName(), QueryBuilder.bindMarker()));
        }

        return delete.toString();
    }

    private static String deleteQueryWithTimestamp(TableMetadata tableMetadata) {
        Delete delete = QueryBuilder.delete().from(tableMetadata);

        delete.using(QueryBuilder.timestamp(QueryBuilder.bindMarker()));

        Delete.Where where = delete.where();

        List<ColumnMetadata> columns = tableMetadata.getPrimaryKey();

        for(ColumnMetadata column : columns) {
            where.and(QueryBuilder.eq(column.getName(), QueryBuilder.bindMarker()));
        }

        return delete.toString();
    }


    private static String readQuery(TableMetadata tableMetadata) {

        Select.Selection select = QueryBuilder.select();


        List<ColumnMetadata> columns = tableMetadata.getColumns();

        for(ColumnMetadata column : columns) {
            select.column(column.getName());
        }

        Select.Where where = select.from(tableMetadata).where();

        columns = tableMetadata.getPrimaryKey();
        for(ColumnMetadata column : columns) {
            where.and(QueryBuilder.eq(column.getName(), QueryBuilder.bindMarker()));
        }

        return where.toString();
    }

    private static String insertQuery(TableMetadata tableMetadata, String... options) {
        Insert insert = QueryBuilder.insertInto(tableMetadata);

        if (options != null) {
            Insert.Options using = insert.using();
            for (String option : options) {
                if ("TTL".equals(option)) {
                    using.and(QueryBuilder.ttl(QueryBuilder.bindMarker()));
                } else {
                    using.and(QueryBuilder.timestamp(QueryBuilder.bindMarker()));
                }
            }
        }

        List<ColumnMetadata> columns = tableMetadata.getColumns();

        for(ColumnMetadata column : columns) {
            insert.value(column.getName(), QueryBuilder.bindMarker());
        }

        return insert.toString();
    }


    private static <T> DatastaxMapper<T> selectMapper(Type target, TableMetadata tableMetadata, DatastaxMapperFactory mapperFactory) {
        DatastaxMapperBuilder<T> mapperBuilder = mapperFactory.newBuilder(target);
        int i = 0;
        for(ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
            mapperBuilder.addMapping(DatastaxColumnKey.of(columnMetadata, i++));
        }
        return mapperBuilder.mapper();
    }

    private static <K> BoundStatementMapper<K> keySetter(Type keyTarget, TableMetadata tableMetadata, DatastaxMapperFactory mapperFactory, int offset) {
        SettableDataMapperBuilder<K> mapperBuilder = mapperFactory.newBuilderFrom(keyTarget);
        int i = offset;
        for(ColumnMetadata columnMetadata : tableMetadata.getPrimaryKey()) {
            mapperBuilder.addColumn(DatastaxColumnKey.of(columnMetadata, i++));
        }
        return new BoundStatementMapper<K>(mapperBuilder.mapper());
    }

    private static <T> BoundStatementMapper<T> insertSetter(Type target, TableMetadata tableMetadata, DatastaxMapperFactory mapperFactory, int offset) {
        SettableDataMapperBuilder<T> mapperBuilder = mapperFactory.newBuilderFrom(target);
        int i = offset;
        for(ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
            mapperBuilder.addColumn(DatastaxColumnKey.of(columnMetadata, i++));
        }
        return new BoundStatementMapper<T>(mapperBuilder.mapper());
    }


}
