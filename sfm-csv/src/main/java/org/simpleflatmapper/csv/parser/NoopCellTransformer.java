package org.simpleflatmapper.csv.parser;

public class NoopCellTransformer extends CellTransformer {
    public void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        cellConsumer.newCell(chars, start, end - start);
    }
}
