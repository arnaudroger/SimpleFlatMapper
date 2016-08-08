package org.simpleflatmapper.map.property;

import org.simpleflatmapper.util.date.DateFormatSupplier;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class DateFormatProperty implements DateFormatSupplier {

    private final String pattern;

    public DateFormatProperty(String pattern) {
        this.pattern = requireNonNull("pattern", pattern);
    }

    public String get() {
        return pattern;
    }

    @Override
    public String toString() {
        return "DateFormat{'" + pattern + "'}";
    }
}
