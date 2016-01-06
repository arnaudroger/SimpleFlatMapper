package org.sfm.jdbc.impl;

public class DatabaseMeta {

    private final String product;
    private final int majorVersion;
    private final int minorVersion;

    public DatabaseMeta(String product, int majorVersion, int minorVersion) {
        this.product = product;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public boolean isMysql() {
        return "MySQL".equals(product);
    }

    @Override
    public String toString() {
        return "DatabaseMeta{" +
                "product='" + product + '\'' +
                ", majorVersion=" + majorVersion +
                ", minorVersion=" + minorVersion +
                '}';
    }
}
