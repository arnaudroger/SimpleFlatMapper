package org.simpleflatmapper.csv.parser;

public final class NoopYamlTrimeCellPreProcessor extends CellPreProcessor {

    public final static NoopYamlTrimeCellPreProcessor INSTANCE = new NoopYamlTrimeCellPreProcessor();

    private NoopYamlTrimeCellPreProcessor() {
    }
    @Override
    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        cellConsumer.newCell(chars, start, end - start);
    }

    public boolean ignoreLeadingSpace() {
        return true;
    }
}
