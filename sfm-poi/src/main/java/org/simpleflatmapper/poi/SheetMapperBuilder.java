package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.map.mapper.AbstractMapperBuilder;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.poi.impl.JoinSheetMapper;
import org.simpleflatmapper.poi.impl.CsvColumnKeyRowKeySourceGetter;
import org.simpleflatmapper.poi.impl.RowGetterFactory;
import org.simpleflatmapper.poi.impl.StaticSheetMapper;
import org.simpleflatmapper.reflect.meta.ClassMeta;

public class SheetMapperBuilder<T> extends AbstractMapperBuilder<Row, T, CsvColumnKey, RowMapper<T>,  SheetMapperBuilder<T>> {

    public static final MapperSourceImpl<Row, CsvColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<Row, CsvColumnKey>(Row.class, new RowGetterFactory());
    private static final KeyFactory<CsvColumnKey> KEY_FACTORY = new KeyFactory<CsvColumnKey>() {
        @Override
        public CsvColumnKey newKey(String name, int i) {
            return new CsvColumnKey(name, i);
        }
    };


    public SheetMapperBuilder(ClassMeta<T> classMeta,
                              MapperConfig<CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey>> mapperConfig,
                              GetterFactory<Row, CsvColumnKey> getterFactory) {
        super(classMeta, new MappingContextFactoryBuilder<Row, CsvColumnKey>(new CsvColumnKeyRowKeySourceGetter()),
                mapperConfig, FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                KEY_FACTORY, 0);
    }

    @Override
    protected RowMapper<T> newJoinMapper(Mapper<Row, T> mapper) {
        return new JoinSheetMapper<T>(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    @Override
    protected RowMapper<T> newStaticMapper(Mapper<Row, T> mapper) {
        return  new StaticSheetMapper<T>(mapper, mapperConfig.consumerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }
}
