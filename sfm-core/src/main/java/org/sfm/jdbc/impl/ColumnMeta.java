package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcColumnKey;

public class ColumnMeta {
    private final String column;
    private final int sqlType;
    private final boolean key;
    private final boolean generated;

    public ColumnMeta(String column, int sqlType, boolean key, boolean generated) {
        this.column = column;
        this.sqlType = sqlType;
        this.key = key;
        this.generated = generated;
    }

    public String getColumn() {
        return column;
    }

    public int getSqlType() {
        return sqlType;
    }

    public boolean isKey() {
        return key;
    }

    public boolean isGenerated() {
        return generated;
    }

    public JdbcColumnKey toJdbcColumnKey(int index) {
        return new JdbcColumnKey(column, index, sqlType);
    }
}
