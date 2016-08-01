package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.CellWriter;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.impl.writer.*;

import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.column.SetterFactoryProperty;
import org.sfm.map.column.SetterProperty;
//IFJAVA8_START
import org.simpleflatmapper.csv.impl.writer.time.JavaTimeFormattingAppender;
import org.sfm.map.column.time.JavaDateTimeFormatterProperty;
import java.time.temporal.TemporalAccessor;
//IFJAVA8_END
import org.sfm.map.column.joda.JodaDateTimeFormatterProperty;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContext;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.EnumOrdinalFormatProperty;
import org.sfm.map.column.FormatProperty;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactory;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.map.impl.fieldmapper.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.SetterOnGetter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ObjectClassMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.primitive.*;
import org.sfm.utils.Supplier;

import java.lang.reflect.Type;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FieldMapperToAppendableFactory implements ConstantTargetFieldMapperFactory<Appendable, CsvColumnKey> {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final CellWriter cellWriter;

    public FieldMapperToAppendableFactory(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, P> FieldMapper<S, Appendable> newFieldMapper(PropertyMapping<S, P, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> pm, MappingContextFactoryBuilder builder, MapperBuilderErrorHandler mappingErrorHandler) {
        if (pm == null) throw new NullPointerException("pm is null");

        Getter<S, ? extends P> getter;

        Getter<?, ?> customGetter = pm.getColumnDefinition().getCustomGetter();
        if (customGetter != null) {
            getter = (Getter<S, P>) customGetter;
        } else {
            getter = pm.getPropertyMeta().getGetter();
        }


        ColumnDefinition<CsvColumnKey, ?> columnDefinition = pm.getColumnDefinition();
        Type type = pm.getPropertyMeta().getPropertyType();
        if (TypeHelper.isPrimitive(type) && !columnDefinition.has(FormatProperty.class)) {
            if (getter instanceof BooleanGetter) {
                return new BooleanFieldMapper<S, Appendable>((BooleanGetter) getter, new BooleanAppendableSetter(cellWriter));
            } else if (getter instanceof ByteGetter) {
                return new ByteFieldMapper<S, Appendable>((ByteGetter) getter, new ByteAppendableSetter(cellWriter));
            } else if (getter instanceof CharacterGetter) {
                return new CharacterFieldMapper<S, Appendable>((CharacterGetter) getter, new CharacterAppendableSetter(cellWriter));
            } else if (getter instanceof ShortGetter) {
                return new ShortFieldMapper<S, Appendable>((ShortGetter) getter, new ShortAppendableSetter(cellWriter));
            } else if (getter instanceof IntGetter) {
                return new IntFieldMapper<S, Appendable>((IntGetter) getter, new IntegerAppendableSetter(cellWriter));
            } else if (getter instanceof LongGetter) {
                return new LongFieldMapper<S, Appendable>((LongGetter) getter, new LongAppendableSetter(cellWriter));
            } else if (getter instanceof FloatGetter) {
                return new FloatFieldMapper<S, Appendable>((FloatGetter) getter, new FloatAppendableSetter(cellWriter));
            } else if (getter instanceof DoubleGetter) {
                return new DoubleFieldMapper<S, Appendable>((DoubleGetter) getter, new DoubleAppendableSetter(cellWriter));
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
            return new JavaTimeFormattingAppender<S>((Getter<S, ? extends TemporalAccessor>) getter, columnDefinition.lookFor(JavaDateTimeFormatterProperty.class).getFormatter(), cellWriter);
        }
        //IFJAVA8_END
        else if (JodaTimeClasses.isJoda(type)) {
            if (columnDefinition.has(JodaDateTimeFormatterProperty.class)) {
                return new JodaTimeFormattingAppender<S>(getter, columnDefinition.lookFor(JodaDateTimeFormatterProperty.class).getFormatter(), cellWriter);
            } else if (columnDefinition.has(DateFormatProperty.class)) {
                return new JodaTimeFormattingAppender<S>(getter, columnDefinition.lookFor(DateFormatProperty.class).getPattern(), cellWriter);
            }
        }

        if (format != null) {
            final Format f = format;
            builder.addSupplier(pm.getColumnKey().getIndex(), new CloneFormatSupplier(f));
            return new FormattingAppender<S>(getter, new MappingContextFormatGetter<S>(pm.getColumnKey().getIndex()), cellWriter);
        }

        if (setter == null) {
            setter = getSetter(pm, cellWriter);
        }

        return new FieldMapperImpl<S, Appendable, P>(getter, setter);
    }

    private <S, P> Setter<Appendable, ? super P> getSetter(PropertyMapping<S, P, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> pm, CellWriter cellWriter) {

        final SetterProperty setterProperty = pm.getColumnDefinition().lookFor(SetterProperty.class);

        if (setterProperty != null) {
            return new CellWriterSetterWrapper(cellWriter, setterProperty.getSetter());
        }

        Setter<Appendable, ?> setter = setterFromFactory(pm);

        if (setter != null) {
            return new CellWriterSetterWrapper(cellWriter, setter);
        } else {
            return new ObjectAppendableSetter(cellWriter);
        }
    }

    private <S, P> Setter<Appendable, ?> setterFromFactory(PropertyMapping<S, P, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> pm) {
        Setter<Appendable, ?> setter = null;

        final SetterFactoryProperty setterFactoryProperty = pm.getColumnDefinition().lookFor(SetterFactoryProperty.class);
        if (setterFactoryProperty != null) {
            final SetterFactory<Appendable, PropertyMapping<S, P, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>>> setterFactory =
                    (SetterFactory<Appendable, PropertyMapping<S, P, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>>>) setterFactoryProperty.getSetterFactory();
            setter = (Setter<Appendable, ?>) setterFactory.getSetter(pm);
        }


        if (setter == null) {
            final ClassMeta<P> classMeta = pm.getPropertyMeta().getPropertyClassMeta();
            if (classMeta instanceof ObjectClassMeta) {
                ObjectClassMeta<P> ocm = (ObjectClassMeta<P>) classMeta;
                if (ocm.getNumberOfProperties() == 1) {
                    PropertyMeta<P, ?> subProp = ocm.getFirstProperty();

                    Setter<Appendable, Object> subSetter = (Setter<Appendable, Object>) setterFromFactory(pm.propertyMeta(subProp));

                    if (subSetter != null) {
                        setter = new SetterOnGetter<Appendable, Object, P>(subSetter, (Getter<P, Object>) subProp.getGetter());
                    } else {
                        return new ObjectToStringSetter<P>(subProp.getGetter());
                    }

                }
            }
        }
        return setter;
    }


    private static class MappingContextFormatGetter<S> implements Getter<MappingContext<? super S>, Format> {
        private final int index;

        public MappingContextFormatGetter(int index) {
            this.index = index;
        }

        @Override
        public Format get(MappingContext<? super S> target) throws Exception {
            return target.context(index);
        }
    }

    private static class CloneFormatSupplier implements Supplier<Format> {
        private final Format f;

        public CloneFormatSupplier(Format f) {
            this.f = f;
        }

        @Override
        public Format get() {
            return (Format) f.clone();
        }
    }
}