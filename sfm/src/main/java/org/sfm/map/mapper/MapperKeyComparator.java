package org.sfm.map.mapper;

import org.sfm.map.FieldKey;
import org.sfm.map.mapper.MapperKey;
import org.sfm.utils.Asserts;

import java.util.Comparator;

public final class MapperKeyComparator<K extends FieldKey<K>>  implements Comparator<MapperKey<K>> {

    private final Comparator<? super K> keyComparator;

    public MapperKeyComparator(Comparator<? super K> keyComparator) {
        this.keyComparator = Asserts.requireNonNull("keyComparator", keyComparator);
    }

    @Override
    public int compare(MapperKey<K> m1, MapperKey<K> m2) {
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
            int d = keyComparator.compare(keys1[i], keys2[i]);
            if (d!= 0) {
                return d;
            }
        }
        return 0;
    }
}
