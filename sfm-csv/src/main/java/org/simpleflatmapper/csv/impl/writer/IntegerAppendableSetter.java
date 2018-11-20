package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.IntContextualSetter;

public class IntegerAppendableSetter implements IntContextualSetter<Appendable> {

    private final CellWriter cellWriter;

    public IntegerAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setInt(Appendable target, int value, Context context) throws Exception {
        cellWriter.writeValue(Integer.toString(value), target);
    }
}
