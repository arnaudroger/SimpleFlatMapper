package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public class BooleanAppendableSetter implements BooleanSetter<Appendable> {

    private final CellWriter cellWriter;

    public BooleanAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setBoolean(Appendable target, boolean value) throws Exception {
        cellWriter.writeValue(Boolean.toString(value), target);
    }
}
