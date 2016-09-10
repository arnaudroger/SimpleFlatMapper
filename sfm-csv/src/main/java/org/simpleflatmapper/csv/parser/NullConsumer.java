package org.simpleflatmapper.csv.parser;

public class NullConsumer implements CellConsumer {

    public static final NullConsumer INSTANCE = new NullConsumer();

    private NullConsumer() {
    }

    @Override
    public void newCell(char[] chars, int offset, int length) {
    }

    @Override
    public void endOfRow() {
    }

    @Override
    public void end() {
    }
}
