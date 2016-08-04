package org.simpleflatmapper.core.map;


import org.simpleflatmapper.core.reflect.meta.PropertyNameMatcher;

public interface PropertyNameMatcherFactory {
    PropertyNameMatcher newInstance(FieldKey<?> key);
}
