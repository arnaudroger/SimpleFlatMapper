package org.sfm.csv.impl.writer;


import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.Getter;

import java.text.Format;

import static org.sfm.utils.Asserts.requireNonNull;

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
