package org.simpleflatmapper.csv.parser;

public final class UnescapeCellPreProcessor extends AbstractUnescapeCellPreProcessor {

    private final char escapeChar;

    public UnescapeCellPreProcessor(char escapeChar) {
        this.escapeChar = escapeChar;
    }

    @Override
    protected final boolean isEscapeChar(char c) {
        return c == escapeChar;
    }
}
