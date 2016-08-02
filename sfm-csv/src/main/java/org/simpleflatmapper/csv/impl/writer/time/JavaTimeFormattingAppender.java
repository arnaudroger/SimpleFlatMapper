package org.simpleflatmapper.csv.impl.writer.time;

import org.simpleflatmapper.csv.CellWriter;
import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.reflect.Getter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class JavaTimeFormattingAppender<T> implements FieldMapper<T, Appendable> {
    private final DateTimeFormatter formatter;
    private final Getter<T, ? extends TemporalAccessor> getter;
    private final CellWriter cellWriter;

    public JavaTimeFormattingAppender(Getter<T, ? extends TemporalAccessor> getter, DateTimeFormatter formatter, CellWriter cellWriter) {
        this.getter = getter;
        this.formatter = formatter;
        this.cellWriter = cellWriter;
    }

    @Override
    public void mapTo(T source, Appendable target, MappingContext<? super T> context) throws Exception {
        cellWriter.writeValue(formatter.format(getter.get(source)), target);
    }
}
