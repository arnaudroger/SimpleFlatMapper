package org.sfm.csv.impl.writer;

import org.sfm.csv.CellWriter;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatAppendableSetter implements FloatSetter<Appendable> {

    private final CellWriter cellWriter;

    public FloatAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setFloat(Appendable target, float value) throws Exception {
        cellWriter.writeValue(Float.toString(value), target);
    }
}
