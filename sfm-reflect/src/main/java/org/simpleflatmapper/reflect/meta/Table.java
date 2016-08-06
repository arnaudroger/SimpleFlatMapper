package org.simpleflatmapper.reflect.meta;

public class Table {

    public static final Table NULL = new Table(null, null, null);

    private final String catalog;
    private final String schema;
    private final String table;

    public Table(String catalog, String schema, String table) {
        this.catalog = catalog;
        this.schema = schema;
        this.table = table;
    }

    public String schema() {
        return schema;
    }

    public String table() {
        return table;
    }

    public String catalog() {
        return catalog;
    }

    public static boolean isNull(Table table) {
        return table == null || table == NULL;
    }
}
