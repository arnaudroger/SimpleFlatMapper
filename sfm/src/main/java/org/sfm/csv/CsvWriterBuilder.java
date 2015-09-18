package org.sfm.csv;


import org.sfm.csv.impl.writer.*;
import org.sfm.map.*;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.KeySourceGetter;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.mapper.ContextualMapper;
import org.sfm.map.mapper.MapperImpl;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.mapper.PropertyMappingsBuilder;
import org.sfm.reflect.*;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.ForEachCallBack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsvWriterBuilder<T> extends AbstractWriterBuilder<Appendable, T, CsvColumnKey, CsvWriterBuilder<T>> {

    private final CellWriter cellWriter;
    private final CellSeparatorAppender<T> cellSeparatorAppender;

    public CsvWriterBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> mapperConfig,
            FieldMapperToSourceFactory<Appendable, CsvColumnKey> fieldAppenderFactory,
            CellWriter cellWriter) {
        super(classMeta, Appendable.class, mapperConfig, fieldAppenderFactory);
        this.cellWriter = cellWriter;
        this.cellSeparatorAppender = new CellSeparatorAppender<T>(cellWriter);
    }

    public CsvWriterBuilder<T> defaultHeaders() {
        for (String column : classMeta.generateHeaders()) {
            addColumn(column);
        }
        return this;
    }

    public static <T> CsvWriterBuilder<T> newBuilder(Class<T> clazz) {
        ClassMeta<T> classMeta = ReflectionService.newInstance().getClassMeta(clazz);
        return CsvWriterBuilder.newBuilder(classMeta);
    }

    public static <T> CsvWriterBuilder<T> newBuilder(ClassMeta<T> classMeta) {
        return CsvWriterBuilder.newBuilder(classMeta, CsvCellWriter.DEFAULT_WRITER);
    }

    public static <T> CsvWriterBuilder<T> newBuilder(ClassMeta<T> classMeta, CellWriter cellWriter) {
        MapperConfig<CsvColumnKey,FieldMapperColumnDefinition<CsvColumnKey>> config =
                MapperConfig.<T, CsvColumnKey>fieldMapperConfig();
        FieldMapperToAppendableFactory appenderFactory = new FieldMapperToAppendableFactory(cellWriter);
        CsvWriterBuilder<T> builder =
                new CsvWriterBuilder<T>(
                        classMeta,
                        config,
                        appenderFactory,
                        cellWriter);
        return builder;
    }

    @Override
    protected Instantiator<T, Appendable> getInstantiator() {
        return new AppendableInstantiator<T>();
    }

    @Override
    protected CsvColumnKey newKey(String column, int i) {
        return new CsvColumnKey(column, i);
    }

    @Override
    protected void preFieldProcess(List<FieldMapper<T, Appendable>> fieldMappers, PropertyMapping<T, ?, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> pm) {
        if (pm.getColumnKey().getIndex() > 0) {
            fieldMappers.add(cellSeparatorAppender);
        }
    }

    @Override
    protected void postMapperProcess(List<FieldMapper<T, Appendable>> fieldMappers) {
        fieldMappers.add(new EndOfRowAppender<T>(cellWriter));
    }

    private static class AppendableInstantiator<T> implements Instantiator<T, Appendable> {
        @Override
        public Appendable newInstance(T o) throws Exception {
            return new StringBuilder();
        }
    }

}
