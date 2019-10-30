package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ArrayResultSetGetter<T> implements Getter<ResultSet, T[]>, ContextualGetter<ResultSet, T[]> {

    public static final int VALUE_INDEX = 2;

    private final int index;
    private final Getter<ResultSet, T> getter;
    private final T[] emptyArray;

    public ArrayResultSetGetter(Class<T> componentType, Getter<ResultSet, T> getter, int index) {
        this.getter = getter;
        this.index = index;
        this.emptyArray = (T[]) java.lang.reflect.Array.newInstance(componentType, 0);
    }

    @Override
    public T[] get(ResultSet resultSet, Context context) throws Exception {
        return get(resultSet);
    }

    @Override
    public T[] get(ResultSet target) throws Exception {
        Array sqlArray = target.getArray(index);

        if (sqlArray != null) {
            List<T> list = new ArrayList<T>();
            ResultSet rs = sqlArray.getResultSet();
            try {
                while (rs.next()) {
                    list.add(getter.get(rs));
                }
            } finally {
                rs.close();
            }
            return list.toArray(emptyArray);
        }
        return null;
    }
}
