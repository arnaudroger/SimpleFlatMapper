package org.sfm.poi;

import org.apache.poi.ss.usermodel.Row;
import org.sfm.map.mapper.AbstractMapperFactory;
import org.sfm.map.GetterFactory;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.sfm.map.MapperConfig;
import org.sfm.poi.impl.DynamicSheetMapper;
import org.sfm.poi.impl.RowGetterFactory;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.simpleflatmapper.csv.CsvColumnKey;

import java.lang.reflect.Type;

public class SheetMapperFactory extends AbstractMapperFactory<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>, SheetMapperFactory> {

    private GetterFactory<Row, CsvColumnKey> getterFactory = new RowGetterFactory();

    /**
     *
     * @return new instance of factory
     */
    public static SheetMapperFactory newInstance() {
        return new SheetMapperFactory();
    }

    private SheetMapperFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<CsvColumnKey>(), FieldMapperColumnDefinition.<CsvColumnKey>identity());
    }

    /**
     * set a new getterFactory.
     * @param getterFactory the getterFactory
     * @return the instance
     */
    public SheetMapperFactory getterFactory(GetterFactory<Row, CsvColumnKey> getterFactory) {
        this.getterFactory = getterFactory;
        return this;
    }

    /**
     *
     * @param type the type to map
     * @param <T> the type to map
     * @return a builder on the specified type
     */
    public <T> SheetMapperBuilder<T> newBuilder(Class<T> type) {
        return newBuilder((Type)type);
    }

    /**
     *
     * @param type the type to map
     * @param <T> the type to map
     * @return a builder on the specified type
     */
    public <T> SheetMapperBuilder<T> newBuilder(TypeReference<T> type) {
        return newBuilder(type.getType());
    }

    /**
     *
     * @param type the type to map
     * @param <T> the type to map
     * @return a builder on the specified type
     */
    public <T> SheetMapperBuilder<T> newBuilder(Type type) {
        MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> mapperConfig = mapperConfig();
        ClassMeta<T> classMeta = getClassMeta(type);
        return  new SheetMapperBuilder<T>(classMeta, mapperConfig, getterFactory);
    }

    /**
     *
     * @param type the type to map
     * @param <T> the type to map
     * @return a dynamic mapper on the specified type
     */
    public <T> SheetMapper<T> newMapper(Class<T> type) {
        return newMapper((Type)type);
    }

    /**
     *
     * @param type the type to map
     * @param <T> the type to map
     * @return a dynamic mapper on the specified type
     */
    public <T> SheetMapper<T> newMapper(TypeReference<T> type) {
        return newMapper(type.getType());
    }

    /**
     *
     * @param type the type to map
     * @param <T> the type to map
     * @return a dynamic mapper on the specified type
     */
    public <T> SheetMapper<T> newMapper(Type type) {
        ClassMeta<T> classMeta = getClassMeta(type);
        MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> mapperConfig = mapperConfig();
        return new DynamicSheetMapper<T>(classMeta, mapperConfig, getterFactory);
    }
}
