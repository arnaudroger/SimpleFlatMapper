package org.sfm.map.column;

import static org.sfm.utils.Asserts.requireNonNull;

public class DateFormatProperty implements ColumnProperty {

    private final String pattern;

    public DateFormatProperty(String pattern) {
        this.pattern = requireNonNull("pattern", pattern);
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "DateFormat{'" + pattern + "'}";
    }
}
