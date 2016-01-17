package org.sfm.datastax.impl;

public class KeyspaceTable {

    public static final KeyspaceTable NULL = new KeyspaceTable(null, null);

    private final String keyspace;
    private final String table;

    public KeyspaceTable(String keyspace, String table) {
        this.keyspace = keyspace;
        this.table = table;
    }

    public String keyspace() {
        return keyspace;
    }

    public String table() {
        return table;
    }
}
