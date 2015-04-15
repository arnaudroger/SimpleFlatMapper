package org.sfm.map.column;

public class DateFormatProperty implements ColumnProperty {

    private final String pattern;

    public DateFormatProperty(String pattern) {
        if (pattern == null) throw new NullPointerException();
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return "DateFormat{'" + pattern + "'}";
    }
}
