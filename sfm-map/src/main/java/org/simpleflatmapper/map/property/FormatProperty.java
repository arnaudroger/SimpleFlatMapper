package org.simpleflatmapper.map.property;


import java.text.Format;

public class FormatProperty {

    private final Format format;

    public FormatProperty(Format format) {
        this.format = (Format) format.clone();
    }

    public Format format() {
        return format;
    }

    @Override
    public String toString() {
        return "Format{" + format + '}';
    }
}
