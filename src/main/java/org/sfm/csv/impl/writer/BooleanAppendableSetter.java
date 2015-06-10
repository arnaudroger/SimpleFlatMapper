package org.sfm.csv.impl.writer;

import org.sfm.reflect.primitive.BooleanSetter;

public class BooleanAppendableSetter implements BooleanSetter<Appendable> {

    private final CsvCellWriter cellWriter;

    public BooleanAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setBoolean(Appendable target, boolean value) throws Exception {
        cellWriter.writeBoolean(value, target);
    }
}
