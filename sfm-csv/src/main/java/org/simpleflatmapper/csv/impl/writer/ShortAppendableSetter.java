package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.ShortContextualSetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

public class ShortAppendableSetter implements ShortContextualSetter<Appendable> {

    private final CellWriter cellWriter;

    public ShortAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setShort(Appendable target, short value, Context context) throws Exception {
        cellWriter.writeValue(Short.toString(value), target);
    }
}
