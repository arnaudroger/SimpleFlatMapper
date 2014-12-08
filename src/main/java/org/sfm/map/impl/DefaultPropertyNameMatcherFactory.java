package org.sfm.map.impl;

import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyNameMatcher;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

public class DefaultPropertyNameMatcherFactory implements PropertyNameMatcherFactory {
    @Override
    public PropertyNameMatcher newInstance(FieldKey<?> key) {
        return new DefaultPropertyNameMatcher(key.getName());
    }
}
