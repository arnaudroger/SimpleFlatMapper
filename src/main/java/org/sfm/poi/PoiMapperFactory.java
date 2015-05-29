package org.sfm.poi;

import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.AbstractMapperFactory;
import org.sfm.map.GetterFactory;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.FieldMapperColumnDefinitionProviderImpl;
import org.sfm.map.impl.MapperConfig;
import org.sfm.poi.impl.RowGetterFactory;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;

public class PoiMapperFactory extends AbstractMapperFactory<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>, PoiMapperFactory> {

    private GetterFactory<Row, CsvColumnKey> getterFactory = new RowGetterFactory();

    public static PoiMapperFactory newInstance() {
        return new PoiMapperFactory();
    }

    private PoiMapperFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<CsvColumnKey, Row>(), FieldMapperColumnDefinition.<CsvColumnKey, Row>identity());
    }

    public PoiMapperFactory getterFactory(GetterFactory<Row, CsvColumnKey> getterFactory) {
        this.getterFactory = getterFactory;
        return this;
    }

    public <T> PoiMapperBuilder<T> newBuilder(Class<T> type) {
        return newBuilder((Type)type);
    }

    public <T> PoiMapperBuilder<T> newBuilder(TypeReference<T> type) {
        return newBuilder(type.getType());
    }

    public <T> PoiMapperBuilder<T> newBuilder(Type type) {
        MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, Row>> mapperConfig = mapperConfig();
        ClassMeta<T> classMeta = getClassMeta(type);
        return  new PoiMapperBuilder<T>(classMeta, mapperConfig, getterFactory);
    }


}
