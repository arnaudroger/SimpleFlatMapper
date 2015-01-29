package org.sfm.reflect.meta;

/**
 * Created by aroger on 05/12/14.
 */
public class IndexedColumn {

    private final int indexValue;
    private final PropertyNameMatcher subPropertyNameMatcher;

    public IndexedColumn(int indexValue, PropertyNameMatcher subPropertyNameMatcher) {
        this.indexValue = indexValue;
        this.subPropertyNameMatcher = subPropertyNameMatcher;
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
