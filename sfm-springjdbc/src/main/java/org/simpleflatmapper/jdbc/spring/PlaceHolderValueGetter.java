package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.NullContextualGetter;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.util.ErrorHelper;

public final class PlaceHolderValueGetter<T> {

    private final String column;
    private final int sqlType;
    private final String typeName;
    private final ContextualGetter<T, ?> getter;
    private final ContextFactory contextFactory;


    public PlaceHolderValueGetter(String column, int sqlType, String typeName, ContextualGetter<T, ?> getter, ContextFactory contextFactory) {
        this.column = column;
        this.sqlType = sqlType;
        this.typeName = typeName;
        this.getter = getter;
        this.contextFactory = contextFactory;
    }


    public boolean isColumn(String column) {
        return this.column.equals(column);
    }

    public Object getValue(T instance) {
        try {
            Context context = contextFactory.newContext();
            return getter.get(instance, context);
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

    public boolean hasGetter() {
        return !NullContextualGetter.isNull(getter);
    }
}
