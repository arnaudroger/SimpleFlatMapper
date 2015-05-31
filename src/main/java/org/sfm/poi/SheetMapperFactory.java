package org.sfm.poi;

import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.AbstractMapperFactory;
import org.sfm.map.GetterFactory;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.FieldMapperColumnDefinitionProviderImpl;
import org.sfm.map.impl.MapperConfig;
import org.sfm.poi.impl.DynamicSheetMapper;
import org.sfm.poi.impl.RowGetterFactory;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;

public class SheetMapperFactory extends AbstractMapperFactory<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>, SheetMapperFactory> {

    private GetterFactory<Row, CsvColumnKey> getterFactory = new RowGetterFactory();

    public static SheetMapperFactory newInstance() {
        return new SheetMapperFactory();
    }

    private SheetMapperFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<CsvColumnKey, Row>(), FieldMapperColumnDefinition.<CsvColumnKey, Row>identity());
    }

    public SheetMapperFactory getterFactory(GetterFactory<Row, CsvColumnKey> getterFactory) {
        this.getterFactory = getterFactory;
        return this;
    }

    public <T> SheetMapperBuilder<T> newBuilder(Class<T> type) {
        return newBuilder((Type)type);
    }

    public <T> SheetMapperBuilder<T> newBuilder(TypeReference<T> type) {
        return newBuilder(type.getType());
    }

    public <T> SheetMapperBuilder<T> newBuilder(Type type) {
        MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>> mapperConfig = mapperConfig();
        ClassMeta<T> classMeta = getClassMeta(type);
        return  new SheetMapperBuilder<T>(classMeta, mapperConfig, getterFactory);
    }

    public <T> SheetMapper<T> newMapper(Class<T> type) {
        return newMapper((Type)type);
    }

    public <T> SheetMapper<T> newMapper(TypeReference<T> type) {
        return newMapper(type.getType());
    }

    public <T> SheetMapper<T> newMapper(Type type) {
        return new DynamicSheetMapper<T>(getClassMeta(type), mapperConfig(), getterFactory);
    }
}
