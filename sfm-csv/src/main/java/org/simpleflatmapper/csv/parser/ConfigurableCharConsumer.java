package org.simpleflatmapper.csv.parser;


public final class ConfigurableCharConsumer extends CharConsumer {

    private final char escapeChar;
    private final char separatorChar;
    private final CellPreProcessor cellPreProcessor;
    private final boolean notIgnoringLeadingSpace;

    public ConfigurableCharConsumer(CharBuffer csvBuffer, TextFormat textFormat, CellPreProcessor cellPreProcessor) {
        super(csvBuffer);
        this.cellPreProcessor = cellPreProcessor;
        this.escapeChar = textFormat.getEscapeChar();
        this.separatorChar = textFormat.getSeparatorChar();
        this.notIgnoringLeadingSpace = !cellPreProcessor.ignoreLeadingSpace();
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
        cellPreProcessor.newCell(chars, start, end, cellConsumer);
    }

    @Override
    protected boolean isNotIgnoringLeadingSpace() {
        return notIgnoringLeadingSpace;
    }
}
