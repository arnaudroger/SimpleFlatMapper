package org.simpleflatmapper.csv.parser;

public class UnescapeCellPreProcessor extends CellPreProcessor {

    private final TextFormat textFormat;

    public UnescapeCellPreProcessor(TextFormat textFormat) {
        this.textFormat = textFormat;
    }


    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
        int strStart = start;
        int strEnd = end;

        int escapeChar = textFormat.escapeChar;
        if (strStart < strEnd && chars[strStart] == escapeChar) {
            strStart ++;
            strEnd = unescape(chars, strStart, strEnd, escapeChar);
        }

        cellConsumer.newCell(chars, strStart, strEnd - strStart);
    }

    private int unescape(final char[] chars, final int start, final int end, final int escapeChar) {
        for(int i = start; i < end - 1; i ++) {
            if (chars[i] == escapeChar) {
                return removeEscapeChars(chars, end, i, escapeChar);
            }
        }

        if (start < end && chars[end - 1] == escapeChar) {
            return end - 1;
        }

        return end;
    }

    private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar, final int escapeChar) {
        int destIndex = firstEscapeChar;
        boolean escaped = true;
        for(int sourceIndex = firstEscapeChar + 1;sourceIndex < end; sourceIndex++) {
            char c = chars[sourceIndex];
            if (c != escapeChar || escaped) {
                chars[destIndex++] = c;
                escaped = false;
            } else {
                escaped = true;
            }
        }
        return destIndex;
    }

    @Override
    public final boolean ignoreLeadingSpace() {
        return false;
    }

}
