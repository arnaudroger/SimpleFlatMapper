package org.sfm.csv.impl.writer;

import org.sfm.csv.CellWriter;
import org.sfm.reflect.primitive.ByteSetter;

public class ByteAppendableSetter implements ByteSetter<Appendable> {

    private final CellWriter cellWriter;

    public ByteAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setByte(Appendable target, byte value) throws Exception {
        cellWriter.writeValue(Byte.toString(value), target);
    }
}
