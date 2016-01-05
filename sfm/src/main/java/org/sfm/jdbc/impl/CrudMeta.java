package org.sfm.jdbc.impl;

import java.lang.reflect.Type;

public class CrudMeta<T, K> {

    private final DatabaseMeta databaseMeta;
    private final Type target;
    private final Type key;
    private final String table;
    private final ColumnMeta[] columnMetas;

    public CrudMeta(Type target, Type key, DatabaseMeta databaseMeta, String table, ColumnMeta[] columnMetas) {
        this.databaseMeta = databaseMeta;
        this.target = target;
        this.key = key;
        this.table = table;
        this.columnMetas = columnMetas;
    }
}
