package org.sfm.poi;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.GetterFactory;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.poi.impl.JoinSheetMapper;
import org.sfm.poi.impl.RowGetterFactory;
import org.sfm.poi.impl.StaticSheetMapper;
import org.sfm.reflect.meta.ClassMeta;

import java.sql.SQLException;

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

    public SheetMapperBuilder<T> addMapping(String columnName) {
        return addMapping(columnName, FieldMapperColumnDefinition.<CsvColumnKey, Row>identity());
    }

    public SheetMapperBuilder<T> addMapping(String columnName, FieldMapperColumnDefinition<CsvColumnKey, Row> definition) {
        return addMapping(columnName, currentColumn++, definition);
    }

    public SheetMapperBuilder<T> addMapping(String columnName, int columnIndex, FieldMapperColumnDefinition<CsvColumnKey, Row> definition) {
        builder.addMapping(new CsvColumnKey(columnName, columnIndex), definition);
        return this;
    }

    public RowMapper<T> mapper() {
        Mapper<Row, T> mapper = builder.mapper();

        if (builder.hasJoin()) {
            return new JoinSheetMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
        } else {
            return new StaticSheetMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
        }
    }

    public SheetMapperBuilder<T> addMapping(CsvColumnKey k, FieldMapperColumnDefinition<CsvColumnKey, Row> columnDefinition) {
        builder.addMapping(k, columnDefinition);
        return this;
    }

    public SheetMapperBuilder<T> addKey(String column) {
        return addMapping(column, FieldMapperColumnDefinition.<CsvColumnKey, Row>key());
    }

    public static class CsvColumnKeyRowKeySourceGetter implements MappingContextFactoryBuilder.KeySourceGetter<CsvColumnKey, Row> {
        @Override
        public Object getValue(CsvColumnKey key, Row source) throws SQLException {
            final Cell cell = source.getCell(key.getIndex());
            if (cell != null) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK:
                        return null;
                    case Cell.CELL_TYPE_BOOLEAN:
                        return cell.getBooleanCellValue();
                    case Cell.CELL_TYPE_NUMERIC:
                        return cell.getNumericCellValue();
                    default:
                        return cell.getStringCellValue();
                }
            }
            return null;
        }
    }
}
