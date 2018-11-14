package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.setter.ContextualSetter;

public class CellWriterSetterWrapper<P> implements ContextualSetter<Appendable, P> {
    private final ContextualSetter<Appendable, ? super P> setter;
    private final CellWriter cellWriter;

    public CellWriterSetterWrapper(CellWriter cellWriter, ContextualSetter<Appendable, ? super P> setter) {
        this.cellWriter = cellWriter;
        this.setter = setter;
    }

    @Override
    public void set(java.lang.Appendable target, P value, Context context) throws Exception {
        StringBuilder sb = new StringBuilder();
        setter.set(sb, value, context);
        cellWriter.writeValue(sb, target);
    }
}
