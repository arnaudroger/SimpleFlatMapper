package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.ErrorHelper;

public final class PlaceHolderValueGetter<T> {

    private final String column;
    private final int sqlType;
    private final String typeName;
    private final Getter<T, ?> getter;


    public PlaceHolderValueGetter(String column, int sqlType, String typeName, Getter<T, ?> getter) {
        this.column = column;
        this.sqlType = sqlType;
        this.typeName = typeName;
        this.getter = getter;
    }


    public boolean isColumn(String column) {
        return this.column.equals(column);
    }

    public Object getValue(T instance) {
        try {
            return getter.get(instance);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    public int getSqlType() {
        return sqlType;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getColumn() {
        return column;
    }
}
