package org.sfm.map.column;


import java.text.Format;

public class FormatProperty implements ColumnProperty {

    private final Format format;

    public FormatProperty(Format format) {
        this.format = format;
    }

    public Format format() {
        return format;
    }
}
