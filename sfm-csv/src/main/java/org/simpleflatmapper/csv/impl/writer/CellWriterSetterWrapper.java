package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.reflect.Setter;

public class CellWriterSetterWrapper<P> implements Setter<Appendable, P> {
    private final Setter<Appendable, ? super P> setter;
    private final CellWriter cellWriter;

    public CellWriterSetterWrapper(CellWriter cellWriter, Setter<Appendable, ? super P> setter) {
        this.cellWriter = cellWriter;
        this.setter = setter;
    }

    @Override
    public void set(java.lang.Appendable target, P value) throws Exception {
        StringBuilder sb = new StringBuilder();
        setter.set(sb, value);
        cellWriter.writeValue(sb, target);
    }
}
