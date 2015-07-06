package org.sfm.poi;


import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.GetterFactory;
import org.sfm.map.Mapper;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.impl.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.poi.impl.JoinSheetMapper;
import org.sfm.poi.impl.CsvColumnKeyRowKeySourceGetter;
import org.sfm.poi.impl.RowGetterFactory;
import org.sfm.poi.impl.StaticSheetMapper;
import org.sfm.reflect.meta.ClassMeta;

public class SheetMapperBuilder<T> {

    public static final MapperSourceImpl<Row, CsvColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<Row, CsvColumnKey>(Row.class, new RowGetterFactory());

    private final FieldMapperMapperBuilder<Row, T, CsvColumnKey> builder;
    private final MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>> mapperConfig;
    private final MappingContextFactoryBuilder<Row, CsvColumnKey> mappingContextFactoryBuilder;
    private int currentColumn = 0;

    public SheetMapperBuilder(ClassMeta<T> classMeta,
                              MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>> mapperConfig,
                              GetterFactory<Row, CsvColumnKey> getterFactory) {
        this.mappingContextFactoryBuilder = new MappingContextFactoryBuilder<Row, CsvColumnKey>(new CsvColumnKeyRowKeySourceGetter());
        builder =
            new FieldMapperMapperBuilder<Row, T, CsvColumnKey>(
                FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                classMeta,
                mapperConfig,
                    mappingContextFactoryBuilder
        );
        this.mapperConfig = mapperConfig;
    }

    /**
     *
     * @param columnName the name of the column to map
     * @return this instance
     */
    public SheetMapperBuilder<T> addMapping(String columnName) {
        return addMapping(columnName, FieldMapperColumnDefinition.<CsvColumnKey, Row>identity());
    }

    /**
     *
     * @param columnName the name of the column to map
     * @param definition the column definition
     * @return this instance
     */
    public SheetMapperBuilder<T> addMapping(String columnName, FieldMapperColumnDefinition<CsvColumnKey, Row> definition) {
        return addMapping(columnName, currentColumn++, definition);
    }

    /**
     *
     * @param columnName the name of the column to map
     * @param columnIndex the index of the column
     * @param definition the column definition
     * @return this instance
     */
    public SheetMapperBuilder<T> addMapping(String columnName, int columnIndex, FieldMapperColumnDefinition<CsvColumnKey, Row> definition) {
        builder.addMapping(new CsvColumnKey(columnName, columnIndex), definition);
        return this;
    }

    /**
     *
     * @param k the column key
     * @param columnDefinition the column definition
     * @return this instance
     */
    public SheetMapperBuilder<T> addMapping(CsvColumnKey k, FieldMapperColumnDefinition<CsvColumnKey, Row> columnDefinition) {
        builder.addMapping(k, columnDefinition);
        return this;
    }

    /**
     *
     * @param columnName the name of the column to map
     * @param properties the column properties
     * @return this instance
     */
    public SheetMapperBuilder<T> addMapping(String columnName, ColumnProperty... properties) {
        return addMapping(columnName, currentColumn++, properties);
    }

    /**
     *
     * @param columnName the name of the column to map
     * @param columnIndex the index of the column
     * @param properties the column properties
     * @return this instance
     */
    public SheetMapperBuilder<T> addMapping(String columnName, int columnIndex,  ColumnProperty... properties) {
        return addMapping(new CsvColumnKey(columnName, columnIndex), properties);
    }

    /**
     *
     * @param k the column key
     * @param properties the column properties
     * @return this instance
     */
    public SheetMapperBuilder<T> addMapping(CsvColumnKey k, ColumnProperty... properties) {
        builder.addMapping(k, FieldMapperColumnDefinition.<CsvColumnKey, Row>of(properties));
        return this;
    }

    /**
     *
     * @param column the column to add as a key
     * @return this instance
     */
    public SheetMapperBuilder<T> addKey(String column) {
        return addMapping(column, FieldMapperColumnDefinition.<CsvColumnKey, Row>key());
    }

    /**
     *
     * @return a new instance of RowMapper&lt;T&gt;
     */
    public RowMapper<T> mapper() {
        Mapper<Row, T> mapper = builder.mapper();

        if (builder.hasJoin()) {
            return new JoinSheetMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
        } else {
            return new StaticSheetMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
        }
    }
}
