package org.sfm.reflect.meta;

/**
 * Created by e19224 on 08/12/2014.
 */
public interface PropertyNameMatcher {
    boolean matches(String property);

    IndexedColumn matchesIndex();

    PropertyNameMatcher partialMatch(String property);
}
