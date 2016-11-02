package org.simpleflatmapper.csv.parser;

public final class CsvUnescapeCellPreProcessor extends AbstractUnescapeCellPreProcessor {

    public static final CsvUnescapeCellPreProcessor INSTANCE = new CsvUnescapeCellPreProcessor();

    private CsvUnescapeCellPreProcessor() {
    }

    @Override
    protected final boolean isEscapeChar(char c) {
        return c == '"';
    }
}
