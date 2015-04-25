package org.sfm.reflect.meta;

import org.sfm.tuples.Tuple2;

public interface PropertyNameMatcher {
    boolean matches(CharSequence property);
    IndexedColumn matchesIndex();
    PropertyNameMatcher partialMatch(CharSequence property);

    Tuple2<String,PropertyNameMatcher> speculativeMatch();
}
