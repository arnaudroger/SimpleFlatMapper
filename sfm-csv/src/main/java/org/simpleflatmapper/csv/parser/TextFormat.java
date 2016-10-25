package org.simpleflatmapper.csv.parser;

public final class TextFormat {

    private final char separatorChar;
    private final char escapeChar;

    public TextFormat(char separatorChar, char escapeChar) {
        this.separatorChar = separatorChar;
        this.escapeChar = escapeChar;
    }

    public char getSeparatorChar() {
        return separatorChar;
    }

    public char getEscapeChar() {
        return escapeChar;
    }
}
