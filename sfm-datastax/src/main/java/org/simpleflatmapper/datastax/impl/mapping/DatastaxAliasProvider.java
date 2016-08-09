package org.simpleflatmapper.datastax.impl.mapping;


import org.simpleflatmapper.reflect.meta.AliasProvider;
import org.simpleflatmapper.reflect.meta.Table;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DatastaxAliasProvider implements AliasProvider {


    private final Class<? extends Annotation> columnClass;
    private final Method columnName;
    private final Class<? extends Annotation> tableClass;
    private final Method tableName;
    private final Method tableKeyspace;

    @SuppressWarnings("unchecked")
    public DatastaxAliasProvider() {
        try {
            columnClass = (Class<? extends Annotation>) Class.forName("com.datastax.driver.mapping.annotations.Column");
            columnName = columnClass.getDeclaredMethod("name");
            tableClass = (Class<? extends Annotation>) Class.forName("com.datastax.driver.mapping.annotations.Table");
            tableName = tableClass.getDeclaredMethod("name");
            tableKeyspace = tableClass.getDeclaredMethod("keyspace");
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
            throw new Error();
        }
    }

    private String getColumnName(Object col) {
        return getString(col, columnName);
    }

    private String getTableName(Object col) {
        return getString(col, tableName);
    }
    private String getTableKeyspace(Object col) {
        return getString(col, tableKeyspace);
    }

    private String getString(Object col, Method method) {
        try {
            return (String) method.invoke(col);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }
    @Override
    public Table getTable(Class<?> target) {
        Table keyspaceTable = Table.NULL;
        Object table = target.getAnnotation(tableClass);
        if (table != null) {
            keyspaceTable = new Table(null, getTableKeyspace(table), getTableName(table));
        }
        return keyspaceTable;
    }

    @Override
    public String getAliasForMethod(Method method) {
        String alias = null;
        Object col = method.getAnnotation(columnClass);
        if (col != null) {
            alias = getColumnName(col);
        }
        return alias;
    }

    @Override
    public String getAliasForField(Field field) {
        String alias = null;
        Object col = field.getAnnotation(columnClass);
        if (col != null) {
            alias = getColumnName(col);
        }
        return alias;
    }

}
