package org.simpleflatmapper.csv.parser;

public final class UnescapeCellTransformer extends CellTransformer {

    private final TextFormat textFormat;
    private final NoopCellTransformer delegate;

    public UnescapeCellTransformer(TextFormat textFormat) {
        this.textFormat = textFormat;
        this.delegate = new NoopCellTransformer();
    }

    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        int strStart = start;
        int strEnd = end;

        if (strStart < strEnd && textFormat.isEscapeCharacter(chars[strStart])) {
            strStart ++;
            strEnd = unescape(chars, strStart, strEnd);
        }

        delegate.newCell(chars, strStart, strEnd, cellConsumer);
    }

    private int unescape(final char[] chars, final int start, final int end) {
        for(int i = start; i < end - 1; i ++) {
            if (textFormat.isEscapeCharacter(chars[i])) {
                return removeEscapeChars(chars, end, i);
            }
        }

        if (start < end && textFormat.isEscapeCharacter(chars[end - 1])) {
            return end - 1;
        }

        return end;
    }

    private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar) {
        int j = firstEscapeChar;
        boolean escaped = true;
        for(int i = firstEscapeChar + 1;i < end; i++) {
            escaped = textFormat.isEscapeCharacter(chars[i]) && ! escaped;
            if (!escaped) {
                chars[j++] = chars[i];
            }
        }
        return j;
    }
}
