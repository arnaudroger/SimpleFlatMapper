package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.ByteContextualSetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;

public class ByteAppendableSetter implements ByteContextualSetter<Appendable> {

    private final CellWriter cellWriter;

    public ByteAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setByte(Appendable target, byte value, Context context) throws Exception {
        cellWriter.writeValue(Byte.toString(value), target);
    }
}
