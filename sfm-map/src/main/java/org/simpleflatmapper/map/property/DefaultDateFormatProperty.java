package org.simpleflatmapper.map.property;

import org.simpleflatmapper.util.date.DefaultDateFormatSupplier;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class DefaultDateFormatProperty implements DefaultDateFormatSupplier {

    private final String pattern;

    public DefaultDateFormatProperty(String pattern) {
        this.pattern = requireNonNull("pattern", pattern);
    }

    public String get() {
        return pattern;
    }

    @Override
    public String toString() {
        return "DefaultDateFormat{'" + pattern + "'}";
    }
}
