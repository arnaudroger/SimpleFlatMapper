package org.simpleflatmapper.csv.parser;

public final class TextFormat {

    public final char separatorChar;
    public final char quoteChar;
    public final char escapeChar;
    public final boolean yamlComment;

    public TextFormat(char separatorChar, char quoteChar, char escapeChar, boolean yamlComment) {
        this.separatorChar = separatorChar;
        this.quoteChar = quoteChar;
        this.escapeChar = escapeChar;
        this.yamlComment = yamlComment;
    }

}
