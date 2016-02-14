package org.sfm.map.impl;

import org.sfm.map.FieldKey;
import org.sfm.map.mapper.MapperKey;

import java.util.Comparator;

public abstract class AbstractMapperKeyComparator<K extends FieldKey<K>>  implements Comparator<MapperKey<K>> {


    public AbstractMapperKeyComparator() {
    }

    @Override
    public final int compare(MapperKey<K> m1, MapperKey<K> m2) {
        K[] keys1 = m1.getColumns();
        K[] keys2 = m2.getColumns();
        int d = keys1.length - keys2.length;
        if (d != 0){
            return d;
        }
        return compareKeys(keys1, keys2);
    }

    private int compareKeys(K[] keys1, K[] keys2) {
        for(int i = 0; i < keys1.length; i++) {
            int d = compareKey(keys1[i], keys2[i]);
            if (d!= 0) {
                return d;
            }
        }
        return 0;
    }

    protected abstract int compareKey(K k1, K k2);
}
