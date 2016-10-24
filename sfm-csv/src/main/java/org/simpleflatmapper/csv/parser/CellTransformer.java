package org.simpleflatmapper.csv.parser;

public abstract class CellTransformer {
    public abstract void newCell(char[] chars, int start, int end, CellConsumer cellConsumer);
}
