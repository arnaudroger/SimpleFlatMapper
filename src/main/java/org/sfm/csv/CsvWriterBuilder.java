package org.sfm.csv;


import org.sfm.csv.impl.writer.*;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.EnumOrdinalFormatProperty;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.*;
import org.sfm.reflect.*;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.primitive.*;
import org.sfm.utils.ForEachCallBack;

import java.lang.reflect.Type;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvWriterBuilder<T> {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final Type target;
    private final ReflectionService reflectionService;
    private final MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig;

    private final PropertyMappingsBuilder<T, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>> propertyMappingsBuilder;

    private int currentIndex = 0;

    public CsvWriterBuilder(Type target,
                            ReflectionService reflectionService,
                            MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig) {
        this.target = target;
        this.reflectionService = reflectionService;
        this.mapperConfig = mapperConfig;
        ClassMeta<T> classMeta = reflectionService.getClassMeta(target);
        this.propertyMappingsBuilder =
                new PropertyMappingsBuilder<T, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>>(
                        classMeta,
                        mapperConfig.propertyNameMatcherFactory(),
                        mapperConfig.mapperBuilderErrorHandler());
    }

    public CsvWriterBuilder<T> addColumn(String column) {
        return addColumn(column, FieldMapperColumnDefinition.<CsvColumnKey, T>identity());

    }
    public CsvWriterBuilder<T> addColumn(String column,  FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition) {
        propertyMappingsBuilder.addProperty(new CsvColumnKey(column, currentIndex++), columnDefinition);
        return this;
    }
    public CsvWriterBuilder<T> addColumn(String column, ColumnProperty... properties) {
        propertyMappingsBuilder.addProperty(new CsvColumnKey(column, currentIndex++), FieldMapperColumnDefinition.<CsvColumnKey, T>identity().add(properties));
        return this;
    }


    @SuppressWarnings("unchecked")
    public Mapper<T, Appendable> mapper() {

        final List<FieldMapper<T, Appendable>> mappers = new ArrayList<FieldMapper<T, Appendable>>();

        final CsvCellWriter cellWriter = new CsvCellWriter();

        final CellSeparatorAppender<T> cellSeparatorAppender = new CellSeparatorAppender<T>(cellWriter);
        propertyMappingsBuilder.forEachProperties(
                new ForEachCallBack<PropertyMapping<T, ?, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>>>() {
                    private boolean first = true;
                    @Override
                    public void handle(PropertyMapping<T, ?, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>> pm) {
                        Getter<T, ?> getter = pm.getPropertyMeta().getGetter();
                        FieldMapper<T, Appendable> fieldMapper =
                                newFieldWriter(pm.getPropertyMeta().getPropertyType(), getter, cellWriter, pm.getColumnDefinition());
                        if (!first) {
                            mappers.add(cellSeparatorAppender);
                        }
                        mappers.add(fieldMapper);
                        first = false;
                    }
                }
        );
        mappers.add(new EndOfRowAppender<T>(cellWriter));

        return new MapperImpl<T, Appendable>(
                mappers.toArray(new FieldMapper[0]),
                new FieldMapper[0],
                STRING_BUILDER_INSTANTIATOR);
    }

    @SuppressWarnings("unchecked")
    protected <P> FieldMapper<T, Appendable> newFieldWriter(Type type,
                                                            Getter<T, P> getter,
                                                            CsvCellWriter cellWriter,
                                                            FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition) {
        if (TypeHelper.isPrimitive(type)) {
            if (getter instanceof BooleanGetter) {
                return new BooleanFieldMapper<T, Appendable>((BooleanGetter) getter, new BooleanAppendableSetter(cellWriter));
            } else if (getter instanceof ByteGetter) {
                return new ByteFieldMapper<T, Appendable>((ByteGetter)getter, new ByteAppendableSetter(cellWriter));
            } else if (getter instanceof CharacterGetter) {
                return new CharacterFieldMapper<T, Appendable>((CharacterGetter)getter, new CharacterAppendableSetter(cellWriter));
            } else if (getter instanceof ShortGetter) {
                return new ShortFieldMapper<T, Appendable>((ShortGetter)getter, new ShortAppendableSetter(cellWriter));
            } else if (getter instanceof IntGetter) {
                return new IntFieldMapper<T, Appendable>((IntGetter)getter, new IntegerAppendableSetter(cellWriter));
            } else if (getter instanceof LongGetter) {
                return new LongFieldMapper<T, Appendable>((LongGetter)getter, new LongAppendableSetter(cellWriter));
            } else if (getter instanceof FloatGetter) {
                return new FloatFieldMapper<T, Appendable>((FloatGetter)getter, new FloatAppendableSetter(cellWriter));
            } else if (getter instanceof DoubleGetter) {
                return new DoubleFieldMapper<T, Appendable>((DoubleGetter)getter, new DoubleAppendableSetter(cellWriter));
            }
        }
        Setter<Appendable, ? super P> setter = null;

        if (TypeHelper.isEnum(type) && columnDefinition.has(EnumOrdinalFormatProperty.class)) {
            setter = (Setter<Appendable, ? super P>) new EnumOrdinalAppendableSetter(cellWriter);
        }

        Format format = null;
        if (TypeHelper.areEquals(type, Date.class)) {
            String df = DEFAULT_DATE_FORMAT;

            DateFormatProperty dfp = columnDefinition.lookFor(DateFormatProperty.class);
            if (dfp != null) {
                df  = dfp.getPattern();
            }
            format = new SimpleDateFormat(df);
        }

        if (format != null) {
            final Format f = format;
            return new FormattingAppender<>(getter, new Getter<MappingContext<T>, Format>() {
                @Override
                public Format get(MappingContext<T> target) throws Exception {
                    return f;
                }
            });
        }

        if (setter == null) {
            setter = new ObjectAppendableSetter(cellWriter);
        }
        return new FieldMapperImpl<T, Appendable, P>(getter, setter);
    }

    private static final AppendableInstantiator STRING_BUILDER_INSTANTIATOR = new AppendableInstantiator();
    private static class AppendableInstantiator implements Instantiator<Object, Appendable> {
        @Override
        public Appendable newInstance(Object o) throws Exception {
            return new StringBuilder();
        }
    }
}
