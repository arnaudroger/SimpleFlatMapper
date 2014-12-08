package org.sfm.reflect.meta;

/**
 * Created by aroger on 05/12/14.
 */
public class IndexedColumn {

    private final String indexName;
    private final int indexValue;
    private final PropertyNameMatcher subPropertyNameMatcher;

    public IndexedColumn(String indexName, int indexValue, PropertyNameMatcher subPropertyNameMatcher) {
        this.indexName = indexName;
        this.indexValue = indexValue;
        this.subPropertyNameMatcher = subPropertyNameMatcher;
    }

    public String getIndexName() {
        return indexName;
    }

    public int getIndexValue() {
        return indexValue;
    }

    public PropertyNameMatcher getSubPropertyNameMatcher() {
        return subPropertyNameMatcher;
    }

    public boolean hasSubProperty() {
        return subPropertyNameMatcher != null;
    }
}
