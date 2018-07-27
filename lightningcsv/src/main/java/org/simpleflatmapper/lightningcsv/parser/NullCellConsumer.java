package org.simpleflatmapper.lightningcsv.parser;

public class NullCellConsumer implements CellConsumer {

    public static final NullCellConsumer INSTANCE = new NullCellConsumer();

    private NullCellConsumer() {
    }

    @Override
    public void newCell(char[] chars, int offset, int length) {
    }

    @Override
    public boolean endOfRow() {
        return true;
    }

    @Override
    public void end() {
    }
}
