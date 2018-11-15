package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.impl.writer.*;

import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.property.SetterFactoryProperty;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.map.fieldmapper.*;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.property.DateFormatProperty;
import org.simpleflatmapper.map.property.EnumOrdinalFormatProperty;
import org.simpleflatmapper.map.property.FormatProperty;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactory;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.map.setter.ContextualSetterAdapter;
import org.simpleflatmapper.map.setter.ContextualSetterOnGetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
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

import java.lang.reflect.Type;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FieldMapperToAppendableFactory implements ConstantTargetFieldMapperFactory<Appendable, CsvColumnKey> {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final CellWriter cellWriter;
    private final ConverterService converterService = ConverterService.getInstance();

    public FieldMapperToAppendableFactory(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, P> FieldMapper<S, Appendable> newFieldMapper(PropertyMapping<S, P, CsvColumnKey> pm, MappingContextFactoryBuilder builder, MapperBuilderErrorHandler mappingErrorHandler) {
        if (pm == null) throw new NullPointerException("pm is null");

        Getter<? super S, ? extends P> getter;

        Getter<?, ?> customGetter = pm.getColumnDefinition().getCustomGetterFrom(pm.getPropertyMeta().getOwnerType());
        if (customGetter != null) {
            getter = (Getter<S, P>) customGetter;
        } else {
            getter = pm.getPropertyMeta().getGetter();
        }


        ColumnDefinition<CsvColumnKey, ?> columnDefinition = pm.getColumnDefinition();
        
        if (columnDefinition == null) throw new NullPointerException("Unexpected null columnDefinition");
        
        Type type = pm.getPropertyMeta().getPropertyType();
        if (TypeHelper.isPrimitive(type) && !columnDefinition.has(FormatProperty.class)) {
            if (getter instanceof BooleanGetter) {
                return new BooleanConstantTargetFieldMapper<S, Appendable>((BooleanGetter<? super S>) getter, new BooleanAppendableSetter(cellWriter));
            } else if (getter instanceof ByteGetter) {
                return new ByteConstantTargetFieldMapper<S, Appendable>((ByteGetter<? super S>) getter, new ByteAppendableSetter(cellWriter));
            } else if (getter instanceof CharacterGetter) {
                return new CharacterConstantTargetFieldMapper<S, Appendable>((CharacterGetter<? super S>) getter, new CharacterAppendableSetter(cellWriter));
            } else if (getter instanceof ShortGetter) {
                return new ShortConstantTargetFieldMapper<S, Appendable>((ShortGetter<? super S>) getter, new ShortAppendableSetter(cellWriter));
            } else if (getter instanceof IntGetter) {
                return new IntConstantTargetFieldMapper<S, Appendable>((IntGetter<? super S>) getter, new IntegerAppendableSetter(cellWriter));
            } else if (getter instanceof LongGetter) {
                return new LongConstantTargetFieldMapper<S, Appendable>((LongGetter<? super S>) getter, new LongAppendableSetter(cellWriter));
            } else if (getter instanceof FloatGetter) {
                return new FloatConstantTargetFieldMapper<S, Appendable>((FloatGetter<? super S>) getter, new FloatAppendableSetter(cellWriter));
            } else if (getter instanceof DoubleGetter) {
                return new DoubleConstantTargetFieldMapper<S, Appendable>((DoubleGetter<? super S>) getter, new DoubleAppendableSetter(cellWriter));
            }
        }

        ContextualSetter<Appendable, ? super P> setter = null;

        if (TypeHelper.isEnum(type) && columnDefinition.has(EnumOrdinalFormatProperty.class)) {
            setter = (ContextualSetter<Appendable, ? super P>) new EnumOrdinalAppendableSetter(cellWriter);
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

        if (format != null) {
            final Format f = format;
            int  i = builder.addSupplier(new CloneFormatSupplier(f));
            return new FormatingAppender<S>(getter, new MappingContextFormatGetter<S>(i), cellWriter);
        }


        if (setter == null) {
            setter = getSetter(pm, cellWriter);
        }

        if (setter == null) {
            ContextualConverter<? super P, ? extends CharSequence> converter =
                    converterService.findConverter(
                            pm.getPropertyMeta().getPropertyType(),
                            CharSequence.class,
                            builder,
                            columnDefinition.properties());

            if (converter != null) {
                return new ConvertingAppender<S, P>(getter, converter, cellWriter);
            }
        }

        return new ConstantTargetFieldMapper<S, Appendable, P>(getter, setter);
    }

    @SuppressWarnings("unchecked")
    private <S, P> ContextualSetter<Appendable, ? super P> getSetter(PropertyMapping<S, P, CsvColumnKey> pm, CellWriter cellWriter) {

        final SetterProperty setterProperty = pm.getColumnDefinition().lookFor(SetterProperty.class);

        if (setterProperty != null) {
            return new CellWriterSetterWrapper<P>(cellWriter, ContextualSetterAdapter.of ((Setter<Appendable, P>) setterProperty.getSetter()));
        }

        ContextualSetter<Appendable, ? super P> setter = setterFromFactory(pm);

        if (setter != null) {
            return new CellWriterSetterWrapper<P>(cellWriter, setter);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <S, P> ContextualSetter<Appendable, ? super P> setterFromFactory(PropertyMapping<S, P, CsvColumnKey> pm) {
        ContextualSetter<Appendable, ? super P> setter = null;

        final SetterFactoryProperty setterFactoryProperty = pm.getColumnDefinition().lookFor(SetterFactoryProperty.class);
        if (setterFactoryProperty != null) {
            final SetterFactory<Appendable, PropertyMapping<S, P, CsvColumnKey>> setterFactory =
                    (SetterFactory<Appendable, PropertyMapping<S, P, CsvColumnKey>>) setterFactoryProperty.getSetterFactory();
            setter = ContextualSetterAdapter.of(setterFactory.getSetter(pm));
        }


        if (setter == null) {
            if (!pm.getPropertyMeta().isSelf()) {
                final ClassMeta<P> classMeta = pm.getPropertyMeta().getPropertyClassMeta();
                if (classMeta instanceof ObjectClassMeta) {
                    ObjectClassMeta<P> ocm = (ObjectClassMeta<P>) classMeta;
                    if (ocm.getNumberOfProperties() == 1) {
                        PropertyMeta<P, ?> subProp = ocm.getFirstProperty();

                        ContextualSetter<Appendable, Object> subSetter = (ContextualSetter<Appendable, Object>) setterFromFactory(pm.propertyMeta(subProp));

                        if (subSetter != null) {
                            setter = new ContextualSetterOnGetter<Appendable, Object, P>(subSetter, (Getter<P, Object>) subProp.getGetter());
                        } else {
                            return new ObjectToStringSetter<P>(subProp.getGetter());
                        }

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