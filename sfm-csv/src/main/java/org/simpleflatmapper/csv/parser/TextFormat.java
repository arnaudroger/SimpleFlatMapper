package org.simpleflatmapper.csv.parser;

public final class TextFormat {

    public final char separatorChar;
    public final char escapeChar;

    public TextFormat(char separatorChar, char escapeChar) {
        this.separatorChar = separatorChar;
        this.escapeChar = escapeChar;
    }

}
