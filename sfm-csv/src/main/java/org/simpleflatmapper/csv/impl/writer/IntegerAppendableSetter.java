package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.reflect.primitive.IntSetter;

public class IntegerAppendableSetter implements IntSetter<Appendable> {

    private final CellWriter cellWriter;

    public IntegerAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setInt(Appendable target, int value) throws Exception {
        cellWriter.writeValue(Integer.toString(value), target);
    }
}
