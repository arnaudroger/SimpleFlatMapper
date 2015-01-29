package org.sfm.reflect.meta;

public interface PropertyNameMatcher {
    boolean matches(String property);

    IndexedColumn matchesIndex();

    PropertyNameMatcher partialMatch(String property);

    PropertyNameMatcher newMatcher(String name);
}
