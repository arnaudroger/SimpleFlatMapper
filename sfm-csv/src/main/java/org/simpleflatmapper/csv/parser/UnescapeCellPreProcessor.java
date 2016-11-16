package org.simpleflatmapper.csv.parser;

public class UnescapeCellPreProcessor extends CellPreProcessor {

    private final TextFormat textFormat;

    public UnescapeCellPreProcessor(TextFormat textFormat) {
        this.textFormat = textFormat;
    }


    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        int strStart = start;
        int strEnd = end;

        if (strStart < strEnd && chars[strStart] == textFormat.escapeChar) {
            strStart ++;
            strEnd = unescape(chars, strStart, strEnd);
        }

        cellConsumer.newCell(chars, strStart, strEnd - strStart);
    }

    private int unescape(final char[] chars, final int start, final int end) {
        for(int i = start; i < end - 1; i ++) {
            if (chars[i] == textFormat.escapeChar) {
                return removeEscapeChars(chars, end, i);
            }
        }

        if (start < end && chars[end - 1] == textFormat.escapeChar) {
            return end - 1;
        }

        return end;
    }

    private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar) {
        int j = firstEscapeChar;
        boolean escaped = true;
        for(int i = firstEscapeChar + 1;i < end; i++) {
            escaped = chars[i] == textFormat.escapeChar && ! escaped;
            if (!escaped) {
                chars[j++] = chars[i];
            }
        }
        return j;
    }

    @Override
    public final boolean ignoreLeadingSpace() {
        return false;
    }

}
