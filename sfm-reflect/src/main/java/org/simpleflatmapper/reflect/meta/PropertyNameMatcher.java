package org.simpleflatmapper.reflect.meta;


import java.util.List;


/**
 *
 */
public interface PropertyNameMatcher {


    boolean matches(CharSequence property);

    IndexedColumn matchesIndex();

    PropertyNameMatcher partialMatch(CharSequence property);

    PropertyNameMatch speculativeMatch();

    List<PropertyNameMatcherKeyValuePair> keyValuePairs();
}
