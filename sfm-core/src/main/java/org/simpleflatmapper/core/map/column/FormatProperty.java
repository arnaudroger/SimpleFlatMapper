package org.simpleflatmapper.core.map.column;


import java.text.Format;

public class FormatProperty implements ColumnProperty {

    private final Format format;

    public FormatProperty(Format format) {
        this.format = (Format) format.clone();
    }

    public Format format() {
        return format;
    }
}
