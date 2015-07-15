package org.sfm.reflect.meta;

import org.sfm.map.FieldKey;

public interface PropertyNameMatcherFactory {
    PropertyNameMatcher newInstance(FieldKey<?> key);
}
