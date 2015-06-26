package org.sfm.csv;


import org.sfm.csv.impl.writer.DefaultFieldAppenderFactory;
import org.sfm.csv.impl.writer.*;
import org.sfm.map.FieldMapper;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.impl.*;
import org.sfm.map.impl.context.KeySourceGetter;
import org.sfm.map.impl.context.MappingContextFactoryBuilder;
import org.sfm.reflect.*;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ForEachCallBack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsvWriterBuilder<T> {

    private final ReflectionService reflectionService;
    private final MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig;

    private final PropertyMappingsBuilder<T, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>> propertyMappingsBuilder;
    private final DefaultFieldAppenderFactory fieldAppenderFactory;
    private final CellWriter cellWriter;
    private final ClassMeta<T> classMeta;

    private int currentIndex = 0;

    public CsvWriterBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig,
            DefaultFieldAppenderFactory fieldAppenderFactory,
            CellWriter cellWriter) {
        this.fieldAppenderFactory = fieldAppenderFactory;
        this.cellWriter = cellWriter;
        this.reflectionService = classMeta.getReflectionService();
        this.mapperConfig = mapperConfig;
        this.propertyMappingsBuilder =
                new PropertyMappingsBuilder<T, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>>(
                        classMeta,
                        mapperConfig.propertyNameMatcherFactory(),
                        mapperConfig.mapperBuilderErrorHandler());
        this.classMeta = classMeta;
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
    public ContextualMapper<T, Appendable> mapper() {

        final List<FieldMapper<T, Appendable>> mappers = new ArrayList<FieldMapper<T, Appendable>>();


        final MappingContextFactoryBuilder mappingContextFactoryBuilder = new MappingContextFactoryBuilder(new KeySourceGetter<CsvColumnKey, T>() {
            @Override
            public Object getValue(CsvColumnKey key, T source) throws SQLException {
                throw new UnsupportedOperationException();
            }
        });

        final CellSeparatorAppender<T> cellSeparatorAppender = new CellSeparatorAppender<T>(cellWriter);
        propertyMappingsBuilder.forEachProperties(
                new ForEachCallBack<PropertyMapping<T, ?, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>>>() {
                    int i = 0;
                    @Override
                    public void handle(PropertyMapping<T, ?, CsvColumnKey,  FieldMapperColumnDefinition<CsvColumnKey, T>> pm) {
                        FieldMapper<T, Appendable> fieldMapper =
                                fieldAppenderFactory.newFieldAppender(
                                        pm,
                                        cellWriter,
                                        mappingContextFactoryBuilder);
                        if (i > 0) {
                            mappers.add(cellSeparatorAppender);
                        }
                        mappers.add(fieldMapper);
                        i++;
                    }
                }
        );
        mappers.add(new EndOfRowAppender<T>(cellWriter));

        MapperImpl<T, Appendable> mapper = new MapperImpl<T, Appendable>(
                mappers.toArray(new FieldMapper[0]),
                new FieldMapper[0],
                STRING_BUILDER_INSTANTIATOR);

        return
            new ContextualMapper<T, Appendable>(mapper, mappingContextFactoryBuilder.newFactory());
    }


    public CsvWriterBuilder<T> defaultHeaders() {
        for (String column : classMeta.generateHeaders()) {
            addColumn(column);
        }
        return this;
    }

    private static final AppendableInstantiator STRING_BUILDER_INSTANTIATOR = new AppendableInstantiator();

    public static <T> CsvWriterBuilder<T> newBuilder(Class<T> clazz) {
        ClassMeta<T> classMeta = ReflectionService.disableAsm().getClassMeta(clazz);
        return CsvWriterBuilder.newBuilder(classMeta);
    }

    public static <T> CsvWriterBuilder<T> newBuilder(ClassMeta<T> classMeta) {
        return CsvWriterBuilder.newBuilder(classMeta, CsvCellWriter.DEFAULT_WRITER);
    }

    public static <T> CsvWriterBuilder<T> newBuilder(ClassMeta<T> classMeta, CellWriter cellWriter) {
        MapperConfig<CsvColumnKey,FieldMapperColumnDefinition<CsvColumnKey,T>> config =
                MapperConfig.<T, CsvColumnKey>fieldMapperConfig();
        DefaultFieldAppenderFactory appenderFactory = DefaultFieldAppenderFactory.<T>instance();
        CsvWriterBuilder<T> builder =
                new CsvWriterBuilder<T>(
                        classMeta,
                        config,
                        appenderFactory,
                        cellWriter);
        return builder;
    }

    private static class AppendableInstantiator implements Instantiator<Object, Appendable> {
        @Override
        public Appendable newInstance(Object o) throws Exception {
            return new StringBuilder();
        }
    }

}
