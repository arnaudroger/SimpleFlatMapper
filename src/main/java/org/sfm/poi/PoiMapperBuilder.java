package org.sfm.poi;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.GetterFactory;
import org.sfm.map.MappingContextFactoryBuilder;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.FieldMapperMapperBuilder;
import org.sfm.map.impl.MapperConfig;
import org.sfm.map.impl.MapperSource;
import org.sfm.poi.impl.RowGetterFactory;
import org.sfm.reflect.ReflectionService;

import java.lang.reflect.Type;
import java.sql.SQLException;

public class PoiMapperBuilder<T> {
    private static final MapperSource<Row, CsvColumnKey> MAPPER_SOURCE =
            new MapperSource<Row, CsvColumnKey>() {
                @Override
                public Class<Row> source() {
                    return Row.class;
                }

                @Override
                public GetterFactory<Row, CsvColumnKey> getterFactory() {
                    return new RowGetterFactory();
                }
            };
    private static final MapperConfig<Row, CsvColumnKey> MAPPER_CONFIG = MapperConfig.config();

    private final FieldMapperMapperBuilder<Row, T, CsvColumnKey> builder;
    private int currentColumn = 0;

    public PoiMapperBuilder(Class<T> target) {
        this((Type) target);
    }
    public PoiMapperBuilder(Type target) {
        builder =
            new FieldMapperMapperBuilder<Row, T, CsvColumnKey>(
                MAPPER_SOURCE,
                ReflectionService.newInstance().getClassMeta(target),
                MAPPER_CONFIG,
                new MappingContextFactoryBuilder<Row, CsvColumnKey>(
                        new MappingContextFactoryBuilder.KeySourceGetter<CsvColumnKey, Row>() {
                            @Override
                            public Object getValue(CsvColumnKey key, Row source) throws SQLException {
                                final Cell cell = source.getCell(key.getIndex());
                                if (cell != null) {
                                    switch (cell.getCellType()) {
                                        case Cell.CELL_TYPE_BLANK:
                                            return "";
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
                        })
        );
    }

    public PoiMapperBuilder<T> addMapping(String columnName) {
        return addMapping(columnName, currentColumn++, FieldMapperColumnDefinition.<CsvColumnKey, Row>identity());
    }

    public PoiMapperBuilder<T> addMapping(String columnName, int columnIndex, FieldMapperColumnDefinition<CsvColumnKey, Row> definition) {
        builder.addMapping(new CsvColumnKey(columnName, columnIndex), definition);
        return this;
    }

    public PoiMapper<T> mapper() {
        return new PoiMapper<T>(builder.mapper());
    }
}
