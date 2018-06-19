package org.simpleflatmapper.lightningcsv.parser;

public final class NoopCellPreProcessor extends CellPreProcessor {
    public static final NoopCellPreProcessor INSTANCE = new NoopCellPreProcessor();

    private NoopCellPreProcessor() {
    }

    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer, int state) {
        cellConsumer.newCell(chars, start, end - start);
    }

    @Override
    public final boolean ignoreLeadingSpace() {
        return false;
    }
}
