package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.simpleflatmapper.jdbc.impl.getter.ArrayResultSetGetter.VALUE_INDEX;

public class ArrayShortResultSetGetter implements Getter<ResultSet, short[]>, ContextualGetter<ResultSet, short[]> {

    private static final short[] INIT = new short[0];
    private final int index;

    public ArrayShortResultSetGetter(int index) {
        this.index = index;
    }

    @Override
    public short[] get(ResultSet resultSet, Context context) throws Exception {
        return get(resultSet);
    }

    @Override
    public short[] get(ResultSet target) throws Exception {
        Array sqlArray = target.getArray(index);

        if (sqlArray != null) {
            short[] array = INIT;
            int capacity = 0;
            int size = 0;

            ResultSet rs = sqlArray.getResultSet();
            try  {
                while(rs.next()) {
                    if (size >= capacity) {
                        int newCapacity = Math.max(Math.max(capacity+ 1, capacity + (capacity >> 1)), 10);
                        array = Arrays.copyOf(array, newCapacity);
                        capacity = newCapacity;
                    }
                    array[size++] = rs.getShort(VALUE_INDEX);
                }
            } finally {
                rs.close();
            }

            return Arrays.copyOf(array, size);
        }

        return null;
    }
}
