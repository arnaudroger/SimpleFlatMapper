package org.simpleflatmapper.csv.parser;


public final class ConfigurableCharConsumer extends CharConsumer {

    private final char escapeChar;
    private final char separatorChar;
    private CellTransformer cellTransformer;

    public ConfigurableCharConsumer(CharBuffer csvBuffer, TextFormat textFormat, CellTransformer cellTransformer) {
        super(csvBuffer);
        this.cellTransformer = cellTransformer;
        this.escapeChar = textFormat.getEscapeChar();
        this.separatorChar = textFormat.getSeparatorChar();
    }

    @Override
    protected final boolean isSeparator(char character) {
        return character == separatorChar;
    }

    @Override
    protected final boolean isNotEscapeCharacter(char character) {
        return character != escapeChar;
    }

    @Override
    protected void pushCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        cellTransformer.newCell(chars, start, end, cellConsumer);
    }
}
