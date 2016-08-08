package org.simpleflatmapper.jdbc;


public class SqlTypeColumnProperty {

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
