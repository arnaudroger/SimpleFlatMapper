package org.simpleflatmapper.reflect.meta;

public class IndexedColumn {

    private final int indexValue;
    private final String indexProperty;
    private final PropertyNameMatcher subPropertyNameMatcher;
    public IndexedColumn(int indexValue, PropertyNameMatcher subPropertyNameMatcher) {
        this(indexValue, "", subPropertyNameMatcher);
    }

    public IndexedColumn(int indexValue, String indexProperty, PropertyNameMatcher subPropertyNameMatcher) {
        this.indexValue = indexValue;
        this.indexProperty = indexProperty;
        this.subPropertyNameMatcher = subPropertyNameMatcher;
    }

    public int getIndexValue() {
        return indexValue;
    }

    public PropertyNameMatcher getSubPropertyNameMatcher() {
        return subPropertyNameMatcher;
    }

    public String getIndexProperty() {
        return indexProperty;
    }
}
