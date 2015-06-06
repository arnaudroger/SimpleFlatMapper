package org.sfm.csv.impl.writer;

import org.sfm.reflect.primitive.ShortSetter;

public class ShortAppendableSetter implements ShortSetter<Appendable> {

    private final CsvCellWriter cellWriter;

    public ShortAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setShort(Appendable target, short value) throws Exception {
        target.append(Short.toString(value));
    }
}
