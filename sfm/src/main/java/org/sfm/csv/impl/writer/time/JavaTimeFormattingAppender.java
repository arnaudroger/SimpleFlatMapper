package org.sfm.csv.impl.writer.time;

import org.sfm.csv.CellWriter;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.Getter;

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
