package org.sfm.csv.impl.writer;


import java.io.IOException;

public class CsvCellWriter {

    public void writeBoolean(boolean b, Appendable appendable) throws IOException {
        appendCharSequence(Boolean.toString(b), appendable);
    }

    public void writeByte(byte b, Appendable appendable) throws IOException {
        appendCharSequence(Byte.toString(b), appendable);
    }

    public void writeChar(char c, Appendable appendable) throws IOException {
        appendCharSequence(Character.toString(c), appendable);
    }

    public void writeShort(short s, Appendable appendable) throws IOException {
        appendCharSequence(Short.toString(s), appendable);
    }

    public void writeInt(int i, Appendable appendable) throws IOException {
        appendCharSequence(Integer.toString(i), appendable);
    }

    public void writeLong(long l, Appendable appendable) throws IOException {
        appendCharSequence(Long.toString(l), appendable);
    }

    public void writeFloat(float f, Appendable appendable) throws IOException {
        appendCharSequence(Float.toString(f), appendable);
    }

    public void writeDouble(double d, Appendable appendable) throws IOException {
        appendCharSequence(Double.toString(d), appendable);
    }

    public void writeCharSequence(CharSequence sequence, Appendable appendable) throws IOException {
        if (needsEscaping(sequence)) {
            escapeCharSequence(sequence, appendable);
        } else {
            appendCharSequence(sequence, appendable);
        }
    }

    private boolean needsEscaping(CharSequence sequence) {
        for(int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (c == '"' || c == ',' || c == '\r' || c == '\n') {
                return true;
            }
        }
        return false;
    }

    private void appendCharSequence(CharSequence sequence, Appendable appendable) throws IOException {
        appendable.append(sequence);
    }

    private void escapeCharSequence(CharSequence sequence, Appendable appendable) throws IOException {
        appendable.append('"');
        for(int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            appendable.append(c);
            if (c == '"') {
                appendable.append('"');
            }
        }
        appendable.append('"');
    }

    public void nextCell(Appendable target) throws IOException {
        target.append(',');
    }

    public void endOfRow(Appendable target) throws IOException {
        appendCharSequence("\r\n", target);
    }
}
