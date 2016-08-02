package org.simpleflatmapper.core.reflect.meta;

import  org.simpleflatmapper.core.map.FieldKey;

public interface PropertyNameMatcherFactory {
    PropertyNameMatcher newInstance(FieldKey<?> key);
}
