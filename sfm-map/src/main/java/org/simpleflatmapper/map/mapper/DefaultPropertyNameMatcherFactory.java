package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;
import org.simpleflatmapper.map.PropertyNameMatcherFactory;

public class DefaultPropertyNameMatcherFactory implements PropertyNameMatcherFactory {

    public static final DefaultPropertyNameMatcherFactory DEFAULT = new DefaultPropertyNameMatcherFactory(false, false);
    public static final DefaultPropertyNameMatcherFactory CASE_SENSITIVE = DEFAULT.caseSensitive(true);
    public static final DefaultPropertyNameMatcherFactory EXACT_MATCH = DEFAULT.exactMatch(true);
    public static final DefaultPropertyNameMatcherFactory CASE_SENSITIVE_EXACT_MATCH = CASE_SENSITIVE.exactMatch(true);

    private final boolean exactMatch;
    private final boolean caseSensitive;

    private DefaultPropertyNameMatcherFactory(boolean exactMatch, boolean caseSensitive) {
        this.exactMatch = exactMatch;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public PropertyNameMatcher newInstance(FieldKey<?> key) {
        return new DefaultPropertyNameMatcher(key.getName(), 0, exactMatch, caseSensitive);
    }

    public DefaultPropertyNameMatcherFactory exactMatch(boolean exactMatch) {
        return new DefaultPropertyNameMatcherFactory(exactMatch, caseSensitive);
    }

    public DefaultPropertyNameMatcherFactory caseSensitive(boolean caseSensitive) {
        return new DefaultPropertyNameMatcherFactory(exactMatch, caseSensitive);
    }
}
