package org.simpleflatmapper.lightningcsv.parser;

public final class TextFormat {
    
    public static final TextFormat RFC4180 = new TextFormat(',', '"', '"', false); 

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextFormat that = (TextFormat) o;

        if (separatorChar != that.separatorChar) return false;
        if (quoteChar != that.quoteChar) return false;
        if (escapeChar != that.escapeChar) return false;
        return yamlComment == that.yamlComment;
    }

    @Override
    public int hashCode() {
        int result = (int) separatorChar;
        result = 31 * result + (int) quoteChar;
        result = 31 * result + (int) escapeChar;
        result = 31 * result + (yamlComment ? 1 : 0);
        return result;
    }
}
