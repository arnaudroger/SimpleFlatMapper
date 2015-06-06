package org.sfm.csv.impl.writer;


import java.io.IOException;

public class CsvCellWriter {

    public void writerInt(int i, Appendable appendable) throws IOException {
        writeCharSequence(Integer.toString(i), appendable);
    }

    public void writeCharSequence(CharSequence sequence, Appendable appendable) throws IOException {
        appendable.append(sequence);
    }

    public void nextCell(Appendable target) throws IOException {
        target.append(',');
    }

    public void endOfRow(Appendable target) throws IOException {
        target.append("\r\n");
    }
}
