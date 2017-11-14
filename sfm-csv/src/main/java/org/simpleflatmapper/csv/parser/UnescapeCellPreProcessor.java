package org.simpleflatmapper.csv.parser;

public class UnescapeCellPreProcessor extends CellPreProcessor {

    private final TextFormat textFormat;

    public UnescapeCellPreProcessor(TextFormat textFormat) {
        this.textFormat = textFormat;
    }


    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer, int state) {
        if ((state & CharConsumer.QUOTED) == 0) {
            cellConsumer.newCell(chars, start, end - start);
        } else {
            unescape(chars, start + 1, end, cellConsumer);
        }
    }

    private void unescape(final char[] chars, int start, int end, CellConsumer cellConsumer) {
        char escapeChar = textFormat.escapeChar;
        for(int i = start; i < end - 1; i++) {
            if (chars[i] == escapeChar) {
                int destIndex = i;
                boolean escaped = true;
                for(i = i +1 ;i < end -1; i++) {
                    char c = chars[i];
                    if (!escaped) {
                        if (c != escapeChar) {
                            chars[destIndex++] = c;
                        } else {
                            escaped = true;
                        }
                    } else {
                        chars[destIndex++] = unescapeChar(c, escapeChar);
                        escaped = false;
                    }
                }
                char c = chars[end - 1];
                if (c != textFormat.quoteChar || escaped) {
                    chars[destIndex++] = c;
                }
                cellConsumer.newCell(chars, start, destIndex - start);
                return;
            }
        }

        int l = end - start;
        if (l >0 && chars[end -1] == textFormat.quoteChar) {
            l --;
        }
        cellConsumer.newCell(chars, start, l);
    }

    private char unescapeChar(char c, char escapeChar) {
        if (escapeChar == '\\') {
            switch (c) {
                case 'n':
                    return '\n';
                case 'r':
                    return '\r';
                case 't':
                    return '\t';
                case 'b':
                    return '\b';
                case 'f':
                    return '\f';
                case 'v':
                    return 0x0B;
            }
        }
        return c;
    }

    @Override
    public final boolean ignoreLeadingSpace() {
        return false;
    }

}
