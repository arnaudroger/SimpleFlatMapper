package org.simpleflatmapper.core.map.column;

import static org.simpleflatmapper.core.utils.Asserts.requireNonNull;

public class DefaultDateFormatProperty implements ColumnProperty {

    private final String pattern;

    public DefaultDateFormatProperty(String pattern) {
        this.pattern = requireNonNull("pattern", pattern);
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "DefaultDateFormatProperty{'" + pattern + "'}";
    }
}
