package org.simpleflatmapper.csv.parser;

public class UnescapeCellPreProcessor extends CellPreProcessor {

    private final TextFormat textFormat;

    public UnescapeCellPreProcessor(TextFormat textFormat) {
        this.textFormat = textFormat;
    }


    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer, int state) {
        if ((state & CharConsumer.ESCAPED) == 0) {
            cellConsumer.newCell(chars, start, end - start);
        } else {
            unescape(chars, start, end, cellConsumer);
        }
    }

    private void unescape(final char[] chars, int start, int end, CellConsumer cellConsumer) {
        if (start < end) {
            int i = start + 1;

            char escapeChar = textFormat.escapeChar;
            if (chars[start] == escapeChar) {
                start++;
            }

            while (i < end - 1) {
                if (chars[i] == escapeChar) {
                    removeEscapeChars(chars, start, i, end, cellConsumer);
                    return;
                }
                i++;
            }

            if (i < end && chars[i] == escapeChar) {
                end--;
            }
        }

        cellConsumer.newCell(chars, start, end - start);
    }

    private void removeEscapeChars(final char[] chars, final int start, final int firstEscapeChar, int end, CellConsumer cellConsumer) {
        char escapeChar = textFormat.escapeChar;
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
        cellConsumer.newCell(chars, start, destIndex - start);
    }

    @Override
    public final boolean ignoreLeadingSpace() {
        return false;
    }

}
