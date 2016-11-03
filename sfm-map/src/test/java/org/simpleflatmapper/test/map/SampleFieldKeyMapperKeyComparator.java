package org.simpleflatmapper.test.map;

import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.mapper.MapperKeyComparator;

public final class SampleFieldKeyMapperKeyComparator extends MapperKeyComparator<SampleFieldKey> {

    public final static SampleFieldKeyMapperKeyComparator INSTANCE = new SampleFieldKeyMapperKeyComparator();

    private SampleFieldKeyMapperKeyComparator() {
    }

    @Override
    public int compare(MapperKey<SampleFieldKey> m1, MapperKey<SampleFieldKey> m2) {
        SampleFieldKey[] keys1 = m1.getColumns();
        SampleFieldKey[] keys2 = m2.getColumns();
        int d = keys1.length - keys2.length;
        if (d != 0) {
            return d;
        }
        return compareKeys(keys1, keys2);
    }

    private int compareKeys(SampleFieldKey[] keys1, SampleFieldKey[] keys2) {
        for (int i = 0; i < keys1.length; i++) {
            int d = compare(keys1[i], keys2[i]);
            if (d != 0) {
                return d;
            }
        }
        return 0;
    }

    protected int compare(SampleFieldKey k1, SampleFieldKey k2) {
        int d = k1.getIndex() - k2.getIndex();
        if (d != 0) return d;
        return k1.getName().compareTo(k2.getName());
    }
}
