package org.sfm.csv.impl.writer;

import org.sfm.reflect.primitive.ByteSetter;

public class ByteAppendableSetter implements ByteSetter<Appendable> {

    private final CsvCellWriter cellWriter;

    public ByteAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setByte(Appendable target, byte value) throws Exception {
        cellWriter.writeByte(value, target);
    }
}
