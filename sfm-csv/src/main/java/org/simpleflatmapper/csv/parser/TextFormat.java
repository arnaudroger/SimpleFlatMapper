package org.simpleflatmapper.csv.parser;

public final class TextFormat {

    public final char separatorChar;
    public final char escapeChar;
    public final boolean yamlComment;

    public TextFormat(char separatorChar, char escapeChar, boolean yamlComment) {
        this.separatorChar = separatorChar;
        this.escapeChar = escapeChar;
        this.yamlComment = yamlComment;
    }

}
