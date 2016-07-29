package org.sfm.jdbc;

import org.sfm.map.column.ColumnProperty;

public class SqlTypeColumnProperty implements ColumnProperty {

    private final int sqlType;

    private SqlTypeColumnProperty(int sqlType) {
        this.sqlType = sqlType;
    }

    public int getSqlType() {
        return sqlType;
    }

    public static SqlTypeColumnProperty of(int sqlType) {
        return new SqlTypeColumnProperty(sqlType);
    }
}
