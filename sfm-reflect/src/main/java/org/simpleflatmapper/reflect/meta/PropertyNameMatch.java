package org.simpleflatmapper.reflect.meta;

public class PropertyNameMatch {

    private final String property;
    private final PropertyNameMatcher leftOverMatcher;

    public PropertyNameMatch(String property, PropertyNameMatcher leftOverMatcher) {
        this.property = property;
        this.leftOverMatcher = leftOverMatcher;
    }

    public String getProperty() {
        return property;
    }

    public PropertyNameMatcher getLeftOverMatcher() {
        return leftOverMatcher;
    }
}
