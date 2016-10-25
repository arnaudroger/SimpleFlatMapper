package org.simpleflatmapper.csv.parser;

public final class CsvCharConsumer extends CharConsumer {

    public CsvCharConsumer(CharBuffer csvBuffer, TextFormat textFormat, CellTransformer cellTransformer) {
        super(csvBuffer, textFormat, cellTransformer);
    }

    @Override
    protected final boolean isSeparator(char character) {
        return character == ',';
    }

    @Override
    protected final boolean isNotEscapeCharacter(char character) {
        return character != '"';
    }
}
