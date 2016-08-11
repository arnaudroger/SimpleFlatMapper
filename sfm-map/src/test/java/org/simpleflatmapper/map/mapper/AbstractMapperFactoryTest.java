package org.simpleflatmapper.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.RowHandlerErrorHandler;
import org.simpleflatmapper.map.SampleFieldKey;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.error.RethrowRowHandlerErrorHandler;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.meta.ObjectClassMeta;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.simpleflatmapper.map.MapperConfig.NO_ASM_MAPPER_THRESHOLD;

public class AbstractMapperFactoryTest {


    @Test
    public void testDefaultMapperConfig() {
        MapperFactory mapperFactory = new MapperFactory();

        MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> mapperConfig = mapperFactory.mapperConfig();

        final ArrayList<Object> properties = new ArrayList<Object>();
        mapperConfig.columnDefinitions().forEach(Object.class, new BiConsumer<Predicate<? super SampleFieldKey>, Object>() {
            @Override
            public void accept(Predicate<? super SampleFieldKey> predicate, Object o) {
                properties.add(o);
            }
        });

        assertTrue(properties.isEmpty());
        assertFalse(mapperConfig.failOnAsm());


        assertEquals(NO_ASM_MAPPER_THRESHOLD, mapperConfig.asmMapperNbFieldsLimit());
        assertNull(mapperConfig.fieldMapperErrorHandler());
        assertFalse(mapperConfig.hasFieldMapperErrorHandler());

        assertTrue(mapperConfig.mapperBuilderErrorHandler() instanceof RethrowMapperBuilderErrorHandler);

        assertTrue(mapperConfig.rowHandlerErrorHandler() instanceof RethrowRowHandlerErrorHandler);

        assertEquals(MapperConfig.MAX_METHOD_SIZE, mapperConfig.maxMethodSize());

        assertTrue(mapperConfig.propertyNameMatcherFactory() instanceof  DefaultPropertyNameMatcherFactory);

        assertTrue(mapperFactory.getReflectionService().isAsmPresent());
        assertTrue(mapperFactory.getReflectionService().isAsmActivated());

    }


    @Test
    public void testErrorHandlers() {
        MapperFactory mapperFactory = new MapperFactory();

        FieldMapperErrorHandler<SampleFieldKey> fieldMapperErrorHandler = new FieldMapperErrorHandler<SampleFieldKey>() {
            @Override
            public void errorMappingField(SampleFieldKey key, Object source, Object target, Exception error) throws MappingException {
            }
        };
        MapperBuilderErrorHandler mapperBuilderErrorHandler = new MapperBuilderErrorHandler() {
            @Override
            public void accessorNotFound(String msg) {
            }

            @Override
            public void propertyNotFound(Type target, String property) {
            }

            @Override
            public void customFieldError(FieldKey<?> key, String message) {
            }
        };
        RowHandlerErrorHandler rowHandlerErrorHandler = new RowHandlerErrorHandler() {
            @Override
            public void handlerError(Throwable error, Object target) {
            }
        };

        mapperFactory.fieldMapperErrorHandler(fieldMapperErrorHandler);
        mapperFactory.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
        mapperFactory.rowHandlerErrorHandler(rowHandlerErrorHandler);


        MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> mapperConfig = mapperFactory.mapperConfig();

        assertEquals(fieldMapperErrorHandler, mapperConfig.fieldMapperErrorHandler());
        assertEquals(mapperBuilderErrorHandler, mapperConfig.mapperBuilderErrorHandler());
        assertEquals(rowHandlerErrorHandler, mapperConfig.rowHandlerErrorHandler());
    }


    static class MapperFactory extends AbstractMapperFactory<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>, MapperFactory> {
        public MapperFactory() {
            super(new FieldMapperColumnDefinitionProviderImpl<SampleFieldKey>(), FieldMapperColumnDefinition.<SampleFieldKey>identity());
        }
    }
}