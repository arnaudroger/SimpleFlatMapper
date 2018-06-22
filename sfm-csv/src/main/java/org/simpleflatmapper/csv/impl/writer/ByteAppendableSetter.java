package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

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
