package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.BooleanContextualSetter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;

public class BooleanAppendableSetter implements BooleanContextualSetter<Appendable> {

    private final CellWriter cellWriter;

    public BooleanAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setBoolean(Appendable target, boolean value, Context context) throws Exception {
        cellWriter.writeValue(Boolean.toString(value), target);
    }
}
