package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.FloatContextualSetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;

public class FloatAppendableSetter implements FloatContextualSetter<Appendable> {

    private final CellWriter cellWriter;

    public FloatAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setFloat(Appendable target, float value, Context context) throws Exception {
        cellWriter.writeValue(Float.toString(value), target);
    }
}
