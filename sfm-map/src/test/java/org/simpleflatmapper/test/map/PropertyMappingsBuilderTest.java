package org.simpleflatmapper.test.map;


import org.junit.Test;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.IgnoreMapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.DefaultPropertyNameMatcherFactory;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.mapper.PropertyMappingsBuilder;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ListElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
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
        PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder =
                new PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        RethrowMapperBuilderErrorHandler.INSTANCE,
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });

        builder.addProperty(new SampleFieldKey("phones_str", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());


        assertIsPhonesElement(builder);

    }


    //@Test causes issue with jdbc array
    public void testAnonymousInArray() {

        final ClassMeta<AnonymousElement> classMeta = ReflectionService.newInstance().getClassMeta(AnonymousElement.class);
        PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder2 =
                new PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        RethrowMapperBuilderErrorHandler.INSTANCE,
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });

        builder2.addProperty(new SampleFieldKey("phones", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        assertIsPhonesElement(builder2);

    }

    @SuppressWarnings("unchecked")
    private void assertIsPhonesElement(PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder) {
        final List<PropertyMapping<AnonymousElement, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>> props = new ArrayList<PropertyMapping<AnonymousElement, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>>();
        builder.forEachProperties(new ForEachCallBack<PropertyMapping<AnonymousElement, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>>() {
            @Override
            public void handle(PropertyMapping<AnonymousElement, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> pm) {
                props.add(pm);
            }
        });

        assertEquals(1, props.size());

        final PropertyMeta<AnonymousElement, ?> propertyMeta = props.get(0).getPropertyMeta();

        assertTrue("Is sub property", propertyMeta.isSubProperty());

        SubPropertyMeta<AnonymousElement, ?, ?> subPropertyMeta = (SubPropertyMeta<AnonymousElement, ?, ?>) propertyMeta;

        assertTrue(TypeHelper.isAssignable(List.class, subPropertyMeta.getOwnerProperty().getPropertyType()));
        assertTrue("expect ListElementPropertyMeta " + subPropertyMeta.getSubProperty(), subPropertyMeta.getSubProperty() instanceof ListElementPropertyMeta);
    }


    @Test
    public void testCustomSourceIncompatibility() {

        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder2 =
                new PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        RethrowMapperBuilderErrorHandler.INSTANCE,
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });

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
        PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder2 =
                new PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        IgnoreMapperBuilderErrorHandler.INSTANCE,
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });

        builder2.addProperty(new SampleFieldKey("id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity().addGetter(new Getter<Object, String>() {
            @Override
            public String get(Object target) throws Exception {
                return null;
            }
        }));
        builder2.addProperty(new SampleFieldKey("name", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        final List<PropertyMapping<DbObject, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>> props = getAllProperties(builder2);

        assertEquals(2, props.size());
        assertNull(props.get(0));
    }

    private List<PropertyMapping<DbObject, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>> getAllProperties(PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder2) {
        return builder2.currentProperties();
    }


    @Test
    public void testAddPropertyIfPresent() {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder2 =
                new PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        RethrowMapperBuilderErrorHandler.INSTANCE,
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });


        List<PropertyMapping<DbObject, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>> props;

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
        PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder2 =
                new PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        IgnoreMapperBuilderErrorHandler.INSTANCE,
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });
        builder2.addProperty(new SampleFieldKey("id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());
        builder2.addProperty(new SampleFieldKey("not_id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());
        List<PropertyMapping<DbObject, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>> props = getAllProperties(builder2);

        assertEquals(2, props.size());
        assertNull(props.get(1));
    }

    @Test
    public void testAddPropertyIgnore() {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder2 =
                new PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        IgnoreMapperBuilderErrorHandler.INSTANCE,
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });

        builder2.addProperty(new SampleFieldKey("not_id", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity().addIgnore());
        List<PropertyMapping<DbObject, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>> props = getAllProperties(builder2);

        assertEquals(1, props.size());
        assertNull(props.get(0));
    }


    @Test
    public void testSelfPropertyInvalidation() {
        final ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);

        MapperBuilderErrorHandler errorHandler = mock(MapperBuilderErrorHandler.class);

        PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder =
                new PropertyMappingsBuilder<DbObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        errorHandler,
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });

        builder.addProperty(new SampleFieldKey("self", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        verify(errorHandler, never()).customFieldError(any(FieldKey.class), any(String.class));
        verify(errorHandler, never()).accessorNotFound(any(String.class));
        verify(errorHandler, never()).propertyNotFound(any(Type.class), any(String.class));

        builder.addProperty(new SampleFieldKey("id", 1), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        verify(errorHandler).propertyNotFound(DbObject.class, "self");
    }
}
