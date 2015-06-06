package org.sfm.csv.impl.writer;

import org.sfm.reflect.Setter;

public class EnumOrdinalAppendableSetter  implements Setter<Appendable, Enum> {
    private final CsvCellWriter cellWriter;

    public EnumOrdinalAppendableSetter(CsvCellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void set(Appendable target, Enum value) throws Exception {
        cellWriter.writerInt(value.ordinal(), target);
    }
}
