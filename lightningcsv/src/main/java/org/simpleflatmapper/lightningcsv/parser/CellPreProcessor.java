package org.simpleflatmapper.lightningcsv.parser;

public abstract class CellPreProcessor {
    public abstract void newCell(char[] chars, int start, int end, CellConsumer cellConsumer, int state);
    public abstract boolean ignoreLeadingSpace();
}
