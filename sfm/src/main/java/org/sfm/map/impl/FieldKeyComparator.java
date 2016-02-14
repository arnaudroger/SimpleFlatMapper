package org.sfm.map.impl;

import org.sfm.map.FieldKey;

import java.util.Comparator;

public class FieldKeyComparator implements Comparator<FieldKey<?>> {
    @Override
    public int compare(FieldKey<?> o1, FieldKey<?> o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
