package org.sfm.map.impl;

import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyNameMatcher;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

public class DefaultPropertyNameMatcherFactory implements PropertyNameMatcherFactory {

    private final boolean exactMatch;
    private final boolean caseSensitive;

    public DefaultPropertyNameMatcherFactory(boolean exactMatch, boolean caseSensitive) {
        this.exactMatch = exactMatch;
        this.caseSensitive = caseSensitive;
    }

    public DefaultPropertyNameMatcherFactory() {
        this(false, false);
    }


    @Override
    public PropertyNameMatcher newInstance(FieldKey<?> key) {
        return new DefaultPropertyNameMatcher(key.getName());
    }
}
