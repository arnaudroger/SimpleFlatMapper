package org.simpleflatmapper.csv.parser;

public final class TextFormat {

    private final char separatorChar;
    private final char escapeChar;

    public TextFormat(char separatorChar, char escapeChar) {
        this.separatorChar = separatorChar;
        this.escapeChar = escapeChar;
    }

    public final boolean isNotEscapeCharacter(char character) {
        return character != escapeChar;
    }

    public final boolean isEscapeCharacter(char character) {
        return character == escapeChar;
    }

    public final boolean isSeparator(char character) {
        return character == separatorChar;
    }

}
