package org.simpleflatmapper.test.map;


import org.junit.Test;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.IgnoreMapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.annotation.Key;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.mapper.PropertyMappingsBuilder;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ArrayElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.TypeReference;


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
        return PropertyMappingsBuilder.<T, SampleFieldKey>of(classMeta, MapperConfig.<SampleFieldKey>fieldMapperConfig(), ConstantPredicate.<PropertyMeta<?, ?>>truePredicate());
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
        assertTrue("expect ListElementPropertyMeta " + subPropertyMeta.getSubProperty(), subPropertyMeta.getSubProperty() instanceof ArrayElementPropertyMeta);
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
                PropertyMappingsBuilder.of(classMeta, MapperConfig.<SampleFieldKey>fieldMapperConfig().mapperBuilderErrorHandler(IgnoreMapperBuilderErrorHandler.INSTANCE), ConstantPredicate.<PropertyMeta<?, ?>>truePredicate());

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
                PropertyMappingsBuilder.of(classMeta, MapperConfig.<SampleFieldKey>fieldMapperConfig().mapperBuilderErrorHandler(IgnoreMapperBuilderErrorHandler.INSTANCE), ConstantPredicate.<PropertyMeta<?, ?>>truePredicate());

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
                        MapperConfig.<SampleFieldKey>fieldMapperConfig().mapperBuilderErrorHandler(errorHandler),
                        ConstantPredicate.<PropertyMeta<?, ?>>truePredicate());

        builder.addProperty(new SampleFieldKey("self", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        verify(errorHandler, never()).customFieldError(any(FieldKey.class), any(String.class));
        verify(errorHandler, never()).accessorNotFound(any(String.class));
        verify(errorHandler, never()).propertyNotFound(any(Type.class), any(String.class));

        builder.addProperty(new SampleFieldKey("id", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        verify(errorHandler).propertyNotFound(DbObject.class, "self");
    }

    @Test
    public void testAnnotations() {

        final ClassMeta<ObjectWithAnnotation> classMeta = ReflectionService.newInstance().getClassMeta(ObjectWithAnnotation.class);
        PropertyMappingsBuilder<ObjectWithAnnotation, SampleFieldKey> builder2 =
                defaultPropertyMappingBuilder(classMeta);

        builder2.addProperty(new SampleFieldKey("id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        List<PropertyMapping<ObjectWithAnnotation, ?, SampleFieldKey>> propertyMappings = builder2.currentProperties();

        assertTrue(propertyMappings.get(0).getColumnDefinition().isKey());
    }

    public static class ObjectWithAnnotation {
        @Key
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

}
