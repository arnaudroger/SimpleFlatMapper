package org.simpleflatmapper.reflect.meta;

public class IndexedColumn {

    private final int indexValue;
    private final String indexProperty;
    private final PropertyNameMatcher subPropertyNameMatcher;
    private final int score;
    public final boolean partial;

    public IndexedColumn(int indexValue, PropertyNameMatcher subPropertyNameMatcher, int score) {
        this(indexValue, "", subPropertyNameMatcher, score, false);
    }

    public IndexedColumn(int indexValue, String indexProperty, PropertyNameMatcher subPropertyNameMatcher, int score, boolean partial) {
        this.indexValue = indexValue;
        this.indexProperty = indexProperty;
        this.subPropertyNameMatcher = subPropertyNameMatcher;
        this.score = score;
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
        return new IndexedColumn(indexValue - firstElementOffset, indexProperty, subPropertyNameMatcher, score, partial);
    }

    public int getScore() {
        return score;
    }
}
