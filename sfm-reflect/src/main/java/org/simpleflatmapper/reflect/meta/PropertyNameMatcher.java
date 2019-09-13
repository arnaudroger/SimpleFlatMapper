package org.simpleflatmapper.reflect.meta;


import java.util.List;


/**
 *
 */
public interface PropertyNameMatcher {

    PropertyNameMatch matches(CharSequence property);

    IndexedColumn matchIndex();

    PropertyNameMatch partialMatch(CharSequence property);

    PropertyNameMatch speculativeMatch();

    List<PropertyNameMatcherKeyValuePair> keyValuePairs();

    int asScore();

}
