package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.reflect.Setter;

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
