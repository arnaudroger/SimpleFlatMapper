package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.CellWriter;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.impl.writer.*;

import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.column.SetterFactoryProperty;
import org.simpleflatmapper.map.column.SetterProperty;
//IFJAVA8_START
import org.simpleflatmapper.csv.impl.writer.time.JavaTimeFormattingAppender;
import org.simpleflatmapper.map.column.time.JavaDateTimeFormatterProperty;
import java.time.temporal.TemporalAccessor;
//IFJAVA8_END
import org.simpleflatmapper.map.column.joda.JodaDateTimeFormatterProperty;
import org.simpleflatmapper.map.fieldmapper.BooleanFieldMapper;
import org.simpleflatmapper.map.fieldmapper.ByteFieldMapper;
import org.simpleflatmapper.map.fieldmapper.CharacterFieldMapper;
import org.simpleflatmapper.map.fieldmapper.DoubleFieldMapper;
import org.simpleflatmapper.map.fieldmapper.FieldMapperImpl;
import org.simpleflatmapper.map.fieldmapper.FloatFieldMapper;
import org.simpleflatmapper.map.fieldmapper.IntFieldMapper;
import org.simpleflatmapper.map.fieldmapper.LongFieldMapper;
import org.simpleflatmapper.map.fieldmapper.ShortFieldMapper;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.column.DateFormatProperty;
import org.simpleflatmapper.map.column.EnumOrdinalFormatProperty;
import org.simpleflatmapper.map.column.FormatProperty;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactory;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.SetterOnGetter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.date.joda.JodaTimeHelper;

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
                df = dfp.get();
            }
            format = new SimpleDateFormat(df);
        }
        //IFJAVA8_START
        else if (TypeHelper.isAssignable(TemporalAccessor.class, type)
                && columnDefinition.has(JavaDateTimeFormatterProperty.class)) {
            return new JavaTimeFormattingAppender<S>((Getter<S, ? extends TemporalAccessor>) getter, columnDefinition.lookFor(JavaDateTimeFormatterProperty.class).get(), cellWriter);
        }
        //IFJAVA8_END
        else if (JodaTimeHelper.isJoda(type)) {
            if (columnDefinition.has(JodaDateTimeFormatterProperty.class)) {
                return new JodaTimeFormattingAppender<S>(getter, columnDefinition.lookFor(JodaDateTimeFormatterProperty.class).get(), cellWriter);
            } else if (columnDefinition.has(DateFormatProperty.class)) {
                return new JodaTimeFormattingAppender<S>(getter, columnDefinition.lookFor(DateFormatProperty.class).get(), cellWriter);
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