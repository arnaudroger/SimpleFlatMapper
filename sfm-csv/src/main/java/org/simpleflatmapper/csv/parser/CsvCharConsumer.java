package org.simpleflatmapper.csv.parser;

public final class CsvCharConsumer extends CharConsumer {

    public static final char escapeChar = '"';
    public static final char separatorChar = ',';

    public CsvCharConsumer(CharBuffer csvBuffer) {
        super(csvBuffer);
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
        CsvUnescapeCellPreProcessor.INSTANCE.newCell(chars, start, end, cellConsumer);
    }

    @Override
    protected boolean isNotIgnoringLeadingSpace() {
        return true;
    }
}
