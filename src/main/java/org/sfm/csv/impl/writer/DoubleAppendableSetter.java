package org.sfm.csv.impl.writer;

import org.sfm.reflect.primitive.DoubleSetter;

public class DoubleAppendableSetter implements DoubleSetter<Appendable> {

    private final CsvCellWriter cellWriter;

    public DoubleAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setDouble(Appendable target, double value) throws Exception {
        cellWriter.writeDouble(value, target);
    }
}
