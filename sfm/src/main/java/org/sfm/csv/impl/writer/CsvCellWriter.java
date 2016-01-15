package org.sfm.csv.impl.writer;


import org.sfm.csv.CellWriter;

import java.io.IOException;

public final class CsvCellWriter implements CellWriter {

    public static final CsvCellWriter DEFAULT_WRITER = new CsvCellWriter(',', '"', false, "\r\n");

    private final boolean alwaysEscape;
    private final char separator;
    private final char quote;
    private final String endOfLine;
    private final char[] specialCharacters;

    public CsvCellWriter(char separator, char quote, boolean alwaysEscape, String endOfLine) {
        this.separator = separator;
        this.quote = quote;
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
            appendable.append(c);
            if (c == quote) {
                appendable.append(quote);
            }
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
        return new CsvCellWriter(separator, quote, alwaysEscape, endOfLine);
    }
    public CsvCellWriter quote(char quote) {
        return new CsvCellWriter(separator, quote, alwaysEscape, endOfLine);
    }

    public CsvCellWriter alwaysEscape(boolean alwaysEscape) {
        return new CsvCellWriter(separator, quote, alwaysEscape, endOfLine);
    }

    public CsvCellWriter endOfLine(String endOfLine) {
        return new CsvCellWriter(separator, quote, alwaysEscape, endOfLine);
    }

    public CellWriter alwaysEscape() {
        return alwaysEscape(true);
    }

}
