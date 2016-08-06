package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.DataType;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.mapper.MapperKeyComparator;


public class DatastaxMapperKeyComparator extends MapperKeyComparator<DatastaxColumnKey> {
    public static final DatastaxMapperKeyComparator INSTANCE = new DatastaxMapperKeyComparator();

    @Override
    public int compare(MapperKey<DatastaxColumnKey> m1, MapperKey<DatastaxColumnKey> m2) {
        DatastaxColumnKey[] keys1 = m1.getColumns();
        DatastaxColumnKey[] keys2 = m2.getColumns();
        int d = keys1.length - keys2.length;
        if (d != 0){
            return d;
        }
        return compareKeys(keys1, keys2);
    }

    private int compareKeys(DatastaxColumnKey[] keys1, DatastaxColumnKey[] keys2) {
        for(int i = 0; i < keys1.length; i++) {
            int d = compare(keys1[i], keys2[i]);
            if (d!= 0) {
                return d;
            }
        }
        return 0;
    }
    private  int compare(DatastaxColumnKey o1, DatastaxColumnKey o2) {
        int d = o1.getName().compareTo(o2.getName());
        if (d != 0) {
            return d;
        }

        d = o1.getIndex() - o2.getIndex();
        if (d != 0) return d;

        final DataType o1DataType = o1.getDataType();
        final DataType o2DataType = o2.getDataType();
        if (o1DataType == null) {
            if (o2DataType == null) return 0;
            else return -1;
        } else {
            if (o2DataType == null) return 1;
            return o1DataType.getName().compareTo(o2.getDataType().getName());
        }
    }
}
