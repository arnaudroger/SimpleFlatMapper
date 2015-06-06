package org.sfm.csv;


import org.sfm.map.FieldMapper;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ForEachCallBack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CsvWriterBuilder<T> {

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

    @SuppressWarnings("unchecked")
    public FieldMapper<T, Appendable> mapper() {

        final List<FieldMapper<T, Appendable>> mappers = new ArrayList<FieldMapper<T, Appendable>>();

        propertyMappingsBuilder.forEachProperties(
                new ForEachCallBack<PropertyMapping<T, ?, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>>>() {
                    private boolean first = true;
                    @Override
                    public void handle(PropertyMapping<T, ?, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>> pm) {
                        FieldMapperImpl<T, Appendable, Object> fieldMapper =
                                new FieldMapperImpl<T, Appendable, Object>(
                                        pm.getPropertyMeta().getGetter(),
                                        new AppendableSetter(first));
                        mappers.add(fieldMapper);
                        first = false;
                    }
                }
        );

        return new MapperImpl<T, Appendable>(
                mappers.toArray(new FieldMapper[0]),
                new FieldMapper[0],
                new Instantiator<T, Appendable>() {
            @Override
            public Appendable newInstance(T o) throws Exception {
                throw new UnsupportedOperationException();
            }
        });
    }

    private static class AppendableSetter implements Setter<Appendable, Object> {
        private final boolean first;

        private AppendableSetter(boolean first) {
            this.first = first;
        }

        @Override
        public void set(Appendable target, Object value) throws Exception {
            if (!first) target.append(',');
            target.append(String.valueOf(value));
        }
    }
}
