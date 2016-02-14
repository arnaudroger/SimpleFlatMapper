package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcColumnKey;

public class JdbcColumnKeyComparator implements java.util.Comparator<org.sfm.jdbc.JdbcColumnKey> {
    @Override
    public int compare(JdbcColumnKey o1, JdbcColumnKey o2) {
        int d = o1.getName().compareTo(o2.getName());
        if (d != 0) return d;
        return o1.getSqlType() - o2.getSqlType();
    }
}
