package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.UnaryFactory;

public class DefaultSetRowMapperBuilder<ROW, SET, T, K extends FieldKey<K>, E extends Exception> 
        extends SetRowMapperBuilderImpl<SetRowMapper<ROW, SET, T, E>, ROW, SET, T, K, E> {
    /**
     * @param classMeta           the meta for the target class.
     * @param parentBuilder       the parent builder, null if none.
     * @param mapperConfig        the mapperConfig.
     * @param mapperSource        the Mapper source.
     * @param keyFactory
     * @param enumerableFactory
     */
    public DefaultSetRowMapperBuilder(
            ClassMeta<T> classMeta,
            MappingContextFactoryBuilder<? super ROW, K> parentBuilder, 
            MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig, MapperSource<? super ROW, K> mapperSource, 
            KeyFactory<K> keyFactory, UnaryFactory<SET, Enumerable<ROW>> enumerableFactory) {
        super(
                classMeta, 
                parentBuilder, 
                mapperConfig,
                mapperSource, 
                keyFactory, 
                enumerableFactory,
                new DefaultSetRowMapperFactory<ROW, SET, T, E>());
    }


    public static class DefaultSetRowMapperFactory<ROW, SET, T , E extends Exception> implements SetRowMapperFactory<SetRowMapper<ROW, SET, T, E>, ROW, SET, T, E> {

        @Override
        public SetRowMapper<ROW, SET, T, E> newJoinMapper(SourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<SET, Enumerable<ROW>> enumerableFactory) {
            return  new JoinMapper<ROW, SET, T, E>(mapper, consumerErrorHandler,mappingContextFactory, enumerableFactory);
        }

        @Override
        public SetRowMapper<ROW, SET, T, E> newStaticMapper(SourceFieldMapper<ROW, T> mapper, ConsumerErrorHandler consumerErrorHandler, MappingContextFactory<? super ROW> mappingContextFactory, UnaryFactory<SET, Enumerable<ROW>> enumerableFactory) {
            return  new StaticSetRowMapper<ROW, SET, T, E>(mapper, consumerErrorHandler,mappingContextFactory, enumerableFactory);
        }

        @Override
        public <I> SetRowMapper<ROW, SET, T, E> newTransformer(SetRowMapper<ROW, SET, I, E> setRowMapper, Function<I, T> transform) {
            return new TransformSetRowMapper<ROW, SET, I, T, E>(setRowMapper, transform);
        }
    }
}
