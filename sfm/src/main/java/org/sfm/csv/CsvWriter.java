package org.sfm.csv;

import org.sfm.csv.impl.writer.CsvCellWriter;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.FormatProperty;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.MapperConfig;
import org.sfm.map.mapper.ContextualMapper;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ErrorHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.Format;

/**
 * A CsvWriter allows the caller to write object of type T to an appendable in a specified format. See {@link org.sfm.csv.CsvWriter#from(Class)} to create one.
 * <p>
 * The DSL allows to create a CsvWriter easily. The CsvWriter will by default append the headers on the call to {@link org.sfm.csv.CsvWriter.CsvWriterDSL#to(Appendable)}
 * Because the DSL create a mapper it is better to cache the {@link org.sfm.csv.CsvWriter.CsvWriterDSL}.
 * <br>
 * <code>
 *     CsvWriter csvWriter = CsvWriter.from(MyObject.class).to(myWriter);<br>
 *     csvWriter.append(obj1).append(obj2);<br>
 * </code>
 * <br>
 * You can deactivate that by calling {@link org.sfm.csv.CsvWriter.CsvWriterDSL#skipHeaders()}
 * <br>
 * <code>
 *     CsvWriter csvWriter = CsvWriter.from(MyObject.class).skipHeaders().to(myWriter);<br>
 * </code>
 * <br>
 * You can also specified the column names.
 * <br>
 * <code>
 *     CsvWriter csvWriter = CsvWriter.from(MyObject.class).columns("id", "name").to(myWriter);<br>
 * </code>
 * <br>
 * Or add a column with a specified format
 * <br>
 * <code>
 *     CsvWriter csvWriter = CsvWriter.from(MyObject.class).columns("date", new SimpleDateFormat("yyyyMMdd")).to(myWriter);<br>
 * </code>
 *
 * @param <T> the type of object to write
 */
public class CsvWriter<T>  {

    private final Mapper<T, Appendable> mapper;
    private final Appendable appendable;
    private final MappingContext<T> mappingContext;

    private CsvWriter(Mapper<T, Appendable> mapper, Appendable appendable, MappingContext<T> mappingContext) {
        this.mapper = mapper;
        this.appendable = appendable;
        this.mappingContext = mappingContext;
    }

    /**
     * write the specified value to the underlying appendable.
     * @param value the value to write
     * @return the current writer
     * @throws IOException If an I/O error occurs
     */
    public CsvWriter<T> append(T value) throws IOException {
        try {
            mapper.mapTo(value, appendable, mappingContext);
        } catch(Exception e) {
            ErrorHelper.rethrow(e);
        }
        return this;
    }

    /**
     * Create a DSL on the specified type.
     * @param type the type of object to write
     * @param <T> the type
     * @return a DSL on the specified type
     */
    public static <T> CsvWriterDSL<T> from(Class<T> type) {
        return from((Type)type);
    }

    /**
     * Create a DSL on the specified type.
     * @param typeReference the type of object to write
     * @param <T> the type
     * @return a DSL on the specified type
     */
    public static <T> CsvWriterDSL<T> from(TypeReference<T> typeReference) {
        return from(typeReference.getType());
    }

    /**
     * Create a DSL on the specified type.
     * @param type the type of object to write
     * @param <T> the type
     * @return a DSL on the specified type
     */
    public static <T> CsvWriterDSL<T> from(Type type) {

        ClassMeta<T> classMeta = ReflectionService.newInstance().getClassMeta(type);

        CellWriter cellWriter = CsvCellWriter.DEFAULT_WRITER;

        CsvWriterBuilder<T> builder = CsvWriterBuilder
                .newBuilder(classMeta, cellWriter);

        MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig = MapperConfig.<T, CsvColumnKey>fieldMapperConfig();
        try {
            builder.defaultHeaders();
            ContextualMapper<T, Appendable> mapper = (ContextualMapper<T, Appendable>) builder.mapper();
            return new DefaultCsvWriterDSL<T>(
                    CsvWriter.<T>toColumnDefinitions(classMeta.generateHeaders()),
                    cellWriter,
                    mapper,
                    classMeta,
                    DefaultFieldAppenderFactory.instance(),
                    mapperConfig, false);
        } catch (UnsupportedOperationException e) {
            return new NoColumnCsvWriterDSL<T>(
                    cellWriter,
                    classMeta,
                    DefaultFieldAppenderFactory.instance(),
                    mapperConfig, false);
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

    /**
     * the csv writer DSL
     * @param <T> the type of object to write
     */
    public static class CsvWriterDSL<T> {

        protected final Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns;
        protected final ContextualMapper<T, Appendable> mapper;
        protected final CellWriter cellWriter;
        protected final ClassMeta<T> classMeta;
        protected final DefaultFieldAppenderFactory fieldAppenderFactory;
        protected final MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig;
        protected final boolean skipHeaders;

        private CsvWriterDSL(
                Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                CellWriter cellWriter,
                ContextualMapper<T, Appendable> mapper,
                ClassMeta<T> classMeta,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig,
                boolean skipHeaders) {
            this.columns = columns;
            this.mapper = mapper;
            this.cellWriter = cellWriter;
            this.classMeta = classMeta;
            this.fieldAppenderFactory = fieldAppenderFactory;
            this.mapperConfig = mapperConfig;
            this.skipHeaders = skipHeaders;
        }

        /**
         * Create a writer on the specified appendable for the type T
         * @param appendable the appendable to write to
         * @return a CsvWriter on the specified appendable
         * @throws IOException If an I/O error occurs
         */
        public CsvWriter<T> to(Appendable appendable) throws IOException {
            if (!skipHeaders) {
                addHeaders(appendable);
            }
            return new CsvWriter<T>(mapper, appendable, mapper.newMappingContext());
        }

        private void addHeaders(Appendable appendable) throws IOException {
            for(int i = 0; i < columns.length; i++) {
                if (i != 0) {
                    cellWriter.nextCell(appendable);
                }
                cellWriter.writeValue(columns[i].first(), appendable);

            }
            cellWriter.endOfRow(appendable);
        }

        /**
         * Create a new DSL object identical to the current one but and append the specified columns
         * @param columnNames the list of column names
         * @return the new DSL
         */
        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> columns(String... columnNames) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[columns.length + columnNames.length];
            System.arraycopy(columns, 0, newColumns, 0, columns.length);
            System.arraycopy(toColumnDefinitions(columnNames), 0, newColumns, columns.length, columnNames.length);

            return newColumnMapDSL(classMeta, newColumns, mapperConfig, fieldAppenderFactory, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified column added.
         * @param column the column name
         * @param property the column properties
         * @return the new DSL
         */
        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> column(String column, ColumnProperty... property) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[columns.length + 1];
            System.arraycopy(columns, 0, newColumns, 0, columns.length);

            FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition =  FieldMapperColumnDefinition.<CsvColumnKey, T>identity().add(property);
            newColumns[columns.length] = new Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>(column, columnDefinition);

            return newColumnMapDSL(classMeta, newColumns, mapperConfig, fieldAppenderFactory, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified column added.
         * @param column the column name
         * @param format the column formatter
         * @return the new DSL
         */
        public CsvWriterDSL<T> column(String column, Format format) {
            return column(column, new FormatProperty(format));
        }

        /**
         * Create a new DSL object identical to the current one but with the specified classMeta.
         * @param classMeta the classMeta
         * @return the new DSL
         */
        public CsvWriterDSL<T> classMeta(ClassMeta<T> classMeta) {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified mapperConfig.
         * @param mapperConfig the mapperConfig
         * @return the new DSL
         */
        public CsvWriterDSL<T> mapperConfig(MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig) {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified fieldAppenderFactory.
         * @param fieldAppenderFactory the mapperConfig
         * @return the new DSL
         */
        public CsvWriterDSL<T> fieldAppenderFactory(DefaultFieldAppenderFactory fieldAppenderFactory) {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified cellWriter.
         * @param cellWriter the cellWriter
         * @return the new DSL
         */
        public CsvWriterDSL<T> cellWriter(CellWriter cellWriter) {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one except it will not append the headers to the appendable.
         * @return the new DSL
         */
        public CsvWriterDSL<T> skipHeaders() {
            return newMapDSL(classMeta, columns, mapperConfig, fieldAppenderFactory, cellWriter, true);
        }


        public MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig() {
            return mapperConfig;
        }

        protected CsvWriterDSL<T> newColumnMapDSL(
                ClassMeta<T> classMeta,
                Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                CellWriter cellWriter,
                boolean skipHeaders) {

            CsvWriterBuilder<T> builder = new CsvWriterBuilder<T>(classMeta, mapperConfig, fieldAppenderFactory, cellWriter);

            for( Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>> col : columns) {
                builder.addColumn(col.first(), col.second());
            }

            ContextualMapper<T, Appendable> mapper = (ContextualMapper<T, Appendable>) builder.mapper();

            return new CsvWriterDSL<T>(columns, cellWriter, mapper, classMeta, fieldAppenderFactory, mapperConfig, skipHeaders);
        }

        protected CsvWriterDSL<T> newMapDSL(
                ClassMeta<T> classMeta,
                Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                CellWriter cellWriter,
                boolean skipHeaders) {

            CsvWriterBuilder<T> builder = new CsvWriterBuilder<T>(classMeta, mapperConfig, fieldAppenderFactory, cellWriter);

            for( Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>> col : columns) {
                builder.addColumn(col.first(), col.second());
            }

            ContextualMapper<T, Appendable> mapper = (ContextualMapper<T, Appendable>) builder.mapper();

            return newCsvWriterDSL(columns, cellWriter, mapper, classMeta, fieldAppenderFactory, mapperConfig, skipHeaders);
        }

        protected CsvWriterDSL<T> newCsvWriterDSL(Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                                                CellWriter cellWriter,
                                                ContextualMapper<T, Appendable> mapper, ClassMeta<T> classMeta,
                                                DefaultFieldAppenderFactory fieldAppenderFactory,
                                                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig,
                                                boolean skipHeaders) {
            return new CsvWriterDSL<T>(columns, cellWriter, mapper, classMeta, fieldAppenderFactory, mapperConfig, skipHeaders);
        }
    }

    public static class NoColumnCsvWriterDSL<T> extends CsvWriterDSL<T> {
        @SuppressWarnings("unchecked")
        public NoColumnCsvWriterDSL(
                CellWriter cellWriter,
                ClassMeta<T> classMeta,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig, boolean skipHeaders) {
            super(new Tuple2[0], cellWriter, null, classMeta, fieldAppenderFactory, mapperConfig, skipHeaders);
        }

        @Override
        public CsvWriter<T> to(Appendable appendable) throws IOException {
            throw new IllegalStateException("No columned defined");
        }
        protected NoColumnCsvWriterDSL<T> newCsvWriterDSL(Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                                                  CellWriter cellWriter,
                                                  ContextualMapper<T, Appendable> mapper, ClassMeta<T> classMeta,
                                                  DefaultFieldAppenderFactory fieldAppenderFactory,
                                                  MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig,
                                                  boolean skipHeaders) {
            return new NoColumnCsvWriterDSL<T>(cellWriter, classMeta, fieldAppenderFactory, mapperConfig, skipHeaders);
        }
    }

    public static class DefaultCsvWriterDSL<T> extends CsvWriterDSL<T> {

        private DefaultCsvWriterDSL(
                Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                CellWriter cellWriter,
                ContextualMapper<T, Appendable> mapper,
                ClassMeta<T> classMeta,
                DefaultFieldAppenderFactory fieldAppenderFactory,
                MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig, boolean skipHeaders) {
            super(columns, cellWriter, mapper, classMeta, fieldAppenderFactory, mapperConfig, skipHeaders);
        }


        /**
         * Create a new DSL object identical to the current one but with the specified columns instead of the default ones.
         * @param columnNames the list of column names
         * @return the new DSL
         */
        public CsvWriterDSL<T> columns(String... columnNames) {
            return newColumnMapDSL(classMeta, CsvWriter.<T>toColumnDefinitions(columnNames), mapperConfig, fieldAppenderFactory, cellWriter, skipHeaders);
        }


        /**
         * Create a new DSL object identical to the current one but with the specified column instead of the default ones.
         * @param column the column name
         * @param property the column properties
         * @return the new DSL
         */
        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> column(String column, ColumnProperty... property) {
            Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] newColumns = new Tuple2[1];

            FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition =  FieldMapperColumnDefinition.<CsvColumnKey, T>identity().add(property);
            newColumns[0] = new Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>(column, columnDefinition);

            return newColumnMapDSL(classMeta, newColumns, mapperConfig, fieldAppenderFactory, cellWriter, skipHeaders);
        }
        protected CsvWriterDSL<T> newCsvWriterDSL(Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey, T>>[] columns,
                                                  CellWriter cellWriter,
                                                  ContextualMapper<T, Appendable> mapper, ClassMeta<T> classMeta,
                                                  DefaultFieldAppenderFactory fieldAppenderFactory,
                                                  MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> mapperConfig,
                                                  boolean skipHeaders) {
            return new DefaultCsvWriterDSL<T>(columns, cellWriter, mapper, classMeta, fieldAppenderFactory, mapperConfig, skipHeaders);
        }
    }



}
