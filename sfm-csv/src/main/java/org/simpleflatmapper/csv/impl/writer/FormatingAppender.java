package org.simpleflatmapper.csv.impl.writer;


import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.Getter;

import java.text.Format;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class FormatingAppender<S> implements FieldMapper<S, Appendable> {

    private final Getter<? super S, ?> getter;
    private final Getter<MappingContext<? super S>, Format> formatAccessor;
    private final CellWriter cellWriter;

    public FormatingAppender(Getter<? super S, ?> getter, Getter<MappingContext<? super S>, Format> formatAccessor, CellWriter cellWriter) {
        this.getter = getter;
        this.formatAccessor = formatAccessor;
        this.cellWriter = cellWriter;
    }

    @Override
    public void mapTo(S source, Appendable target, MappingContext<? super S> context) throws Exception {
        Object o = getter.get(source);
        if (o != null) {
            Format format = formatAccessor.get(context);
            requireNonNull("Format in mapping context", format);
            cellWriter.writeValue(format.format(o), target);
        }
    }
}
