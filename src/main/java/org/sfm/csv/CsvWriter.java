package org.sfm.csv;

import org.sfm.csv.impl.writer.CellWriter;
import org.sfm.csv.impl.writer.CsvCellWriter;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.FormatProperty;
import org.sfm.map.impl.ContextualMapper;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ErrorHelper;

import java.io.IOException;
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

    public void write(T value) throws IOException {
        try {
            mapper.mapTo(value, appendable, mappingContext);
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        }
    }

    public static <T> MapDSL<T> from(Class<T> clazz) {

        ClassMeta<T> classMeta = ReflectionService.newInstance().getClassMeta(clazz);

        CellWriter cellWriter = CsvCellWriter.DEFAULT_WRITER;

        ContextualMapper<T, Appendable> mapper =
                CsvWriterBuilder
                        .newBuilder(classMeta, cellWriter)
                        .defaultHeaders()
                        .mapper();

        return new DefaultMapDSL<T>(CsvWriter.<T>toColumnDefinitions(classMeta.generateHeaders()),cellWriter, mapper, classMeta);
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

    public static class MapDSL<T> {

        private final Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns;
        private final ContextualMapper<T, Appendable> mapper;
        private final CellWriter cellWriter;
        private final ClassMeta<T> classMeta;

        public MapDSL(Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns, CellWriter cellWriter, ContextualMapper<T, Appendable> mapper, ClassMeta<T> classMeta) {
            this.columns = columns;
            this.mapper = mapper;
            this.cellWriter = cellWriter;
            this.classMeta = classMeta;
        }

        public CsvWriter<T> writeTo(Appendable appendable) throws IOException {
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
        public MapDSL<T> columns(String... headers) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[columns.length + headers.length];
            System.arraycopy(columns, 0, newColumns, 0, columns.length);
            System.arraycopy(toColumnDefinitions(headers), 0, newColumns, columns.length, headers.length);

            return newMapDSL(newColumns);
        }

        @SuppressWarnings("unchecked")
        public MapDSL<T> column(String column, ColumnProperty... property) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[columns.length + 1];
            System.arraycopy(columns, 0, newColumns, 0, columns.length);

            FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition =  FieldMapperColumnDefinition.<CsvColumnKey, T>identity().add(property);
            newColumns[columns.length] = new Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>(column, columnDefinition);

            return newMapDSL(newColumns);
        }

        public MapDSL<T> column(String column, Format format) {
            return column(column, new FormatProperty(format));
        }

        protected MapDSL<T> newMapDSL(Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns) {
            CsvWriterBuilder<T> builder = CsvWriterBuilder
                    .newBuilder(classMeta, cellWriter);

            for( Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>> col : newColumns) {
                builder.addColumn(col.first(), col.second());
            }

            ContextualMapper<T, Appendable> mapper = builder.mapper();

            return new MapDSL<T>(newColumns, cellWriter, mapper, classMeta);
        }
    }

    public static class DefaultMapDSL<T> extends MapDSL<T> {

        public DefaultMapDSL(Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns, CellWriter cellWriter, ContextualMapper<T, Appendable> mapper, ClassMeta<T> classMeta) {
            super(columns, cellWriter, mapper, classMeta);
        }

        public MapDSL<T> columns(String... headers) {
            return newMapDSL(CsvWriter.<T>toColumnDefinitions(headers));
        }

        public MapDSL<T> column(String column, ColumnProperty... property) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[1];

            FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition =  FieldMapperColumnDefinition.<CsvColumnKey, T>identity().add(property);
            newColumns[0] = new Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>(column, columnDefinition);

            return newMapDSL(newColumns);
        }
    }

}
