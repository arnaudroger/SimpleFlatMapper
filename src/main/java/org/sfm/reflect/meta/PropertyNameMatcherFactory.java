package org.sfm.reflect.meta;

import org.sfm.map.impl.FieldKey;

public interface PropertyNameMatcherFactory {
    PropertyNameMatcher newInstance(FieldKey<?> key);
}
