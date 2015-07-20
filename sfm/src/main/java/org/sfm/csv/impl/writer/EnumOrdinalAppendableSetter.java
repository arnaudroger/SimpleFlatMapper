package org.sfm.csv.impl.writer;

import org.sfm.csv.CellWriter;
import org.sfm.reflect.Setter;

public class EnumOrdinalAppendableSetter  implements Setter<Appendable, Enum> {
    private final CellWriter cellWriter;

    public EnumOrdinalAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void set(Appendable target, Enum value) throws Exception {
        cellWriter.writeValue(Integer.toString(value.ordinal()), target);
    }
}
