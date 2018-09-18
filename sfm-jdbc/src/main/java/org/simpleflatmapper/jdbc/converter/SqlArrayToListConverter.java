package org.simpleflatmapper.jdbc.converter;


import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SqlArrayToListConverter<T> implements ContextualConverter<Array, List<T>> {
    private final Getter<? super ResultSet, ? extends T> getter;

    @SuppressWarnings("unchecked")
    public SqlArrayToListConverter(Getter<? super ResultSet, ? extends T> getter) {
        this.getter = getter;
    }

    @Override
    public List<T> convert(Array in, Context context) throws Exception {
        if (in == null) return null;
        List<T> list = new ArrayList<T>();

        ResultSet rs = in.getResultSet();
        try {
            while(rs.next()) {
                list.add(getter.get(rs));
            }
        } finally {
            rs.close();
        }
        return list;
    }
}
