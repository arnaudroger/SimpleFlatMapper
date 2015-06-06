package org.sfm.csv.impl.writer;

import org.sfm.reflect.primitive.IntSetter;

public class IntegerAppendableSetter implements IntSetter<Appendable> {

    private final CsvCellWriter cellWriter;

    public IntegerAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setInt(Appendable target, int value) throws Exception {
        target.append(Integer.toString(value));
    }
}
