package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.LongContextualSetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;

public class LongAppendableSetter implements LongContextualSetter<Appendable> {

    private final CellWriter cellWriter;

    public LongAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setLong(Appendable target, long value, Context context) throws Exception {
        cellWriter.writeValue(Long.toString(value), target);
    }
}
