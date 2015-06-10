package org.sfm.csv.impl.writer;

import org.sfm.reflect.primitive.LongSetter;

public class LongAppendableSetter implements LongSetter<Appendable> {

    private final CsvCellWriter cellWriter;

    public LongAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setLong(Appendable target, long value) throws Exception {
        cellWriter.writeLong(value, target);
    }
}
