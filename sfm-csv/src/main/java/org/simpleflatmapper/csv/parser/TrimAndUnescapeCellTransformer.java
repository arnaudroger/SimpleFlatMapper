package org.simpleflatmapper.csv.parser;


public final class TrimAndUnescapeCellTransformer extends CellTransformer {

    private final UnescapeCellTransformer delegate;

    public TrimAndUnescapeCellTransformer(char escapeChar) {
        this.delegate = new UnescapeCellTransformer(escapeChar);
    }

    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        int strStart = start;
        int strEnd = end;

        strEnd = strEnd(strStart, strEnd, chars);
        strStart = strStart(strStart, strEnd, chars);

        delegate.newCell(chars, strStart, strEnd, cellConsumer);
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
}
