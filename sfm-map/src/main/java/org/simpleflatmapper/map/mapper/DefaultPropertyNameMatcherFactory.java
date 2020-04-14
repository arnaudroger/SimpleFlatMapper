package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;
import org.simpleflatmapper.map.PropertyNameMatcherFactory;
import org.simpleflatmapper.util.CharPredicate;

import java.util.Arrays;

public class DefaultPropertyNameMatcherFactory implements PropertyNameMatcherFactory {

    public static final DefaultPropertyNameMatcherFactory DEFAULT = new DefaultPropertyNameMatcherFactory(false, false, DefaultPropertyNameMatcher.DEFAULT_IS_SEPARATOR_CHAR);
    public static final DefaultPropertyNameMatcherFactory CASE_SENSITIVE = DEFAULT.caseSensitive(true);
    public static final DefaultPropertyNameMatcherFactory EXACT_MATCH = DEFAULT.exactMatch(true);
    public static final DefaultPropertyNameMatcherFactory CASE_SENSITIVE_EXACT_MATCH = CASE_SENSITIVE.exactMatch(true);

    private final boolean exactMatch;
    private final boolean caseSensitive;
    private final CharPredicate isSeparatorChar;

    private DefaultPropertyNameMatcherFactory(boolean exactMatch, boolean caseSensitive, CharPredicate isSeparatorChar) {
        this.exactMatch = exactMatch;
        this.caseSensitive = caseSensitive;
        this.isSeparatorChar = isSeparatorChar;
    }

    @Override
    public PropertyNameMatcher newInstance(FieldKey<?> key) {
        return new DefaultPropertyNameMatcher(key.getName(), 0, exactMatch, caseSensitive, isSeparatorChar);
    }

    public DefaultPropertyNameMatcherFactory exactMatch(boolean exactMatch) {
        return new DefaultPropertyNameMatcherFactory(exactMatch, caseSensitive, isSeparatorChar);
    }

    public DefaultPropertyNameMatcherFactory caseSensitive(boolean caseSensitive) {
        return new DefaultPropertyNameMatcherFactory(exactMatch, caseSensitive, isSeparatorChar);
    }

    public DefaultPropertyNameMatcherFactory separatorCharPredicate(CharPredicate isSeparatorChar) {
        return new DefaultPropertyNameMatcherFactory(exactMatch, caseSensitive, isSeparatorChar);
    }

    public DefaultPropertyNameMatcherFactory addSeparators(final char... separators) {
        final char[] separatorsCopy = Arrays.copyOf(separators, separators.length);
        return new DefaultPropertyNameMatcherFactory(exactMatch, caseSensitive, new CharPredicate() {
            @Override
            public boolean apply(char c) {
                return isSeparatorChar.apply(c) || arrayContains(separatorsCopy, c);
            }

            private boolean arrayContains(char[] separatorsCopy, char c) {
                for(char cc : separatorsCopy) {
                    if (cc == c) return true;
                }
                return false;
            }
        });
    }
}
