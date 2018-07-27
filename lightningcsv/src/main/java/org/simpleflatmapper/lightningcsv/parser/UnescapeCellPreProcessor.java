package org.simpleflatmapper.lightningcsv.parser;

public class UnescapeCellPreProcessor extends CellPreProcessor {

    private char escapeChar;
    private char quoteChar;

    public UnescapeCellPreProcessor(char escapeChar, char quoteChar) {
        this.escapeChar = escapeChar;
        this.quoteChar = quoteChar;
    }


    public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer, int state) {
        if ((state & ConfigurableCharConsumer.QUOTED) == 0) {
            cellConsumer.newCell(chars, start, end - start);
        } else if ((state & ConfigurableCharConsumer.CONTAINS_ESCAPED_CHAR) == 0) {
            unquote(chars, start + 1, end, cellConsumer);
        } else {
            unescape(chars, start + 1, end, cellConsumer);
        }
    }

    private void unquote(final char[] chars, int start, int end, CellConsumer cellConsumer) {
        int l = end - start;
        if (l > 0 && chars[end - 1] == quoteChar) {
            l--;
        }
        cellConsumer.newCell(chars, start, l);
    }

    private void unescape(final char[] chars, int start, int end, CellConsumer cellConsumer) {
        for(int i = start; i < end - 1; i++) {
            if (chars[i] == escapeChar) {
                unescape(chars, start, end, cellConsumer, i);
                return;
            }
        }
        // no escape Char ??? fall back on unquote
        unquote(chars, start, end, cellConsumer);
    }

    private void unescape(char[] chars, int start, int end, CellConsumer cellConsumer, int currentIndex) {
        int destIndex = currentIndex;
        boolean escaped = true;
        for(int i = currentIndex +1 ;i < end -1; i++) {
            char c = chars[i];
            if (!escaped) {
                if (c != escapeChar) {
                    chars[destIndex++] = c;
                } else {
                    escaped = true;
                }
            } else {
                chars[destIndex++] = unescapeChar(c);
                escaped = false;
            }
        }
        char c = chars[end - 1];
        if (c != quoteChar || escaped) {
            chars[destIndex++] = c;
        }
        cellConsumer.newCell(chars, start, destIndex - start);
    }

    private char unescapeChar(char c) {
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
