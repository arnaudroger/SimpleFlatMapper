package org.sfm.csv.impl.writer;


import java.io.IOException;

public class CsvCellWriter implements CellWriter {

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
        this.specialCharacters = (endOfLine + quote + separator).toCharArray();
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
}
