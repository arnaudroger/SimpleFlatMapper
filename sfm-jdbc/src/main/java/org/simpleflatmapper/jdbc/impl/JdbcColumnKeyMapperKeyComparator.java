package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.mapper.MapperKeyComparator;

public final class JdbcColumnKeyMapperKeyComparator extends MapperKeyComparator<JdbcColumnKey> {

    public final static JdbcColumnKeyMapperKeyComparator INSTANCE = new JdbcColumnKeyMapperKeyComparator();

    private JdbcColumnKeyMapperKeyComparator() {
    }

    @Override
    public int compare(MapperKey<JdbcColumnKey> m1, MapperKey<JdbcColumnKey> m2) {
        JdbcColumnKey[] keys1 = m1.getColumns();
        JdbcColumnKey[] keys2 = m2.getColumns();
        int d = keys1.length - keys2.length;
        if (d != 0) {
            return d;
        }
        return compareKeys(keys1, keys2);
    }

    private int compareKeys(JdbcColumnKey[] keys1, JdbcColumnKey[] keys2) {
        for (int i = 0; i < keys1.length; i++) {
            int d = compare(keys1[i], keys2[i]);
            if (d != 0) {
                return d;
            }
        }
        return 0;
    }

    protected int compare(JdbcColumnKey k1, JdbcColumnKey k2) {
        int d = k1.getIndex() - k2.getIndex();
        if (d != 0) return d;
        d = k1.getName().compareTo(k2.getName());
        if (d != 0) return d;
        return k1.getSqlType(null) - k2.getSqlType(null);
    }
}
