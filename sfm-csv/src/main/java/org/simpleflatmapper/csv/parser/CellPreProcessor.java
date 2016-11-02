package org.simpleflatmapper.csv.parser;

public abstract class CellPreProcessor {
    public abstract void newCell(char[] chars, int start, int end, CellConsumer cellConsumer);
    public abstract boolean ignoreLeadingSpace();
}
