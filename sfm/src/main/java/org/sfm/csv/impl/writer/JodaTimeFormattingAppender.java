package org.sfm.csv.impl.writer;

import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.csv.CellWriter;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.reflect.Getter;


public class JodaTimeFormattingAppender<T> implements FieldMapper<T, Appendable> {
    private final CellWriter cellWriter;
    private final DateTimeFormatter formatter;
    private final Getter<T, ? extends ReadableInstant> getter;

    @SuppressWarnings("unchecked")
    public JodaTimeFormattingAppender(Getter<T, ?> getter, DateTimeFormatter formatter, CellWriter cellWriter) {
        this.getter = (Getter<T, ? extends ReadableInstant>)getter;
        this.formatter = formatter;
        this.cellWriter = cellWriter;
    }

    @SuppressWarnings("unchecked")
    public JodaTimeFormattingAppender(Getter<T, ?> getter, String pattern, CellWriter cellWriter) {
        this.getter = (Getter<T, ? extends ReadableInstant>)getter;
        this.formatter = DateTimeFormat.forPattern(pattern);
        this.cellWriter = cellWriter;
    }

    @Override
    public void mapTo(T source, Appendable target, MappingContext<? super T> context) throws Exception {
        cellWriter.writeValue(formatter.print(getter.get(source)), target);
    }
}
