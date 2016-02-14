package org.sfm.datastax.impl;

import com.datastax.driver.core.DataType;
import org.sfm.datastax.DatastaxColumnKey;

import java.util.Comparator;

public class DatastaxColumnKeyComparator implements Comparator<DatastaxColumnKey> {
    @Override
    public int compare(DatastaxColumnKey o1, DatastaxColumnKey o2) {
        int d = o1.getName().compareTo(o2.getName());
        if (d != 0) {
            return d;
        }
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
