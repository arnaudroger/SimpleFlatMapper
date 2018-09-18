package org.simpleflatmapper.csv.impl.writer;


import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.Getter;

public class ConvertingAppender<S, P> implements FieldMapper<S, Appendable> {

    private final Getter<? super S, ? extends P> getter;
    private final ContextualConverter<? super P, ? extends  CharSequence> converter;
    private final CellWriter cellWriter;

    public ConvertingAppender(Getter<? super S, ? extends P> getter, ContextualConverter<? super P, ? extends  CharSequence> converter, CellWriter cellWriter) {
        this.getter = getter;
        this.converter = converter;
        this.cellWriter = cellWriter;
    }

    @Override
    public void mapTo(S source, Appendable target, MappingContext<? super S> context) throws Exception {
        P o = getter.get(source);
        if (o != null) {
            cellWriter.writeValue(converter.convert(o, context), target);
        }
    }
}
