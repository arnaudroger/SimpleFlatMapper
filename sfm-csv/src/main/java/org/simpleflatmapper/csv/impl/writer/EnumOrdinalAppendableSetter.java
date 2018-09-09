package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.reflect.Setter;

public class EnumOrdinalAppendableSetter  implements ContextualSetter<Appendable, Enum> {
    private final CellWriter cellWriter;

    public EnumOrdinalAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void set(Appendable target, Enum value, Context context) throws Exception {
        cellWriter.writeValue(Integer.toString(value.ordinal()), target);
    }
}
