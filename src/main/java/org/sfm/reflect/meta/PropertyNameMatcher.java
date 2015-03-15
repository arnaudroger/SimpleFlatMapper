package org.sfm.reflect.meta;

import org.sfm.tuples.Tuple2;

public interface PropertyNameMatcher {
    boolean matches(String property);
    IndexedColumn matchesIndex();
    PropertyNameMatcher partialMatch(String property);

    Tuple2<String,PropertyNameMatcher> speculativeMatch();
}
