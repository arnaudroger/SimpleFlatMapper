package org.sfm.csv;

import org.sfm.csv.impl.writer.*;

//IFJAVA8_START
import org.sfm.csv.impl.writer.time.JavaTimeFormattingAppender;
import org.sfm.map.column.time.JavaDateTimeFormatterProperty;
import java.time.temporal.TemporalAccessor;
//IFJAVA8_END
import org.sfm.map.FieldMapperToSourceFactory;
import org.sfm.map.column.joda.JodaDateTimeFormatterProperty;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.EnumOrdinalFormatProperty;
import org.sfm.map.column.FormatProperty;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.map.impl.fieldmapper.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.primitive.*;
import org.sfm.utils.Supplier;

import java.lang.reflect.Type;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FieldMapperToAppendableFactory implements FieldMapperToSourceFactory<Appendable, CsvColumnKey> {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final CellWriter cellWriter;

    public FieldMapperToAppendableFactory(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, P> FieldMapper<T, Appendable> newFieldMapperToSource(
            PropertyMapping<T, P, CsvColumnKey, ? extends ColumnDefinition<CsvColumnKey, ?>> pm,
            MappingContextFactoryBuilder builder) {
        if (pm == null) throw new NullPointerException("pm is null");

        Type type = pm.getPropertyMeta().getPropertyType();
        Getter<T, ? extends P> getter = pm.getPropertyMeta().getGetter();
        ColumnDefinition<CsvColumnKey, ?> columnDefinition = pm.getColumnDefinition();
        if (TypeHelper.isPrimitive(type) && !columnDefinition.has(FormatProperty.class)) {
            if (getter instanceof BooleanGetter) {
                return new BooleanFieldMapper<T, Appendable>((BooleanGetter) getter, new BooleanAppendableSetter(cellWriter));
            } else if (getter instanceof ByteGetter) {
                return new ByteFieldMapper<T, Appendable>((ByteGetter) getter, new ByteAppendableSetter(cellWriter));
            } else if (getter instanceof CharacterGetter) {
                return new CharacterFieldMapper<T, Appendable>((CharacterGetter) getter, new CharacterAppendableSetter(cellWriter));
            } else if (getter instanceof ShortGetter) {
                return new ShortFieldMapper<T, Appendable>((ShortGetter) getter, new ShortAppendableSetter(cellWriter));
            } else if (getter instanceof IntGetter) {
                return new IntFieldMapper<T, Appendable>((IntGetter) getter, new IntegerAppendableSetter(cellWriter));
            } else if (getter instanceof LongGetter) {
                return new LongFieldMapper<T, Appendable>((LongGetter) getter, new LongAppendableSetter(cellWriter));
            } else if (getter instanceof FloatGetter) {
                return new FloatFieldMapper<T, Appendable>((FloatGetter) getter, new FloatAppendableSetter(cellWriter));
            } else if (getter instanceof DoubleGetter) {
                return new DoubleFieldMapper<T, Appendable>((DoubleGetter) getter, new DoubleAppendableSetter(cellWriter));
            }
        }
        Setter<Appendable, ? super P> setter = null;

        if (TypeHelper.isEnum(type) && columnDefinition.has(EnumOrdinalFormatProperty.class)) {
            setter = (Setter) new EnumOrdinalAppendableSetter(cellWriter);
        }

        Format format = null;

        if (columnDefinition.has(FormatProperty.class)) {
            format = columnDefinition.lookFor(FormatProperty.class).format();
        } else if (TypeHelper.areEquals(type, Date.class)) {
            String df = DEFAULT_DATE_FORMAT;

            DateFormatProperty dfp = columnDefinition.lookFor(DateFormatProperty.class);
            if (dfp != null) {
                df = dfp.getPattern();
            }
            format = new SimpleDateFormat(df);
        }
        //IFJAVA8_START
        else if (TypeHelper.isAssignable(TemporalAccessor.class, type)
                && columnDefinition.has(JavaDateTimeFormatterProperty.class)) {
            return new JavaTimeFormattingAppender<T>((Getter<T, ? extends TemporalAccessor>) getter, columnDefinition.lookFor(JavaDateTimeFormatterProperty.class).getFormatter(), cellWriter);
        }
        //IFJAVA8_END
        else if (JodaTimeClasses.isJoda(type)) {
            if (columnDefinition.has(JodaDateTimeFormatterProperty.class)) {
                return new JodaTimeFormattingAppender<T>(getter, columnDefinition.lookFor(JodaDateTimeFormatterProperty.class).getFormatter(), cellWriter);
            } else if (columnDefinition.has(DateFormatProperty.class)) {
                return new JodaTimeFormattingAppender<T>(getter, columnDefinition.lookFor(DateFormatProperty.class).getPattern(), cellWriter);
            }
        }

        if (format != null) {
            final Format f = format;
            builder.addSupplier(pm.getColumnKey().getIndex(), new Supplier<Format>() {
                @Override
                public Format get() {
                    return (Format) f.clone();
                }
            });
            return new FormattingAppender<T>(getter, new MappingContextFormatGetter<T>(pm.getColumnKey().getIndex()), cellWriter);
        }

        if (setter == null) {
            setter = new ObjectAppendableSetter(cellWriter);
        }

        return new FieldMapperImpl<T, Appendable, P>(getter, setter);
    }


    private static class MappingContextFormatGetter<T> implements Getter<MappingContext<? super T>, Format> {
        private final int index;

        public MappingContextFormatGetter(int index) {
            this.index = index;
        }

        @Override
        public Format get(MappingContext<? super T> target) throws Exception {
            return target.context(index);
        }
    }
}