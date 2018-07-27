package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.reflect.primitive.LongSetter;

public class LongAppendableSetter implements LongSetter<Appendable> {

    private final CellWriter cellWriter;

    public LongAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setLong(Appendable target, long value) throws Exception {
        cellWriter.writeValue(Long.toString(value), target);
    }
}
