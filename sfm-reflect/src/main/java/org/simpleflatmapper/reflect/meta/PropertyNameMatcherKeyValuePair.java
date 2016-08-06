package org.simpleflatmapper.reflect.meta;

public class PropertyNameMatcherKeyValuePair {

    private final PropertyNameMatcher key;
    private final PropertyNameMatcher value;

    public PropertyNameMatcherKeyValuePair(PropertyNameMatcher key, PropertyNameMatcher value) {
        this.key = key;
        this.value = value;
    }

    public PropertyNameMatcher getKey() {
        return key;
    }

    public PropertyNameMatcher getValue() {
        return value;
    }
}
