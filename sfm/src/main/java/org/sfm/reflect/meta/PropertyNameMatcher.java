package org.sfm.reflect.meta;

import org.sfm.tuples.Tuple2;

import java.util.List;


/**
 *
 */
public interface PropertyNameMatcher {


    boolean matches(CharSequence property);

    IndexedColumn matchesIndex();

    PropertyNameMatcher partialMatch(CharSequence property);

    Tuple2<String,PropertyNameMatcher> speculativeMatch();

    List<Tuple2<PropertyNameMatcher, PropertyNameMatcher>> keyValuePairs();
}
