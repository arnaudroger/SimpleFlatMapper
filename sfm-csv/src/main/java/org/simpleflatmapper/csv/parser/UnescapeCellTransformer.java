package org.simpleflatmapper.csv.parser;

public final class UnescapeCellTransformer extends CellTransformer {

    private final char escapeChar;

    public UnescapeCellTransformer(char escapeChar) {
        this.escapeChar = escapeChar;
    }

    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        int strStart = start;
        int strEnd = end;

        if (strStart < strEnd && chars[strStart] == escapeChar) {
            strStart ++;
            strEnd = unescape(chars, strStart, strEnd);
        }

        cellConsumer.newCell(chars, strStart, strEnd - strStart);
    }

    private int unescape(final char[] chars, final int start, final int end) {
        for(int i = start; i < end - 1; i ++) {
            if (chars[i] == escapeChar) {
                return removeEscapeChars(chars, end, i);
            }
        }

        if (start < end && chars[end - 1]  == escapeChar) {
            return end - 1;
        }

        return end;
    }

    private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar) {
        int j = firstEscapeChar;
        boolean escaped = true;
        for(int i = firstEscapeChar + 1;i < end; i++) {
            escaped = chars[i]  == escapeChar && ! escaped;
            if (!escaped) {
                chars[j++] = chars[i];
            }
        }
        return j;
    }
}
