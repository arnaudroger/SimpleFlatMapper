package org.simpleflatmapper.lightningcsv.parser;

public class IgnoreCellConsumer implements CellConsumer {

    public static final IgnoreCellConsumer INSTANCE = new IgnoreCellConsumer();

    private IgnoreCellConsumer() {
    }

    @Override
    public void newCell(char[] chars, int offset, int length) {
        // ignore
    }

    @Override
    public boolean endOfRow() {
        return false;
    }

    @Override
    public void end() {
        // ignore
    }
}
