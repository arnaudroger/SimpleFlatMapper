package org.simpleflatmapper.test.map.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.IgnoreMapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.PropertyNameMatcherFactory;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.reflect.DefaultReflectionService;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.DefaultPropertyNameMatcherFactory;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.map.property.RenameProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.simpleflatmapper.map.MapperConfig.NO_ASM_MAPPER_THRESHOLD;

public class AbstractMapperFactoryTest {


    @Test
    public void testDefaultMapperConfig() {
        MapperFactory mapperFactory = new MapperFactory();

        MapperConfig<SampleFieldKey, Object[]> mapperConfig = mapperFactory.mapperConfig();

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

        assertTrue(mapperConfig.consumerErrorHandler() instanceof RethrowConsumerErrorHandler);

        assertEquals(MapperConfig.MAX_METHOD_SIZE, mapperConfig.maxMethodSize());

        assertTrue(mapperConfig.propertyNameMatcherFactory() instanceof DefaultPropertyNameMatcherFactory);

        assertTrue(mapperFactory.getReflectionService().isAsmActivated());

    }


    @Test
    public void testErrorHandlers() {
        MapperFactory mapperFactory = new MapperFactory();

        FieldMapperErrorHandler<SampleFieldKey> fieldMapperErrorHandler = new FieldMapperErrorHandler<SampleFieldKey>() {
            @Override
            public void errorMappingField(SampleFieldKey key, Object source, Object target, Exception error, Context mappingContext) throws MappingException {
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
        ConsumerErrorHandler consumerErrorHandler = new ConsumerErrorHandler() {
            @Override
            public void handlerError(Throwable error, Object target) {
            }
        };

        mapperFactory.fieldMapperErrorHandler(fieldMapperErrorHandler);
        mapperFactory.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
        mapperFactory.consumerErrorHandler(consumerErrorHandler);


        MapperConfig<SampleFieldKey, Object[]> mapperConfig = mapperFactory.mapperConfig();

        assertEquals(fieldMapperErrorHandler, mapperConfig.fieldMapperErrorHandler());
        assertEquals(mapperBuilderErrorHandler, mapperConfig.mapperBuilderErrorHandler());
        assertEquals(consumerErrorHandler, mapperConfig.consumerErrorHandler());
        Assert.assertEquals(consumerErrorHandler, mapperFactory.consumerErrorHandler());

    }

    @Test
    public void testPropertyNameMatcherFactory() {
        PropertyNameMatcherFactory factory = new PropertyNameMatcherFactory() {
            @Override
            public PropertyNameMatcher newInstance(FieldKey<?> key) {
                return null;
            }
        };
        assertSame(factory, new MapperFactory().propertyNameMatcherFactory(factory).mapperConfig().propertyNameMatcherFactory());
    }

    @Test
    public void testAsmSettings() {
        ReflectionService reflectionService = new MapperFactory().useAsm(false).getReflectionService();

        assertFalse(reflectionService.isAsmActivated());


        MapperConfig<SampleFieldKey, Object[]> mapperConfig = new MapperFactory()
                .asmMapperNbFieldsLimit(33)
                .failOnAsm(true)
                .maxMethodSize(13)
                .mapperConfig();

        assertEquals(33, mapperConfig.asmMapperNbFieldsLimit());
        assertEquals(13, mapperConfig.maxMethodSize());
        assertEquals(true, mapperConfig.failOnAsm());

        reflectionService = new DefaultReflectionService(null);

        assertSame(reflectionService, new MapperFactory().reflectionService(reflectionService).getReflectionService());

    }

    @Test
    public void testColumnDefinition() {
        MapperFactory mapperFactory = new MapperFactory();

        Object prop1 = new Object();
        Object prop2 = new Object();

        mapperFactory.addColumnDefinition("mykey", FieldMapperColumnDefinition.<SampleFieldKey>identity().add(prop1));

        Assert.assertArrayEquals(new Object[] {prop1}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("mykey", 0)).properties());

        mapperFactory.addColumnDefinition(new Predicate<SampleFieldKey>() {
            @Override
            public boolean test(SampleFieldKey sampleFieldKey) {
                return sampleFieldKey.getIndex() < 1;
            }
        }, FieldMapperColumnDefinition.<SampleFieldKey>identity().add(prop2));

        Assert.assertArrayEquals(new Object[] {prop2, prop1}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("mykey", 0)).properties());
        Assert.assertArrayEquals(new Object[] {prop1}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("mykey", 1)).properties());

    }

    @Test
    public void testColumnProperty() {
        MapperFactory mapperFactory = new MapperFactory();

        Object prop1 = new Object();
        Object prop2 = new Object();
        final Object prop3 = new Object();

        mapperFactory.addColumnProperty("mykey", prop1);

        mapperFactory.addColumnProperty(new Predicate<SampleFieldKey>() {
            @Override
            public boolean test(SampleFieldKey sampleFieldKey) {
                return sampleFieldKey.getIndex() < 1;
            }
        }, prop2);

        mapperFactory.addColumnProperty(new Predicate<SampleFieldKey>() {
            @Override
            public boolean test(SampleFieldKey sampleFieldKey) {
                return sampleFieldKey.getIndex() < 1;
            }
        }, new UnaryFactory<SampleFieldKey, Object>() {
            @Override
            public Object newInstance(SampleFieldKey sampleFieldKey) {
                return prop3;
            }
        });

        Assert.assertArrayEquals(new Object[] {prop3, prop2, prop1}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("mykey", 0)).properties());
        Assert.assertArrayEquals(new Object[] {prop1}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("mykey", 1)).properties());
    }


    @Test
    public void testAliases() {
        MapperFactory mapperFactory = new MapperFactory();

        mapperFactory.addAlias("a", "aa");
        mapperFactory.addAliases(new HashMap<String, String>() {{
            put("b", "bb");
            put("c", "cc");
        }});

        Assert.assertArrayEquals(new Object[] {new RenameProperty("aa")}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("a", 0)).properties());
        Assert.assertArrayEquals(new Object[] {new RenameProperty("bb")}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("b", 0)).properties());
        Assert.assertArrayEquals(new Object[] {new RenameProperty("cc")}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("c", 0)).properties());
        Assert.assertArrayEquals(new Object[] {}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("d", 0)).properties());

    }

    @Test
    public void testKeys() {
        MapperFactory mapperFactory = new MapperFactory();

        mapperFactory.addKeys("a", "b");

        Assert.assertArrayEquals(new Object[] {KeyProperty.DEFAULT}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("a", 0)).properties());
        Assert.assertArrayEquals(new Object[] {KeyProperty.DEFAULT}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("b", 0)).properties());
        Assert.assertArrayEquals(new Object[] {}, mapperFactory.columnDefinitions().getColumnDefinition(new SampleFieldKey("c", 0)).properties());

    }

    @Test
    public void testCopyConstructor() {
        MapperFactory mapperFactory = new MapperFactory();
        MapperFactory mapperFactory2 = new MapperFactory(mapperFactory);

        Assert.assertEquals(mapperFactory.columnDefinitions(), mapperFactory2.columnDefinitions());
        Assert.assertEquals(mapperFactory.getReflectionService().isAsmActivated(), mapperFactory2.getReflectionService().isAsmActivated());
        Assert.assertEquals(mapperFactory.mapperConfig().failOnAsm(), mapperFactory2.mapperConfig().failOnAsm());
        Assert.assertEquals(mapperFactory.mapperConfig().asmMapperNbFieldsLimit(), mapperFactory2.mapperConfig().asmMapperNbFieldsLimit());
        Assert.assertEquals(mapperFactory.mapperConfig().propertyNameMatcherFactory(), mapperFactory2.mapperConfig().propertyNameMatcherFactory());
        Assert.assertEquals(mapperFactory.mapperConfig().maxMethodSize(), mapperFactory2.mapperConfig().maxMethodSize());


    }
    @Test
    public void testMeta() {
        MapperFactory mapperFactory = new MapperFactory();

        ClassMeta<String> stringClassMeta = mapperFactory.getClassMeta(String.class);

        Assert.assertEquals(stringClassMeta, mapperFactory.getClassMeta((Type)String.class));
        Assert.assertEquals(stringClassMeta, mapperFactory.getClassMeta(new TypeReference<String>() {
        }));


        assertNotNull(mapperFactory.getClassMetaWithExtraInstantiator(String.class, null));
        assertNotNull(mapperFactory.getClassMetaWithExtraInstantiator((Type)String.class, null));
        assertNotNull( mapperFactory.getClassMetaWithExtraInstantiator(new TypeReference<String>() {
        }, null));

    }

    static class MapperFactory extends AbstractMapperFactory<SampleFieldKey, MapperFactory, Object[]> {
        public MapperFactory() {
            super(new FieldMapperColumnDefinitionProviderImpl<SampleFieldKey>(), FieldMapperColumnDefinition.<SampleFieldKey>identity(), new ContextualGetterFactoryAdapter<Object[], SampleFieldKey>(new GetterFactory<Object[], SampleFieldKey>() {
                @Override
                public <P> Getter<Object[], P> newGetter(Type target, SampleFieldKey key, Object... properties) {
                    return null;
                }
            }));
        }

        public MapperFactory(AbstractMapperFactory<SampleFieldKey, ?, Object[]> config) {
            super(config);
        }
    }
}