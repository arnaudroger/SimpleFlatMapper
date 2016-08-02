package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.core.map.mapper.AbstractMapperBuilder;
import org.simpleflatmapper.core.map.GetterFactory;
import org.simpleflatmapper.core.map.Mapper;
import org.simpleflatmapper.core.map.MapperConfig;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.core.map.mapper.KeyFactory;
import org.simpleflatmapper.core.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.poi.impl.JoinSheetMapper;
import org.simpleflatmapper.poi.impl.CsvColumnKeyRowKeySourceGetter;
import org.simpleflatmapper.poi.impl.RowGetterFactory;
import org.simpleflatmapper.poi.impl.StaticSheetMapper;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;

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
    protected CsvColumnKey key(String column, int index) {
        return new CsvColumnKey(column, index);
    }

    @Override
    protected RowMapper<T> newJoinJdbcMapper(Mapper<Row, T> mapper) {
        return new JoinSheetMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }

    @Override
    protected RowMapper<T> newStaticJdbcMapper(Mapper<Row, T> mapper) {
        return  new StaticSheetMapper<T>(mapper, mapperConfig.rowHandlerErrorHandler(), mappingContextFactoryBuilder.newFactory());
    }
}
