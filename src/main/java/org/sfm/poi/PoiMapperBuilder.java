package org.sfm.poi;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.GetterFactory;
import org.sfm.map.MappingContextFactoryBuilder;
import org.sfm.map.impl.*;
import org.sfm.poi.impl.RowGetterFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.sql.SQLException;

public class PoiMapperBuilder<T> {

    public static final MapperSourceImpl<Row, CsvColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<Row, CsvColumnKey>(Row.class, new RowGetterFactory());

    private final FieldMapperMapperBuilder<Row, T, CsvColumnKey> builder;
    private int currentColumn = 0;

    public PoiMapperBuilder(ClassMeta<T> classMeta,
                            MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>> mapperConfig,
                            GetterFactory<Row, CsvColumnKey> getterFactory) {
        builder =
            new FieldMapperMapperBuilder<Row, T, CsvColumnKey>(
                FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                classMeta,
                mapperConfig,
                new MappingContextFactoryBuilder<Row, CsvColumnKey>(new CsvColumnKeyRowKeySourceGetter())
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

    public static class CsvColumnKeyRowKeySourceGetter implements MappingContextFactoryBuilder.KeySourceGetter<CsvColumnKey, Row> {
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
    }
}
