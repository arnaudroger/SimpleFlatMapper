package org.simpleflatmapper.lightningcsv.parser;


public final class TrimCellPreProcessor extends CellPreProcessor {

    private final CellPreProcessor delegate;

    public TrimCellPreProcessor(CellPreProcessor cellPreProcessor) {
        this.delegate = cellPreProcessor;
    }

    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer, int state) {
        int strStart = start;
        int strEnd = end;

        strEnd = strEnd(strStart, strEnd, chars);
        strStart = strStart(strStart, strEnd, chars);

        delegate.newCell(chars, strStart, strEnd, cellConsumer, state);
    }

    private int strEnd(int start, int end, char[] chars) {
        for(; start < end && chars[end - 1] == ' '; end--)
            ;
        return end;
    }

    private int strStart(int start, int end, char[] chars) {
        for(;start < end && chars[start] == ' '; start++)
            ;
        return start;
    }

    @Override
    public boolean ignoreLeadingSpace() {
        return true;
    }
}
