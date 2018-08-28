package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.ErrorHelper;

public final class PlaceHolderValueGetter<T> {

    private final String column;
    private final int sqlType;
    private final String typeName;
    private final FieldMapperGetter<T, ?> getter;


    public PlaceHolderValueGetter(String column, int sqlType, String typeName, FieldMapperGetter<T, ?> getter) {
        this.column = column;
        this.sqlType = sqlType;
        this.typeName = typeName;
        this.getter = getter;
    }


    public boolean isColumn(String column) {
        return this.column.equals(column);
    }

    public Object getValue(T instance, MappingContext<?> mappingContext) {
        try {
            return getter.get(instance, mappingContext);
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
