package org.simpleflatmapper.poi.impl;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.mapper.MapperCache;
import org.simpleflatmapper.poi.SheetMapper;
import org.simpleflatmapper.poi.SheetMapperBuilder;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.csv.CsvColumnKeyMapperKeyComparator;
import org.simpleflatmapper.util.Enumerable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END


public class DynamicSheetMapper<T> implements SheetMapper<T> {

    private final int startRow = 0;


    private final MapperCache<CsvColumnKey, SheetMapper<T>> mapperCache =
            new MapperCache<CsvColumnKey, SheetMapper<T>>(CsvColumnKeyMapperKeyComparator.INSTANCE);
    private final ClassMeta<T> classMeta;
    private final MapperConfig<CsvColumnKey, Row> mapperConfig;
    private final ContextualGetterFactory<? super Row, CsvColumnKey> getterFactory;


    public DynamicSheetMapper(
            ClassMeta<T> classMeta,
            MapperConfig<CsvColumnKey, Row> mapperConfig,
            GetterFactory<? super Row, CsvColumnKey> getterFactory) {
        this(classMeta, mapperConfig, new ContextualGetterFactoryAdapter<Row, CsvColumnKey>(getterFactory));
    }


    public DynamicSheetMapper(
            ClassMeta<T> classMeta,
            MapperConfig<CsvColumnKey, Row> mapperConfig,
            ContextualGetterFactory<? super Row, CsvColumnKey> getterFactory) {
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
    public Enumerable<T> enumerate(Sheet sheet) {
        return getPoiMapper(startRow, sheet).enumerate(sheet);
    }

    @Override
    public Enumerable<T> enumerate(int startRow, Sheet sheet) {
        return getPoiMapper(startRow, sheet).enumerate(startRow, sheet);
    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH forEach(Sheet sheet, RH consumer) {
        return forEach(startRow, sheet, consumer);
    }

    @Override
    public <RH extends CheckedConsumer<? super T>> RH forEach(int startRow, Sheet sheet, RH consumer) {
        return getPoiMapper(startRow, sheet).forEach(startRow + 1, sheet, consumer);
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

        return getPoiMapper(new MapperKey<CsvColumnKey>(keys.toArray(new CsvColumnKey[0])));


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
                builder.addMapping(k, FieldMapperColumnDefinition.<CsvColumnKey>identity());
            }

            mapper = builder.mapper();

            mapperCache.add(key, mapper);
        }
        return mapper;
    }

}
