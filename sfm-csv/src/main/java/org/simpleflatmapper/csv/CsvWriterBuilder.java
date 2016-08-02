package org.simpleflatmapper.csv;


import org.simpleflatmapper.csv.impl.writer.CellSeparatorAppender;
import org.simpleflatmapper.csv.impl.writer.CsvCellWriter;
import org.simpleflatmapper.csv.impl.writer.EndOfRowAppender;
import org.simpleflatmapper.csv.mapper.FieldMapperToAppendableFactory;
import org.simpleflatmapper.core.map.*;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.mapper.ConstantTargetFieldMapperFactory;
import org.simpleflatmapper.core.map.mapper.PropertyMapping;
import org.simpleflatmapper.core.reflect.*;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;

import java.util.List;

public class CsvWriterBuilder<T> extends AbstractWriterBuilder<Appendable, T, CsvColumnKey, CsvWriterBuilder<T>> {

    private final CellWriter cellWriter;
    private final CellSeparatorAppender<T> cellSeparatorAppender;

    public CsvWriterBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> mapperConfig,
            ConstantTargetFieldMapperFactory<Appendable, CsvColumnKey> fieldAppenderFactory,
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
                MapperConfig.<CsvColumnKey>fieldMapperConfig();
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
    protected CsvColumnKey newKey(String column, int i, FieldMapperColumnDefinition<CsvColumnKey> columnDefinition) {
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
