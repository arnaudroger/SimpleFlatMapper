package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.MapperBuilder;
import org.simpleflatmapper.map.mapper.SetRowMapperBuilderImpl;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.poi.impl.JoinSheetMapper;
import org.simpleflatmapper.poi.impl.StaticSheetMapper;
import org.simpleflatmapper.poi.impl.TransformRowMapper;
import org.simpleflatmapper.poi.impl.UnorderedJoinSheetMapper;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.poi.impl.CsvColumnKeyRowKeySourceGetter;
import org.simpleflatmapper.poi.impl.RowGetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.List;

public class SheetMapperBuilder<T> extends MapperBuilder<Row, Sheet, T, CsvColumnKey, RuntimeException, RowMapper<T>, RowMapper<T>, SheetMapperBuilder<T>> {

    public static final MapperSourceImpl<Row, CsvColumnKey> FIELD_MAPPER_SOURCE =
            new MapperSourceImpl<Row, CsvColumnKey>(Row.class, new ContextualGetterFactoryAdapter<Row, CsvColumnKey>(new RowGetterFactory()));
    private static final KeyFactory<CsvColumnKey> KEY_FACTORY = new KeyFactory<CsvColumnKey>() {
        @Override
        public CsvColumnKey newKey(String name, int i) {
            return new CsvColumnKey(name, i);
        }
    };
    public static final Function<Object[], ColumnDefinition<CsvColumnKey, ?>> COLUMN_DEFINITION_FACTORY = FieldMapperColumnDefinition.factory();


    public SheetMapperBuilder(ClassMeta<T> classMeta,
                              MapperConfig<CsvColumnKey, Row> mapperConfig,
                              GetterFactory<? super Row, CsvColumnKey> getterFactory) {
        this(classMeta, mapperConfig, new ContextualGetterFactoryAdapter<Row, CsvColumnKey>(getterFactory));
    }
    public SheetMapperBuilder(ClassMeta<T> classMeta,
                MapperConfig<CsvColumnKey, Row> mapperConfig,
                ContextualGetterFactory<? super Row, CsvColumnKey> getterFactory) {        super(KEY_FACTORY,
                new SetRowMapperBuilderImpl<RowMapper<T> , Row, Sheet, T, CsvColumnKey, RuntimeException>(
                        classMeta,
                        new MappingContextFactoryBuilder<Row, CsvColumnKey>(CsvColumnKeyRowKeySourceGetter.INSTANCE, !mapperConfig.unorderedJoin()),
                        mapperConfig,
                        FIELD_MAPPER_SOURCE.getterFactory(getterFactory),
                        KEY_FACTORY,
                        new UnaryFactory<Sheet, Enumerable<Row>>() {
                            @Override
                            public Enumerable<Row> newInstance(Sheet rows) {
                                throw new UnsupportedOperationException();
                            }
                        },
                        new RowMapperFactory<T>(),
                        CsvColumnKeyRowKeySourceGetter.INSTANCE),  new BiFunction<RowMapper<T>, List<CsvColumnKey>, RowMapper<T>>() {
                    @Override
                    public RowMapper<T> apply(RowMapper<T> setRowMapper, List<CsvColumnKey> keys) {
                        return setRowMapper;
                    }
                }, COLUMN_DEFINITION_FACTORY, 0
                
        );
    }

    private static class RowMapperFactory<T> implements SetRowMapperBuilderImpl.SetRowMapperFactory<RowMapper<T>, Row, Sheet, T, RuntimeException> {
        @Override
        public RowMapper<T> newJoinMapper(ContextualSourceFieldMapper<Row, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory, UnaryFactory<Sheet, Enumerable<Row>> enumerableFactory) {
            return new JoinSheetMapper<T>(mapper, consumerErrorHandler, mappingContextFactory);
        }

        @Override
        public RowMapper<T> newUnorderedJoinMapper(ContextualSourceFieldMapper<Row, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory, UnaryFactory<Sheet, Enumerable<Row>> enumerableFactory) {
            return new UnorderedJoinSheetMapper<T>(mapper, consumerErrorHandler, mappingContextFactory);
        }

        @Override
        public RowMapper<T> newStaticMapper(ContextualSourceFieldMapper<Row, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super Row> mappingContextFactory, UnaryFactory<Sheet, Enumerable<Row>> enumerableFactory) {
            return new StaticSheetMapper<T>(mapper, consumerErrorHandler, mappingContextFactory);
        }

        @Override
        public <I> RowMapper<T> newTransformer(SetRowMapper<Row, Sheet, I, RuntimeException> setRowMapper, Function<I, T> transform) {
            return new TransformRowMapper((RowMapper<T>)setRowMapper, transform);
        }
    }
}
