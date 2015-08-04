package org.sfm.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.MapperKey;
import org.sfm.map.mapper.MapperCache;
import org.sfm.poi.SheetMapper;
import org.sfm.poi.SheetMapperBuilder;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.RowHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


public class DynamicSheetMapper<T> implements SheetMapper<T> {

    private final int startRow = 0;


    private final MapperCache<CsvColumnKey, SheetMapper<T>> mapperCache = new MapperCache<CsvColumnKey, SheetMapper<T>>();
    private final ClassMeta<T> classMeta;
    private final MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>> mapperConfig;
    private final GetterFactory<Row, CsvColumnKey> getterFactory;

    public DynamicSheetMapper(
            ClassMeta<T> classMeta,
            MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>> mapperConfig,
            GetterFactory<Row, CsvColumnKey> getterFactory) {
        this.classMeta = classMeta;
        this.mapperConfig = mapperConfig;
        this.getterFactory = getterFactory;
    }


    @Override
    public Iterator<T> iterator(Sheet sheet) {
        return iterator(startRow, sheet);
    }

    @Override
    public Iterator<T> iterator(int startRow, Sheet sheet) {
        return getPoiMapper(startRow, sheet).iterator(startRow + 1 , sheet);
    }

    @Override
    public <RH extends RowHandler<T>> RH forEach(Sheet sheet, RH rowHandler) {
        return forEach(startRow, sheet, rowHandler);
    }

    @Override
    public <RH extends RowHandler<T>> RH forEach(int startRow, Sheet sheet, RH rowHandler) {
        return getPoiMapper(startRow, sheet).forEach(startRow + 1, sheet, rowHandler);
    }

    //IFJAVA8_START
    @Override
    public Stream<T> stream(Sheet sheet) {
        return stream(startRow, sheet);
    }

    @Override
    public Stream<T> stream(int startRow, Sheet sheet) {
        return getPoiMapper(startRow, sheet).stream(startRow + 1, sheet);
    }
    //IFJAVA8_END

    private SheetMapper<T> getPoiMapper(int startRow, Sheet sheet) {
        Row row = sheet.getRow(startRow);

        List<CsvColumnKey> keys = new ArrayList<CsvColumnKey>(row.getLastCellNum() - row.getFirstCellNum());
        for(short i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                keys.add(new CsvColumnKey(cell.getStringCellValue(), i));
            }
        }

        return getPoiMapper(new MapperKey<CsvColumnKey>(keys.toArray(new CsvColumnKey[keys.size()])));


    }

    private SheetMapper<T> getPoiMapper(MapperKey<CsvColumnKey> key) {
        SheetMapper<T> mapper = mapperCache.get(key);

        if (mapper == null) {
            final SheetMapperBuilder<T> builder =
                    new SheetMapperBuilder<T>(
                            classMeta,
                            mapperConfig,
                            getterFactory);

            for(CsvColumnKey k : key.getColumns()) {
                builder.addMapping(k, FieldMapperColumnDefinition.<CsvColumnKey, Row>identity());
            }

            mapper = builder.mapper();

            mapperCache.add(key, mapper);
        }
        return mapper;
    }

}
