package org.simpleflatmapper.csv;

import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.mapper.MapperKeyComparator;

public final class CsvColumnKeyMapperKeyComparator extends MapperKeyComparator<CsvColumnKey> {

    public final static CsvColumnKeyMapperKeyComparator INSTANCE = new CsvColumnKeyMapperKeyComparator();

    private CsvColumnKeyMapperKeyComparator() {
    }

    @Override
    public int compare(MapperKey<CsvColumnKey> m1, MapperKey<CsvColumnKey> m2) {
        CsvColumnKey[] keys1 = m1.getColumns();
        CsvColumnKey[] keys2 = m2.getColumns();
        int d = keys1.length - keys2.length;
        if (d != 0) {
            return d;
        }
        return compareKeys(keys1, keys2);
    }

    private int compareKeys(CsvColumnKey[] keys1, CsvColumnKey[] keys2) {
        for (int i = 0; i < keys1.length; i++) {
            int d = compare(keys1[i], keys2[i]);
            if (d != 0) {
                return d;
            }
        }
        return 0;
    }

    private int compare(CsvColumnKey k1, CsvColumnKey k2) {
        int d = k1.getIndex() - k2.getIndex();
        if (d != 0) return d;
        return k1.getName().compareTo(k2.getName());
    }
}