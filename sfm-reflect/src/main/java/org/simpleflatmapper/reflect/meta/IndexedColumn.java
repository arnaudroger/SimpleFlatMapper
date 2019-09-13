package org.simpleflatmapper.reflect.meta;

public class IndexedColumn {

    private final int indexValue;
    private final String indexProperty;
    private final PropertyNameMatcher subPropertyNameMatcher;
    public final boolean partial;

    public IndexedColumn(int indexValue, PropertyNameMatcher subPropertyNameMatcher) {
        this(indexValue, "", subPropertyNameMatcher, false);
    }

    public IndexedColumn(int indexValue, String indexProperty, PropertyNameMatcher subPropertyNameMatcher, boolean partial) {
        this.indexValue = indexValue;
        this.indexProperty = indexProperty;
        this.subPropertyNameMatcher = subPropertyNameMatcher;
        this.partial = partial;
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

    public IndexedColumn alignTo(int firstElementOffset) {
        if (firstElementOffset == 0) return this;
        return new IndexedColumn(indexValue - firstElementOffset, indexProperty, subPropertyNameMatcher, partial);
    }
}
