package org.sfm.csv.impl.writer;

import org.sfm.reflect.Setter;

public class ObjectAppendableSetter implements Setter<Appendable, Object> {

    private final CsvCellWriter cellWriter;

    public ObjectAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void set(Appendable target, Object value) throws Exception {
        if (value != null) {
            cellWriter.writeCharSequence(String.valueOf(value), target);
        }
    }
}
