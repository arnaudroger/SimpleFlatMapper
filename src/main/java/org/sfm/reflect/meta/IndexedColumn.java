package org.sfm.reflect.meta;

/**
 * Created by aroger on 05/12/14.
 */
public class IndexedColumn {

    private final String indexName;
    private final int indexValue;
    private final String propertyName;

    public IndexedColumn(String indexName, int indexValue, String propertyName) {
        this.indexName = indexName;
        this.indexValue = indexValue;
        this.propertyName = propertyName;
    }

    public String getIndexName() {
        return indexName;
    }

    public int getIndexValue() {
        return indexValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean hasProperty() {
        return propertyName != null;
    }
}
