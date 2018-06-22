package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

public class DoubleAppendableSetter implements DoubleSetter<Appendable> {

    private final CellWriter cellWriter;

    public DoubleAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setDouble(Appendable target, double value) throws Exception {
        cellWriter.writeValue(Double.toString(value), target);
    }
}
