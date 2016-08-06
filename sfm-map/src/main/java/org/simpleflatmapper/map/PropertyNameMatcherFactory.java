package org.simpleflatmapper.map;


import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;

public interface PropertyNameMatcherFactory {
    PropertyNameMatcher newInstance(FieldKey<?> key);
}
