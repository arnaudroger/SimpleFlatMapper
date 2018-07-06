package org.simpleflatmapper.poi;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.mapper.MapperBuilder;
import org.simpleflatmapper.poi.impl.JoinSheetMapper;
import org.simpleflatmapper.poi.impl.RowEnumerable;
import org.simpleflatmapper.poi.impl.StaticSheetMapper;
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
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.Iterator;
import java.util.stream.Stream;

public class SheetMapperBuilder<T> extends MapperBuilder<Row, Sheet, T, CsvColumnKey, RuntimeException, RowMapper<T>,  SheetMapperBuilder<T>> {

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
                KEY_FACTORY, new UnaryFactory<Sheet, Enumerable<Row>>() {
                    @Override
                    public Enumerable<Row> newInstance(Sheet rows) {
                        throw new UnsupportedOperationException();
                    }
                }, new Function<SetRowMapper<Row, Sheet, T, RuntimeException>, RowMapper<T>>() {
                    @Override
                    public RowMapper<T> apply(SetRowMapper<Row, Sheet, T, RuntimeException> setRowMapper) {
                        throw new UnsupportedOperationException();
                    }
                }, 0);
        
        
    }


    @Override
    public RowMapper<T> mapper() {
        SourceFieldMapper<Row, T> fieldMapper = setRowMapperBuilder.sourceFieldMapper();
        
        if (setRowMapperBuilder.hasJoin()) {
            return new JoinSheetMapper<T>(fieldMapper, setRowMapperBuilder.mapperConfig().consumerErrorHandler(), setRowMapperBuilder.getMappingContextFactoryBuilder().newFactory());
        } else {
            return new StaticSheetMapper<T>(fieldMapper, setRowMapperBuilder.mapperConfig().consumerErrorHandler(), setRowMapperBuilder.getMappingContextFactoryBuilder().newFactory());
        }
    }
}
