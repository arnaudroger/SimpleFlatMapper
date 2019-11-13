package org.simpleflatmapper.test.map;


import org.junit.Test;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.IgnoreMapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.annotation.InferNull;
import org.simpleflatmapper.map.annotation.Key;
import org.simpleflatmapper.map.mapper.MapperImpl;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.mapper.PropertyMappingsBuilder;
import org.simpleflatmapper.map.property.InferNullProperty;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.map.property.OptionalProperty;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.ErrorDoc;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.TypeReference;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PropertyMappingsBuilderTest {


    public static final PropertyMappingsBuilder.PropertyPredicateFactory<SampleFieldKey> CONSTANT_PREDICATE = new PropertyMappingsBuilder.PropertyPredicateFactory<SampleFieldKey>() {
        @Override
        public PropertyFinder.PropertyFilter predicate(SampleFieldKey sampleFieldKey, Object[] objects, List<PropertyMappingsBuilder.AccessorNotFound> accessorNotFounds) {
            return PropertyFinder.PropertyFilter.trueFilter();
        }
    };

    public static class AnonymousElement {
        public List<String> phones;
    }

    @Test
    public void testStringInArray() {

        final ClassMeta<AnonymousElement> classMeta = ReflectionService.newInstance().getClassMeta(AnonymousElement.class);
        PropertyMappingsBuilder<AnonymousElement, SampleFieldKey> builder =
                defaultPropertyMappingBuilder(classMeta);

        builder.addProperty(new SampleFieldKey("phones_str", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());


        assertIsPhonesElement(builder);

    }

    private <T> PropertyMappingsBuilder<T, SampleFieldKey> defaultPropertyMappingBuilder(ClassMeta<T> classMeta) {
        return PropertyMappingsBuilder.<T, SampleFieldKey, Object[]>of(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig(), CONSTANT_PREDICATE);
    }


    //@Test causes issue with jdbc array
    public void testAnonymousInArray() {

        final ClassMeta<AnonymousElement> classMeta = ReflectionService.newInstance().getClassMeta(AnonymousElement.class);
        PropertyMappingsBuilder<AnonymousElement, SampleFieldKey> builder2 =
                defaultPropertyMappingBuilder(classMeta);

        builder2.addProperty(new SampleFieldKey("phones", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        assertIsPhonesElement(builder2);

    }

    @SuppressWarnings("unchecked")
    private void assertIsPhonesElement(PropertyMappingsBuilder<AnonymousElement, SampleFieldKey> builder) {
        final List<PropertyMapping<AnonymousElement, ?, SampleFieldKey>> props = new ArrayList<PropertyMapping<AnonymousElement, ?, SampleFieldKey>>();
        builder.forEachProperties(new ForEachCallBack<PropertyMapping<AnonymousElement, ?, SampleFieldKey>>() {
            @Override
            public void handle(PropertyMapping<AnonymousElement, ?, SampleFieldKey> pm) {
                props.add(pm);
            }
        });

        assertEquals(1, props.size());

        final PropertyMeta<AnonymousElement, ?> propertyMeta = props.get(0).getPropertyMeta();

        assertTrue("Is sub property", propertyMeta.isSubProperty());

        SubPropertyMeta<AnonymousElement, ?, ?> subPropertyMeta = (SubPropertyMeta<AnonymousElement, ?, ?>) propertyMeta;

        assertTrue(TypeHelper.isAssignable(List.class, subPropertyMeta.getOwnerProperty().getPropertyType()));
        assertTrue("expect ListElementPropertyMeta " + ((SubPropertyMeta)subPropertyMeta.getSubProperty()).getOwnerProperty(), ((SubPropertyMeta)subPropertyMeta.getSubProperty()).getOwnerProperty() instanceof ArrayElementPropertyMeta);
    }


    @Test
    public void testCustomSourceIncompatibility() {

        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyMappingsBuilder<DbObject, SampleFieldKey> builder2 =
                defaultPropertyMappingBuilder(classMeta);

        try {
            builder2.addProperty(new SampleFieldKey("id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity().addGetter(new Getter<Object, String>() {
                @Override
                public String get(Object target) throws Exception {
                    return null;
                }
            }));
            fail();
        } catch(MapperBuildingException e) {
        }
    }

    @Test
    public void testCustomSourceIncompatibilityIgnoreError() {

        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyMappingsBuilder<DbObject, SampleFieldKey> builder2 =
                PropertyMappingsBuilder.of(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().mapperBuilderErrorHandler(IgnoreMapperBuilderErrorHandler.INSTANCE), CONSTANT_PREDICATE);

        builder2.addProperty(new SampleFieldKey("id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity().addGetter(new Getter<Object, String>() {
            @Override
            public String get(Object target) throws Exception {
                return null;
            }
        }));
        builder2.addProperty(new SampleFieldKey("name", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        final List<PropertyMapping<DbObject, ?, SampleFieldKey>> props = getAllProperties(builder2);

        assertEquals(2, props.size());
        assertNull(props.get(0));
    }

    private List<PropertyMapping<DbObject, ?, SampleFieldKey>> getAllProperties(PropertyMappingsBuilder<DbObject, SampleFieldKey> builder2) {
        return builder2.currentProperties();
    }


    @Test
    public void testAddPropertyIfPresent() {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyMappingsBuilder<DbObject, SampleFieldKey> builder2 =
                defaultPropertyMappingBuilder(classMeta);


        List<PropertyMapping<DbObject, ?, SampleFieldKey>> props;

        builder2.addPropertyIfPresent(new SampleFieldKey("id", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());
        props = getAllProperties(builder2);

        assertEquals(1, props.size());
        assertNotNull(props.get(0));

        builder2.addPropertyIfPresent(new SampleFieldKey("not_id", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());
        props = getAllProperties(builder2);

        assertEquals(2, props.size());
        assertNotNull(props.get(0));
        assertNull(props.get(1));



    }

    @Test
    public void testAddFailedPropertyIgnoreError() {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyMappingsBuilder<DbObject, SampleFieldKey> builder2 =
                PropertyMappingsBuilder.of(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().mapperBuilderErrorHandler(IgnoreMapperBuilderErrorHandler.INSTANCE), CONSTANT_PREDICATE);

        builder2.addProperty(new SampleFieldKey("id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());
        builder2.addProperty(new SampleFieldKey("not_id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());
        List<PropertyMapping<DbObject, ?, SampleFieldKey>> props = getAllProperties(builder2);

        assertEquals(2, props.size());
        assertNull(props.get(1));
    }

    @Test
    public void testAddPropertyIgnore() {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyMappingsBuilder<DbObject, SampleFieldKey> builder2 =
                defaultPropertyMappingBuilder(classMeta);

        builder2.addProperty(new SampleFieldKey("not_id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity().addIgnore());
        List<PropertyMapping<DbObject, ?, SampleFieldKey>> props = getAllProperties(builder2);

        assertEquals(1, props.size());
        assertNull(props.get(0));
    }


    @Test
    public void testSelfPropertyInvalidation() {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);

        MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);

        PropertyMappingsBuilder<DbObject, SampleFieldKey> builder =
                PropertyMappingsBuilder.of(
                        classMeta,
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().mapperBuilderErrorHandler(errorHandler),
                        CONSTANT_PREDICATE);

        builder.addProperty(new SampleFieldKey("self", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        verify(errorHandler, never()).customFieldError(any(FieldKey.class), any(String.class));
        verify(errorHandler, never()).accessorNotFound(any(String.class));
        verify(errorHandler, never()).propertyNotFound(any(Type.class), any(String.class));

        builder.addProperty(new SampleFieldKey("id", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        verify(errorHandler).propertyNotFound(DbObject.class, "self");
    }

    @Test
    public void testAnnotationsKey() {

        final ClassMeta<ObjectWithAnnotation> classMeta = ReflectionService.newInstance().getClassMeta(ObjectWithAnnotation.class);
        PropertyMappingsBuilder<ObjectWithAnnotation, SampleFieldKey> builder2 =
                defaultPropertyMappingBuilder(classMeta);

        builder2.addProperty(new SampleFieldKey("id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        List<PropertyMapping<ObjectWithAnnotation, ?, SampleFieldKey>> propertyMappings = builder2.currentProperties();

        assertTrue(propertyMappings.get(0).getColumnDefinition().has(KeyProperty.class));
    }

    @Test
    public void testAnnotationsNull() {

        final ClassMeta<ObjectWithAnnotationNull> classMeta = ReflectionService.newInstance().getClassMeta(ObjectWithAnnotationNull.class);
        PropertyMappingsBuilder<ObjectWithAnnotationNull, SampleFieldKey> builder2 =
                defaultPropertyMappingBuilder(classMeta);

        builder2.addProperty(new SampleFieldKey("id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        List<PropertyMapping<ObjectWithAnnotationNull, ?, SampleFieldKey>> propertyMappings = builder2.currentProperties();

        assertTrue(propertyMappings.get(0).getColumnDefinition().has(InferNullProperty.class));
    }

    public static class ObjectWithAnnotation {
        @Key
        public int id;
    }

    public static class ObjectWithAnnotationNull {
        @InferNull
        public int id;
    }
    @Test
    public void test418() {
        ClassMeta<List<Tuple2<B, List<C>>>> classMeta2 = ReflectionService.newInstance().getClassMeta(new TypeReference<List<Tuple2<B, List<C>>>>() {}.getType());

        PropertyMappingsBuilder<List<Tuple2<B, List<C>>>, SampleFieldKey> builder2 =
                defaultPropertyMappingBuilder(classMeta2);

        PropertyMapping<List<Tuple2<B, List<C>>>, Object, SampleFieldKey> pm1 = builder2.addProperty(new SampleFieldKey("id", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());
        PropertyMapping<List<Tuple2<B, List<C>>>, Object, SampleFieldKey> pm2 = builder2.addProperty(new SampleFieldKey("id", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        assertEquals("[0].element1[0].id", pm2.getPropertyMeta().getPath());
    }
    

    public static class A {
        public int id;
    }
    public static class B {
        public int id;
    }
    public static class C {
        public int id;
    }

    // networks_network_ipv4 is map to networks[4] instead of networks[].network.ipv4
    @Test
    public void test431() {
        ClassMeta<FooN> classMeta =  ReflectionService.newInstance().getClassMeta(FooN.class);
        PropertyMappingsBuilder<FooN, SampleFieldKey> builder 
                = defaultPropertyMappingBuilder(classMeta);

        SampleFieldKey networks_network_ipv4 = new SampleFieldKey("networks_network_ipv4", 1);
        FieldMapperColumnDefinition<SampleFieldKey> identity = FieldMapperColumnDefinition.<SampleFieldKey>identity();
        PropertyMapping<FooN, Object, SampleFieldKey> propertyMapping = 
                builder.addProperty(networks_network_ipv4, identity);

        assertEquals("networks[0].network.ipv4", propertyMapping.getPropertyMeta().getPath());

    }
    
    public static class FooN {
        public List<BarN> networks;
    }
    
    public static class BarN {
        public ZooN network;
    }
    
    public static class ZooN {
        public InetAddress ipv4;
    }
    
    @Test
    public void test576OptionalProperty() {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);


        PropertyMappingsBuilder<DbObject, SampleFieldKey> builder =
                PropertyMappingsBuilder.of(
                        classMeta,
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig(),
                        CONSTANT_PREDICATE);

        FieldMapperColumnDefinition<SampleFieldKey> columnDefinition = FieldMapperColumnDefinition.<SampleFieldKey>identity().add(OptionalProperty.INSTANCE);
        PropertyMapping<DbObject, Object, SampleFieldKey> pName = builder.addProperty(new SampleFieldKey("name", 0), columnDefinition);
        assertEquals("name", pName.getPropertyMeta().getPath());
        PropertyMapping<DbObject, Object, SampleFieldKey> pNotThere = builder.addProperty(new SampleFieldKey("not_there", 0), columnDefinition);
        assertNull(pNotThere);

        builder =
                PropertyMappingsBuilder.of(
                        classMeta,
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig(),
                        CONSTANT_PREDICATE);

        PropertyMapping<DbObject, Object, SampleFieldKey> pNotThere1 = builder.addProperty(new SampleFieldKey("not_there", 0), columnDefinition);
        assertEquals("{this}", pNotThere1.getPropertyMeta().getPath());
        
        PropertyMapping<DbObject, Object, SampleFieldKey> pName1 = builder.addProperty(new SampleFieldKey("name", 0), columnDefinition);
        assertEquals("name", pName1.getPropertyMeta().getPath());
        
        assertNull(builder.currentProperties().get(0));

    }


    @Test
    public void testHandleMapperErrorSetterNotFound() throws NoSuchMethodException, SecurityException, IOException {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);

        MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);

        PropertyMappingsBuilder<DbObject, SampleFieldKey> builder =
                PropertyMappingsBuilder.of(
                        classMeta,
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().mapperBuilderErrorHandler(errorHandler),
                        CONSTANT_PREDICATE);

        FieldMapperColumnDefinition<SampleFieldKey> columnDefinition = FieldMapperColumnDefinition.<SampleFieldKey>identity();

        builder.addProperty(new SampleFieldKey("id", 1), columnDefinition);
        builder.addProperty(new SampleFieldKey("notthere1", 2), columnDefinition);

        verify(errorHandler).propertyNotFound(DbObject.class, "notthere1");


        builder.addProperty(new SampleFieldKey("notthere3", 3), columnDefinition);

        verify(errorHandler).propertyNotFound(DbObject.class, "notthere3");

    }

    public static class MyClass {
        public Foo prop;
    }
    @Test
    public void testHandleMapperErrorGetterNotFound() throws NoSuchMethodException, SecurityException, IOException {
        final ClassMeta<MyClass> classMeta = ReflectionService.newInstance().getClassMeta(MyClass.class);
        FieldMapperColumnDefinition<SampleFieldKey> columnDefinition = FieldMapperColumnDefinition.<SampleFieldKey>identity();

        MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);

        PropertyMappingsBuilder<MyClass, SampleFieldKey> builder =
                PropertyMappingsBuilder.of(
                        classMeta,
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().mapperBuilderErrorHandler(errorHandler),
                        new PropertyMappingsBuilder.PropertyPredicateFactory<SampleFieldKey>() {
                            @Override
                            public PropertyFinder.PropertyFilter predicate(final SampleFieldKey key, Object[] properties, final List<PropertyMappingsBuilder.AccessorNotFound> accessorNotFounds) {
                                return new PropertyFinder.PropertyFilter(new Predicate<PropertyMeta<?, ?>>() {

                                    @Override
                                    public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                        accessorNotFounds.add(new PropertyMappingsBuilder.AccessorNotFound(key, propertyMeta.getPath(), propertyMeta.getPropertyType(), ErrorDoc.CSFM_GETTER_NOT_FOUND, propertyMeta));
                                        return false;
                                    }
                                }, ConstantPredicate.<PropertyMeta<?, ?>>truePredicate());
                            }
                        });


        builder.addProperty(new SampleFieldKey("prop", 1), columnDefinition);


        verify(errorHandler).accessorNotFound("Could not find Getter for SampleFieldKey{name=prop, affinities=[], type=class java.lang.Object} returning type class org.simpleflatmapper.test.beans.Foo path prop. See https://github.com/arnaudroger/SimpleFlatMapper/wiki/Errors_CSFM_GETTER_NOT_FOUND");
    }
    
    @Test
    public void testSelfPropertyWithNoAccessorCallsPropertyNotFound602() {
        final ClassMeta<C602> classMeta = ReflectionService.newInstance().getClassMeta(C602.class);
        FieldMapperColumnDefinition<SampleFieldKey> columnDefinition = FieldMapperColumnDefinition.<SampleFieldKey>identity();

        MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);

        PropertyMappingsBuilder<C602, SampleFieldKey> builder =
                PropertyMappingsBuilder.of(
                        classMeta,
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().mapperBuilderErrorHandler(errorHandler),
                        new PropertyMappingsBuilder.PropertyPredicateFactory<SampleFieldKey>() {
                            @Override
                            public PropertyFinder.PropertyFilter predicate( final SampleFieldKey key, Object[] properties, final List<PropertyMappingsBuilder.AccessorNotFound> accessorNotFounds) {
                                return  new  PropertyFinder.PropertyFilter (new Predicate<PropertyMeta<?, ?>>() {

                                    @Override
                                    public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                        if (propertyMeta.isSelf()) {
                                            accessorNotFounds.add(new PropertyMappingsBuilder.AccessorNotFound(key, propertyMeta.getPath(), propertyMeta.getPropertyType(), ErrorDoc.CSFM_GETTER_NOT_FOUND, propertyMeta));
                                            return false;
                                        }
                                        return true;
                                    }
                                }, ConstantPredicate.<PropertyMeta<?, ?>>truePredicate());
                            }
                        });


        builder.addProperty(new SampleFieldKey("id", 1), columnDefinition);


        verify(errorHandler).propertyNotFound(C602.class, "id");
        
    }
    
    public static class C602 {
        public final String name;

        public C602(String name) {
            this.name = name;
        }
    }
    
    

    @Test
    public void testInstantiatorError() {
        MapperImpl<Object, DbObject> mapper = new MapperImpl<Object, DbObject>(null, null,
                new BiInstantiator<Object, MappingContext<? super Object>, DbObject>() {
                    @Override
                    public DbObject newInstance(Object s, MappingContext<? super Object> context) throws Exception {
                        throw new IOException();
                    }
                });

        try {
            mapper.map(null, null);
            fail("Expected error");
        } catch(Exception e) {
            assertTrue(e instanceof IOException);
        }
    }

}
