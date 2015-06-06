package org.sfm.csv.impl.writer;

import org.sfm.reflect.primitive.FloatSetter;

public class FloatAppendableSetter implements FloatSetter<Appendable> {

    private final CsvCellWriter cellWriter;

    public FloatAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setFloat(Appendable target, float value) throws Exception {
        target.append(Float.toString(value));
    }
}
