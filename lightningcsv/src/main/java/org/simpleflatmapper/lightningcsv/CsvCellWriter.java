package org.simpleflatmapper.lightningcsv;


import java.io.IOException;
import java.util.Iterator;

public final class CsvCellWriter implements CellWriter {

    public static final CsvCellWriter DEFAULT_WRITER = new CsvCellWriter(',', '"', '"', false, "\r\n");

    private final boolean alwaysEscape;
    private final char separator;
    private final char quote;
    private final char escape;
    private final String endOfLine;
    private final char[] specialCharacters;

    public CsvCellWriter(char separator, char quote, char escape, boolean alwaysEscape, String endOfLine) {
        this.separator = separator;
        this.quote = quote;
        this.escape = escape;
        this.alwaysEscape = alwaysEscape;
        this.endOfLine = endOfLine;
        this.specialCharacters = (getSpecialCharacterForEndOfLine(endOfLine) + quote + separator).toCharArray();
    }

    private String getSpecialCharacterForEndOfLine(String endOfLine) {
        if ("\n".equals(endOfLine) || "\r".equals(endOfLine)) {
            return "\r\n";
        }
        return endOfLine;
    }

    public void writeRow(CharSequence[] values, Appendable appendable) throws IOException {
        if (values != null && values.length > 0) {
            writeValue(values[0], appendable);

            for(int i = 1; i < values.length; i++) {
                nextCell(appendable);
                writeValue(values[1], appendable);
            }
        }
        endOfRow(appendable);
    }

    public void writeRow(Iterable<? extends CharSequence> values, Appendable appendable) throws IOException {
        Iterator<? extends CharSequence> iterator = values.iterator();
        if (iterator.hasNext()) {
            writeValue(iterator.next(), appendable);

            while(iterator.hasNext()) {
                nextCell(appendable);
                writeValue(iterator.next(), appendable);
            }
        }
        endOfRow(appendable);
    }


    @Override
    public void writeValue(CharSequence sequence, Appendable appendable) throws IOException {
        if (alwaysEscape || needsEscaping(sequence)) {
            escapeCharSequence(sequence, appendable);
        } else {
            appendable.append(sequence);
        }
    }

    private boolean needsEscaping(CharSequence sequence) {
        char[] specialCharacters = this.specialCharacters;
        for(int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            for(int j = 0; j < specialCharacters.length; j++) {
                char s = specialCharacters[j];
                if (c == s) return true;
            }
        }
        return false;
    }

    private void escapeCharSequence(CharSequence sequence, Appendable appendable) throws IOException {
        char quote = this.quote;
        appendable.append(quote);
        for(int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (c == quote) {
                appendable.append(escape);
            }
            appendable.append(c);
        }
        appendable.append(quote);
    }

    @Override
    public void nextCell(Appendable target) throws IOException {
        target.append(separator);
    }

    @Override
    public void endOfRow(Appendable target) throws IOException {
        target.append(endOfLine);
    }

    public CsvCellWriter separator(char separator) {
        return new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine);
    }
    public CsvCellWriter quote(char quote) {
        return new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine);
    }
    public CsvCellWriter escape(char escape) {
        return new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine);
    }
    public CsvCellWriter alwaysEscape(boolean alwaysEscape) {
        return new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine);
    }

    public CsvCellWriter endOfLine(String endOfLine) {
        return new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine);
    }

    public CellWriter alwaysEscape() {
        return alwaysEscape(true);
    }

}
