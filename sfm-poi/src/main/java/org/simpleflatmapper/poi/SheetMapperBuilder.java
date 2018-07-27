package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.mapper.MapperBuilder;
import org.simpleflatmapper.map.mapper.SetRowMapperBuilderImpl;
import org.simpleflatmapper.poi.impl.JoinSheetMapper;
import org.simpleflatmapper.poi.impl.StaticSheetMapper;
import org.simpleflatmapper.poi.impl.TransformRowMapper;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.poi.impl.CsvColumnKeyRowKeySourceGetter;
import org.simpleflatmapper.poi.impl.RowGetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.UnaryFactory;

public class SheetMapperBuilder<T> extends MapperBuilder<Row, Sheet, T, CsvColumnKey, RuntimeException, RowMapper<T>, RowMapper<T>, SheetMapperBuilder<T>> {

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
        super(KEY_FACTORY, 
                new SetRowMapperBuilderImpl<RowMapper<T> , Row, Sheet, T, CsvColumnKey, RuntimeException>(
                        classMeta,
                        new MappingContextFactoryBuilder<Row, CsvColumnKey>(new CsvColumnKeyRowKeySourceGetter()),
                        mapperConfig,
                        FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                        KEY_FACTORY,
                        new UnaryFactory<Sheet, Enumerable<Row>>() {
                            @Override
                            public Enumerable<Row> newInstance(Sheet rows) {
                                throw new UnsupportedOperationException();
                            }
                        },
                        new RowMapperFactory<T>()
                ),  new Function<RowMapper<T>, RowMapper<T>>() {
                    @Override
                    public RowMapper<T> apply(RowMapper<T> setRowMapper) {
                        return setRowMapper;
                    }
                }, 0
                
        );
    }

    private static class RowMapperFactory<T> implements SetRowMapperBuilderImpl.SetRowMapperFactory<RowMapper<T>, Row, Sheet, T, RuntimeException> {
        @Override
        public RowMapper<T> newJoinMapper(SourceFieldMapper<Row, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory, UnaryFactory<Sheet, Enumerable<Row>> enumerableFactory) {
            return new JoinSheetMapper<T>(mapper, consumerErrorHandler, mappingContextFactory);
        }

        @Override
        public RowMapper<T> newStaticMapper(SourceFieldMapper<Row, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory, UnaryFactory<Sheet, Enumerable<Row>> enumerableFactory) {
            return new StaticSheetMapper<T>(mapper, consumerErrorHandler, mappingContextFactory);
        }

        @Override
        public <I> RowMapper<T> newTransformer(SetRowMapper<Row, Sheet, I, RuntimeException> setRowMapper, Function<I, T> transform) {
            return new TransformRowMapper((RowMapper<T>)setRowMapper, transform);
        }
    }
}
