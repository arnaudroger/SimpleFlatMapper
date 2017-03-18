package org.simpleflatmapper.jdbc.impl;

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

    public boolean isPostgresSql() {
        return "PostgreSQL".equals(product);
    }

    @Override
    public String toString() {
        return "DatabaseMeta{" +
                "product='" + product + '\'' +
                ", majorVersion=" + majorVersion +
                ", minorVersion=" + minorVersion +
                '}';
    }

    public boolean isVersionMet(int major, int minor) {
        if (major < majorVersion) {
            return true;
        } else if (major == majorVersion) {
            return minor <= minorVersion;
        }
        return false;
    }
}
