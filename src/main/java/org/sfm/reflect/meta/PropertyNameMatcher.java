package org.sfm.reflect.meta;

import org.sfm.tuples.Tuple2;


/**
 *
 */
public interface PropertyNameMatcher {

    /**
     *
     * @param property
     * @return
     */
    boolean matches(CharSequence property);

    /**
     *
     * @return
     */
    IndexedColumn matchesIndex();

    /**
     *
     * @param property
     * @return
     */
    PropertyNameMatcher partialMatch(CharSequence property);

    /**
     *
     * @return
     */
    Tuple2<String,PropertyNameMatcher> speculativeMatch();
}
