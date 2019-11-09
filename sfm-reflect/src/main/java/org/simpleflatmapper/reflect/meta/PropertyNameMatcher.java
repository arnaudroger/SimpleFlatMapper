package org.simpleflatmapper.reflect.meta;


import java.util.List;


/**
 *
 */
public interface PropertyNameMatcher {

    PropertyNameMatch matches(CharSequence property, boolean tryPlural);
    PropertyNameMatch matches(CharSequence property);

    IndexedColumn matchIndex();

    PropertyNameMatch partialMatch(CharSequence property);
    PropertyNameMatch partialMatch(CharSequence property, boolean tryPlural);

    PropertyNameMatch speculativeMatch();

    List<PropertyNameMatcherKeyValuePair> keyValuePairs();

    int asScore();

}
