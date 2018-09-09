package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.DoubleContextualSetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;

public class DoubleAppendableSetter implements DoubleContextualSetter<Appendable> {

    private final CellWriter cellWriter;

    public DoubleAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setDouble(Appendable target, double value, Context context) throws Exception {
        cellWriter.writeValue(Double.toString(value), target);
    }
}
