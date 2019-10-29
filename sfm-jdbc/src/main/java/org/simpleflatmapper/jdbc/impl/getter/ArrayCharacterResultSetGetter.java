package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.Arrays;

public class ArrayCharacterResultSetGetter implements Getter<ResultSet, char[]>, ContextualGetter<ResultSet, char[]> {
    private static final char[] INIT = new char[0];
    private final int index;

    public ArrayCharacterResultSetGetter(int index) {
        this.index = index;
    }

    @Override
    public char[] get(ResultSet resultSet, Context context) throws Exception {
        return get(resultSet);
    }

    @Override
    public char[] get(ResultSet target) throws Exception {
        Array sqlArray = target.getArray(index);

        if (sqlArray != null) {
            char[] array = INIT;
            int capacity = 0;
            int size = 0;

            try (ResultSet rs = sqlArray.getResultSet()) {
                while(rs.next()) {
                    if (size >= capacity) {
                        int newCapacity = Math.max(Math.max(capacity+ 1, capacity + (capacity >> 1)), 10);
                        array = Arrays.copyOf(array, newCapacity);
                        capacity = newCapacity;
                    }
                    array[size++] = (char) rs.getInt(1);
                }
            }

            return Arrays.copyOf(array, size);
        }

        return null;
    }
}
