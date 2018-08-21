package org.simpleflatmapper.poi;

import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.poi.impl.DynamicSheetMapper;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.poi.impl.RowGetterFactory;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.csv.CsvColumnKey;

import java.lang.reflect.Type;

public class SheetMapperFactory extends AbstractMapperFactory<CsvColumnKey, SheetMapperFactory> {

    private GetterFactory<Row, CsvColumnKey> getterFactory = new RowGetterFactory();

    /**
     *
     * @return new newInstance of factory
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
     * @return the newInstance
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
        MapperConfig<CsvColumnKey> mapperConfig = mapperConfig();
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
        MapperConfig<CsvColumnKey> mapperConfig = mapperConfig();
        return new DynamicSheetMapper<T>(classMeta, mapperConfig, getterFactory);
    }
}
