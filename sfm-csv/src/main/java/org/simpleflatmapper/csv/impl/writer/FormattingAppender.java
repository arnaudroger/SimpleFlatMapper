package org.simpleflatmapper.csv.impl.writer;


import org.simpleflatmapper.csv.CellWriter;
import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.reflect.Getter;

import java.text.Format;

import static org.simpleflatmapper.core.utils.Asserts.requireNonNull;

public class FormattingAppender<S> implements FieldMapper<S, Appendable> {

    private final Getter<S, ?> getter;
    private final Getter<MappingContext<? super S>, Format> formatAccessor;
    private final CellWriter cellWriter;

    public FormattingAppender(Getter<S, ?> getter, Getter<MappingContext<? super S>, Format> formatAccessor, CellWriter cellWriter) {
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
