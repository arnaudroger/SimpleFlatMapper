package org.sfm.jdbc.impl;

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
}
