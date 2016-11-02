package org.simpleflatmapper.csv.parser;

public abstract class AbstractUnescapeCellPreProcessor extends CellPreProcessor {

    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        int strStart = start;
        int strEnd = end;

        if (strStart < strEnd && isEscapeChar(chars[strStart])) {
            strStart ++;
            strEnd = unescape(chars, strStart, strEnd);
        }

        cellConsumer.newCell(chars, strStart, strEnd - strStart);
    }

    private int unescape(final char[] chars, final int start, final int end) {
        for(int i = start; i < end - 1; i ++) {
            if (isEscapeChar(chars[i])) {
                return removeEscapeChars(chars, end, i);
            }
        }

        if (start < end && isEscapeChar(chars[end - 1])) {
            return end - 1;
        }

        return end;
    }

    private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar) {
        int j = firstEscapeChar;
        boolean escaped = true;
        for(int i = firstEscapeChar + 1;i < end; i++) {
            escaped = isEscapeChar(chars[i]) && ! escaped;
            if (!escaped) {
                chars[j++] = chars[i];
            }
        }
        return j;
    }

    protected abstract boolean isEscapeChar(char c);

    @Override
    public final boolean ignoreLeadingSpace() {
        return false;
    }

}
