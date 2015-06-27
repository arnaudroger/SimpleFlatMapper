package org.sfm.csv;

import org.sfm.csv.impl.writer.CellWriter;
import org.sfm.csv.impl.writer.CsvCellWriter;
import org.sfm.csv.impl.writer.DefaultFieldAppenderFactory;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.FormatProperty;
import org.sfm.map.impl.ContextualMapper;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.MapperConfig;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ErrorHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.Format;

public class CsvWriter<T>  {

    private final Mapper<T, Appendable> mapper;
    private final Appendable appendable;
    private final MappingContext<T> mappingContext;

    public CsvWriter(Mapper<T, Appendable> mapper, Appendable appendable, MappingContext<T> mappingContext) {
        this.mapper = mapper;
        this.appendable = appendable;
        this.mappingContext = mappingContext;
    }

    public CsvWriter<T> append(T value) throws IOException {
        try {
            mapper.mapTo(value, appendable, mappingContext);
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        }
        return this;
    }

    public static <T> CsvWriterDSL<T> from(Class<T> type) {
        return from((Type)type);
    }

    public static <T> CsvWriterDSL<T> from(TypeReference<T> typeReference) {
        return from(typeReference.getType());
    }

    public static <T> CsvWriterDSL<T> from(Type type) {

        ClassMeta<T> classMeta = ReflectionService.newInstance().getClassMeta(type);

        CellWriter cellWriter = CsvCellWriter.DEFAULT_WRITER;

        CsvWriterBuilder<T> builder = CsvWriterBuilder
                .newBuilder(classMeta, cellWriter);

        MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig = MapperConfig.<T, CsvColumnKey>fieldMapperConfig();
        try {
            builder.defaultHeaders();
            ContextualMapper<T, Appendable> mapper = builder.mapper();
            return new DefaultCsvWriterDSL<T>(
                    CsvWriter.<T>toColumnDefinitions(classMeta.generateHeaders()),
                    cellWriter,
                    mapper,
                    classMeta,
                    DefaultFieldAppenderFactory.instance(),
                    mapperConfig);
        } catch (UnsupportedOperationException e) {
            return new NoColumnCsvWriterDSL<T>(
                    cellWriter,
                    classMeta,
                    DefaultFieldAppenderFactory.instance(),
                    mapperConfig);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] toColumnDefinitions(String[] header) {
        Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columnDefinitions = new Tuple2[header.length];
        FieldMapperColumnDefinition<CsvColumnKey, T> identity = FieldMapperColumnDefinition.<CsvColumnKey, T>identity();
        for(int i = 0; i < header.length; i++) {
            columnDefinitions[i] = new Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>(header[i], identity);
        }
        return columnDefinitions;
    }

    public static class CsvWriterDSL<T> {

        protected final Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns;
        protected final ContextualMapper<T, Appendable> mapper;
        protected final CellWriter cellWriter;
        protected final ClassMeta<T> classMeta;
        protected final DefaultFieldAppenderFactory fieldAppenderFactory;
        protected final MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig;

        public CsvWriterDSL(
                Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                CellWriter cellWriter,
                ContextualMapper<T, Appendable> mapper,
                ClassMeta<T> classMeta,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig) {
            this.columns = columns;
            this.mapper = mapper;
            this.cellWriter = cellWriter;
            this.classMeta = classMeta;
            this.fieldAppenderFactory = fieldAppenderFactory;
            this.mapperConfig = mapperConfig;
        }

        public CsvWriter<T> to(Appendable appendable) throws IOException {
            for(int i = 0; i < columns.length; i++) {
                if (i != 0) {
                    cellWriter.nextCell(appendable);
                }
                cellWriter.writeValue(columns[i].first(), appendable);

            }
            cellWriter.endOfRow(appendable);

            return new CsvWriter<T>(mapper, appendable, mapper.newMappingContext());
        }

        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> columns(String... headers) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[columns.length + headers.length];
            System.arraycopy(columns, 0, newColumns, 0, columns.length);
            System.arraycopy(toColumnDefinitions(headers), 0, newColumns, columns.length, headers.length);

            return newMapDSL(classMeta, newColumns, mapperConfig, fieldAppenderFactory, cellWriter);
        }

        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> column(String column, ColumnProperty... property) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[columns.length + 1];
            System.arraycopy(columns, 0, newColumns, 0, columns.length);

            FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition =  FieldMapperColumnDefinition.<CsvColumnKey, T>identity().add(property);
            newColumns[columns.length] = new Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>(column, columnDefinition);

            return newMapDSL(classMeta, newColumns, mapperConfig, fieldAppenderFactory, cellWriter);
        }

        public CsvWriterDSL<T> column(String column, Format format) {
            return column(column, new FormatProperty(format));
        }

        public CsvWriterDSL<T> classMeta(ClassMeta<T> classMeta) {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter);
        }

        public CsvWriterDSL<T> mapperConfig(MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig) {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter);
        }

        public CsvWriterDSL<T> fieldAppenderFactory(DefaultFieldAppenderFactory fieldAppenderFactory) {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter);
        }

        public CsvWriterDSL<T> cellWriter(CellWriter cellWriter) {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter);
        }


        public MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig() {
            return mapperConfig;
        }


        protected CsvWriterDSL<T> newMapDSL(
                ClassMeta<T> classMeta,
                Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                CellWriter cellWriter) {

            CsvWriterBuilder<T> builder = new CsvWriterBuilder<T>(classMeta, mapperConfig, fieldAppenderFactory, cellWriter);

            for( Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>> col : columns) {
                builder.addColumn(col.first(), col.second());
            }

            ContextualMapper<T, Appendable> mapper = builder.mapper();

            return new CsvWriterDSL<T>(columns, cellWriter, mapper, classMeta, fieldAppenderFactory, mapperConfig);
        }
    }

    public static class NoColumnCsvWriterDSL<T> extends CsvWriterDSL<T> {
        @SuppressWarnings("unchecked")
        public NoColumnCsvWriterDSL(
                CellWriter cellWriter,
                ClassMeta<T> classMeta,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig) {

            super(new Tuple2[0], cellWriter, null, classMeta, fieldAppenderFactory, mapperConfig);
        }

        @Override
        public CsvWriter<T> to(Appendable appendable) throws IOException {
            throw new IllegalStateException("No columned defined");
        }
    }

    public static class DefaultCsvWriterDSL<T> extends CsvWriterDSL<T> {

        public DefaultCsvWriterDSL(
                Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                CellWriter cellWriter,
                ContextualMapper<T, Appendable> mapper,
                ClassMeta<T> classMeta,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig) {
            super(columns, cellWriter, mapper, classMeta, fieldAppenderFactory, mapperConfig);
        }

        public CsvWriterDSL<T> columns(String... headers) {
            return newMapDSL(classMeta, CsvWriter.<T>toColumnDefinitions(headers), mapperConfig, fieldAppenderFactory, cellWriter);
        }

        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> column(String column, ColumnProperty... property) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[1];

            FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition =  FieldMapperColumnDefinition.<CsvColumnKey, T>identity().add(property);
            newColumns[0] = new Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>(column, columnDefinition);

            return newMapDSL(classMeta, newColumns, mapperConfig, fieldAppenderFactory, cellWriter);
        }
    }

}
