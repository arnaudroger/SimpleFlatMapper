package org.simpleflatmapper.reflect.meta;

public class PropertyNameMatch {

    private final String property;
    private final PropertyNameMatcher leftOverMatcher;
    public final int score;

    public PropertyNameMatch(String property, PropertyNameMatcher leftOverMatcher, int score) {
        this.property = property;
        this.leftOverMatcher = leftOverMatcher;
        this.score = score;
    }

    public String getProperty() {
        return property;
    }

    public PropertyNameMatcher getLeftOverMatcher() {
        return leftOverMatcher;
    }
}
