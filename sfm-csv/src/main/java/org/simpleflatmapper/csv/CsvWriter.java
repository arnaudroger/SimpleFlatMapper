package org.simpleflatmapper.csv;

import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.converter.DefaultContextFactoryBuilder;
import org.simpleflatmapper.converter.ToStringConverter;
import org.simpleflatmapper.csv.mapper.FieldMapperToAppendableFactory;
import org.simpleflatmapper.lightningcsv.CellWriter;
import org.simpleflatmapper.lightningcsv.CsvCellWriter;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.PropertyWithGetter;
import org.simpleflatmapper.map.mapper.ContextualSourceFieldMapperImpl;
import org.simpleflatmapper.map.property.FormatProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.TypeReference;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A CsvWriter allows the caller to write object of type T to an appendable in a specified format. See {@link CsvWriter#from(Class)} to create one.
 * <p>
 * The DSL allows to create a CsvWriter easily. The CsvWriter will by default append the headers on the call to {@link CsvWriter.CsvWriterDSL#to(Appendable)}
 * Because the DSL create a mapper it is better to cache the {@link CsvWriter.CsvWriterDSL}.
 * <br>
 * <code>
 *     CsvWriter csvWriter = CsvWriter.from(MyObject.class).to(myWriter);<br>
 *     csvWriter.append(obj1).append(obj2);<br>
 * </code>
 * <br>
 * You can deactivate that by calling {@link CsvWriter.CsvWriterDSL#skipHeaders()}
 * <br>
 * <code>
 *     CsvWriter csvWriter = CsvWriter.from(MyObject.class).skipHeaders().to(myWriter);<br>
 * </code>
 * <br>
 * You can also specified the property names.
 * <br>
 * <code>
 *     CsvWriter csvWriter = CsvWriter.from(MyObject.class).columns("id", "name").to(myWriter);<br>
 * </code>
 * <br>
 * Or add a property with a specified format
 * <br>
 * <code>
 *     CsvWriter csvWriter = CsvWriter.from(MyObject.class).columns("date", new SimpleDateFormat("yyyyMMdd")).to(myWriter);<br>
 * </code>
 *
 * @param <T> the type of object to write
 */
public class CsvWriter<T>  {

    private final FieldMapper<T, Appendable> mapper;
    private final Appendable appendable;
    private final MappingContext<? super T> mappingContext;

    private CsvWriter(FieldMapper<T, Appendable> mapper, Appendable appendable, MappingContext<? super T> mappingContext) {
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

        MapperConfig<CsvColumnKey, ?> mapperConfig = MapperConfig.<CsvColumnKey, CsvRow>fieldMapperConfig();
        try {
            String[] headers = defaultHeaders(classMeta);
            for(String header : headers) {
                builder.addColumn(header);
            }
            ContextualSourceFieldMapperImpl<T, Appendable> mapper = builder.mapper();

            return new DefaultCsvWriterDSL<T>(
                    CsvWriter.<T>toColumnDefinitions(headers),
                    cellWriter,
                    mapper,
                    classMeta,
                    mapperConfig, false);
        } catch (UnsupportedOperationException e) {
            return new NoColumnCsvWriterDSL<T>(
                    cellWriter,
                    classMeta,
                    mapperConfig, false);
        }
    }

    private static <T> String[] defaultHeaders(ClassMeta<T> classMeta) {
        List<String> columns = new ArrayList<String>();
        addDefaultHeaders(classMeta, "", columns);
        return  columns.toArray(new String[0]);
    }


    private static <P> void addDefaultHeaders(final ClassMeta<P> classMeta, final String prefix, final List<String> columns) {
        classMeta.forEachProperties(new Consumer<PropertyMeta<P,?>>() {
            @Override
            public void accept(PropertyMeta<P, ?> propertyMeta) {
                if (! PropertyWithGetter.INSTANCE.test(propertyMeta)) return;
                String currentName = prefix +  propertyMeta.getPath();
                if (!canWrite(propertyMeta.getPropertyType())) {
                    addDefaultHeaders(propertyMeta.getPropertyClassMeta(), currentName + "_", columns);
                } else {
                    columns.add(toDelimiterSeparated(currentName));
                }

            }
        });
    }

    private static String toDelimiterSeparated(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        boolean lastWasUpperCase = false;
        for(int i = 0; i < str.length(); i ++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (lastWasUpperCase) {
                    sb.append(c);
                } else {
                    if (i > 0) {
                        sb.append('_');
                    }
                    sb.append(Character.toLowerCase(c));
                }
                lastWasUpperCase = true;
            } else {
                lastWasUpperCase = false;
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static <P, E> boolean canWrite(Type type) {
        ContextualConverter<? super Object, ?> converter = ConverterService.getInstance().findConverter(type, CharSequence.class, new DefaultContextFactoryBuilder());
        return (converter != null && (! (converter instanceof ToStringConverter) || allowToStringConverter(type) ) );
    }

    private static boolean allowToStringConverter(Type type) {
        return TypeHelper.isPrimitive(type)
                || TypeHelper.isEnum(type)
                || TypeHelper.isInPackage(type, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.startsWith("java.");
            }
        })
                ;

    }

    @SuppressWarnings("unchecked")
    private static <T> Column[] toColumnDefinitions(String[] header) {
        Column[] columnDefinitions = new Column[header.length];
        int offset = 0;
        return toColumnDefinitions(header, columnDefinitions, offset);
    }

    private static Column[] toColumnDefinitions(String[] header, Column[] columnDefinitions, int offset) {
        FieldMapperColumnDefinition<CsvColumnKey> identity = FieldMapperColumnDefinition.<CsvColumnKey>identity();
        for(int i = 0; i < header.length; i++) {
            columnDefinitions[i + offset] = new Column(header[i], identity);
        }
        return columnDefinitions;
    }

    /**
     * the csv writer DSL
     * @param <T> the type of object to write
     */
    public static class CsvWriterDSL<T> {

        protected final Column[] columns;
        protected final ContextualSourceFieldMapperImpl<T, Appendable> mapper;
        protected final CellWriter cellWriter;
        protected final ClassMeta<T> classMeta;
        protected final MapperConfig<CsvColumnKey, ?> mapperConfig;
        protected final boolean skipHeaders;

        private CsvWriterDSL(
                Column[] columns,
                CellWriter cellWriter,
                ContextualSourceFieldMapperImpl<T, Appendable> mapper,
                ClassMeta<T> classMeta,
                MapperConfig<CsvColumnKey, ?> mapperConfig,
                boolean skipHeaders) {
            this.columns = columns;
            this.mapper = mapper;
            this.cellWriter = cellWriter;
            this.classMeta = classMeta;
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
                cellWriter.writeValue(columns[i].name(), appendable);

            }
            cellWriter.endOfRow(appendable);
        }

        /**
         * Create a new DSL object identical to the current one but and append the specified columns
         * @param columnNames the list of property names
         * @return the new DSL
         */
        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> columns(String... columnNames) {
            Column[] newColumns =
                    Arrays.copyOf(columns, columns.length + columnNames.length);
            toColumnDefinitions(columnNames, newColumns, columns.length);
            return newColumnMapDSL(classMeta, newColumns, mapperConfig, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified property added.
         * @param column the property name
         * @param property the property properties
         * @return the new DSL
         */
        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> column(String column, Object... property) {
            Column[] newColumns =
                    Arrays.copyOf(columns, columns.length + 1);

            FieldMapperColumnDefinition<CsvColumnKey> columnDefinition =  FieldMapperColumnDefinition.<CsvColumnKey>identity().add(property);
            newColumns[columns.length] = new Column(column, columnDefinition);

            return newColumnMapDSL(classMeta, newColumns, mapperConfig, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified property added.
         * @param column the property name
         * @param format the property formatter
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
            return newMapDSL(classMeta, columns, mapperConfig, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified mapperConfig.
         * @param mapperConfig the mapperConfig
         * @return the new DSL
         */
        public CsvWriterDSL<T> mapperConfig(MapperConfig<CsvColumnKey, ?> mapperConfig) {
            return newMapDSL(classMeta, columns, mapperConfig, cellWriter, skipHeaders);
        }

        /**
         * Create a new DSL object identical to the current one but with the specified cellWriter.
         * @param cellWriter the cellWriter
         * @return the new DSL
         */
        public CsvWriterDSL<T> cellWriter(CellWriter cellWriter) {
            return newMapDSL(classMeta, columns, mapperConfig, cellWriter, skipHeaders);
        }

        public CsvWriterDSL<T> separator(char separator) {
            if (cellWriter instanceof CsvCellWriter) {
                return newMapDSL(classMeta, columns, mapperConfig, ((CsvCellWriter)cellWriter).separator(separator), skipHeaders);
            }
            throw new IllegalStateException("Custom cell writer set, cannot use schema to alter it");
        }

        public CsvWriterDSL<T> quote(char quote) {
            if (cellWriter instanceof CsvCellWriter) {
                return newMapDSL(classMeta, columns, mapperConfig, ((CsvCellWriter)cellWriter).quote(quote), skipHeaders);
            }
            throw new IllegalStateException("Custom cell writer set, cannot use schema to alter it");
        }
        
        public CsvWriterDSL<T> escape(char escape) {
            if (cellWriter instanceof CsvCellWriter) {
                return newMapDSL(classMeta, columns, mapperConfig, ((CsvCellWriter)cellWriter).escape(escape), skipHeaders);
            }
            throw new IllegalStateException("Custom cell writer set, cannot use schema to alter it");
        }
        
        public CsvWriterDSL<T> endOfLine(String endOfLine) {
            if (cellWriter instanceof CsvCellWriter) {
                return newMapDSL(classMeta, columns, mapperConfig, ((CsvCellWriter)cellWriter).endOfLine(endOfLine), skipHeaders);
            }
            throw new IllegalStateException("Custom cell writer set, cannot use schema to alter it");
        }

        public CsvWriterDSL<T> alwaysEscape() {
            if (cellWriter instanceof CsvCellWriter) {
                return newMapDSL(classMeta, columns, mapperConfig, ((CsvCellWriter)cellWriter).alwaysEscape(), skipHeaders);
            }
            throw new IllegalStateException("Custom cell writer set, cannot use schema to alter it");
        }

        /**
         * Create a new DSL object identical to the current one except it will not append the headers to the appendable.
         * @return the new DSL
         */
        public CsvWriterDSL<T> skipHeaders() {
            return newMapDSL(classMeta, columns, mapperConfig, cellWriter, true);
        }


        public MapperConfig<CsvColumnKey, ?> mapperConfig() {
            return mapperConfig;
        }

        protected CsvWriterDSL<T> newColumnMapDSL(
                ClassMeta<T> classMeta,
                Column[] columns,
                MapperConfig<CsvColumnKey, ?> mapperConfig,
                CellWriter cellWriter,
                boolean skipHeaders) {

            CsvWriterBuilder<T> builder = new CsvWriterBuilder<T>(classMeta, mapperConfig, new FieldMapperToAppendableFactory(cellWriter), cellWriter);

            for( Column col : columns) {
                builder.addColumn(col.name(), col.definition());
            }

            ContextualSourceFieldMapperImpl<T, Appendable> mapper = builder.mapper();

            return new CsvWriterDSL<T>(columns, cellWriter, mapper,  classMeta, mapperConfig, skipHeaders);
        }

        protected CsvWriterDSL<T> newMapDSL(
                ClassMeta<T> classMeta,
                Column[] columns,
                MapperConfig<CsvColumnKey, ?> mapperConfig,
                CellWriter cellWriter,
                boolean skipHeaders) {

            CsvWriterBuilder<T> builder = new CsvWriterBuilder<T>(classMeta, mapperConfig, new FieldMapperToAppendableFactory(cellWriter), cellWriter);

            for( Column col : columns) {
                builder.addColumn(col.name(), col.definition());
            }

            ContextualSourceFieldMapperImpl<T, Appendable> mapper = builder.mapper();

            return newCsvWriterDSL(columns, cellWriter, mapper, classMeta, mapperConfig, skipHeaders);
        }

        protected CsvWriterDSL<T> newCsvWriterDSL(Column[] columns,
                                                CellWriter cellWriter, ContextualSourceFieldMapperImpl<T, Appendable> mapper, ClassMeta<T> classMeta,
                                                MapperConfig<CsvColumnKey, ?> mapperConfig,
                                                boolean skipHeaders) {
            return new CsvWriterDSL<T>(columns, cellWriter, mapper, classMeta, mapperConfig, skipHeaders);
        }
    }

    public static class NoColumnCsvWriterDSL<T> extends CsvWriterDSL<T> {
        @SuppressWarnings("unchecked")
        public NoColumnCsvWriterDSL(
                CellWriter cellWriter,
                ClassMeta<T> classMeta,
                MapperConfig<CsvColumnKey, ?> mapperConfig, boolean skipHeaders) {
            super(new Column[0], cellWriter, null, classMeta, mapperConfig, skipHeaders);
        }

        @Override
        public CsvWriter<T> to(Appendable appendable) throws IOException {
            throw new IllegalStateException("No column defined");
        }
        
        @Override
        protected NoColumnCsvWriterDSL<T> newCsvWriterDSL(Column[] columns,
                                                  CellWriter cellWriter,
                                                  ContextualSourceFieldMapperImpl<T, Appendable> mapper, ClassMeta<T> classMeta,
                                                  MapperConfig<CsvColumnKey, ?> mapperConfig,
                                                  boolean skipHeaders) {
            return new NoColumnCsvWriterDSL<T>(cellWriter, classMeta, mapperConfig, skipHeaders);
        }
    }

    public static class DefaultCsvWriterDSL<T> extends CsvWriterDSL<T> {

        private DefaultCsvWriterDSL(
                Column[] columns,
                CellWriter cellWriter,
                ContextualSourceFieldMapperImpl<T, Appendable> mapper,
                ClassMeta<T> classMeta,
                MapperConfig<CsvColumnKey, ?> mapperConfig, boolean skipHeaders) {
            super(columns, cellWriter, mapper, classMeta, mapperConfig, skipHeaders);
        }


        /**
         * Create a new DSL object identical to the current one but with the specified columns instead of the default ones.
         * @param columnNames the list of property names
         * @return the new DSL
         */
        public CsvWriterDSL<T> columns(String... columnNames) {
            return newColumnMapDSL(classMeta, CsvWriter.<T>toColumnDefinitions(columnNames), mapperConfig, cellWriter, skipHeaders);
        }


        /**
         * Create a new DSL object identical to the current one but with the specified property instead of the default ones.
         * @param column the property name
         * @param property the property properties
         * @return the new DSL
         */
        @SuppressWarnings("unchecked")
        public CsvWriterDSL<T> column(String column, Object... property) {
            Column[] newColumns = new Column[1];

            FieldMapperColumnDefinition<CsvColumnKey> columnDefinition =  FieldMapperColumnDefinition.<CsvColumnKey>identity().add(property);
            newColumns[0] = new Column(column, columnDefinition);

            return newColumnMapDSL(classMeta, newColumns, mapperConfig, cellWriter, skipHeaders);
        }
        
        @Override
        protected CsvWriterDSL<T> newCsvWriterDSL(Column[] columns,
                                                  CellWriter cellWriter,
                                                  ContextualSourceFieldMapperImpl<T, Appendable> mapper, ClassMeta<T> classMeta,
                                                  MapperConfig<CsvColumnKey, ?> mapperConfig,
                                                  boolean skipHeaders) {
            return new DefaultCsvWriterDSL<T>(columns, cellWriter, mapper, classMeta, mapperConfig, skipHeaders);
        }
    }

    // Tuple2<String, FieldMapperColumnDefinition<CsvColumnKey>>
    public static class Column {
        private final String name;
        private final FieldMapperColumnDefinition<CsvColumnKey> definition;

        public Column(String name, FieldMapperColumnDefinition<CsvColumnKey> definition) {
            this.name = name;
            this.definition = definition;
        }

        public String name() {
            return name;
        }

        public FieldMapperColumnDefinition<CsvColumnKey> definition() {
            return definition;
        }
    }



}
