package org.simpleflatmapper.test.map;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.GetterFactoryProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.IndexedGetter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;

import static org.junit.Assert.*;

public class FieldMapperColumnDefinitionTest {


    @Test
    public void testCompose() throws Exception {
        GetterFactory<InputStream, SampleFieldKey> getterFactory = new GetterFactory<InputStream, SampleFieldKey>() {
            @Override
            public <P> Getter<InputStream, P> newGetter(Type target, SampleFieldKey key, Object... properties) {
                return null;
            }

            @Override
            public String toString() {
                return "GetterFactory";
            }

        };
        Getter<InputStream, Integer> getter = new Getter<InputStream, Integer>() {
            @Override
            public Integer get(InputStream target) throws Exception {
                return 3;
            }
            @Override
            public String toString() {
                return "Getter";
            }
        };
        FieldMapper<InputStream, Object> fieldMapper = new FieldMapper<InputStream, Object>() {
            @Override
            public void mapTo(InputStream source, Object target, MappingContext<? super InputStream> mappingContext) throws Exception {
            }

            @Override
            public String toString() {
                return "FieldMapper";
            }
        };
        final Predicate<PropertyMeta<?, ?>> appliesTo = new Predicate<PropertyMeta<?, ?>>() {
            @Override
            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                return false;
            }

            @Override
            public String toString() {
                return "K";
            }
        };
        FieldMapperColumnDefinition<SampleFieldKey> compose =
                FieldMapperColumnDefinition.<SampleFieldKey>identity().addRename("blop").addGetter(getter).addFieldMapper(fieldMapper).addGetterFactory(getterFactory).addKey(appliesTo);

        Assert.assertEquals("blop", compose.rename(new SampleFieldKey("bar", -1)).getName());
        assertEquals(fieldMapper, compose.getCustomFieldMapper());
        assertEquals(getterFactory, ((ContextualGetterFactoryAdapter)compose.getCustomGetterFactoryFrom(InputStream.class)).delegate);
        assertNull(compose.getCustomGetterFactoryFrom(Date.class));
        assertEquals(new Integer(3), compose.getCustomGetterFrom(InputStream.class).get(null));
        assertNull(compose.getCustomGetterFrom(Date.class));

        assertFalse(compose.ignore());

        assertTrue(FieldMapperColumnDefinition.<SampleFieldKey>identity().addIgnore().ignore());

        assertEquals("ColumnDefinition{Rename{'blop'}, Getter{Getter}, FieldMapper{FieldMapper}, GetterFactory{ContextualGetterFactoryAdapter{delegate=GetterFactory}}, Key{K}, Ignore{}}", compose.addIgnore().toString());

        assertTrue(compose.isKey());
        assertFalse(FieldMapperColumnDefinition.<SampleFieldKey>identity().isKey());
        assertSame(appliesTo, compose.keyAppliesTo());
    }


    @Test
    public void testMultipleCustomGetterFactory() throws Exception {
        GetterFactoryProperty property1 = GetterFactoryProperty.forType(String.class, new IndexedGetter<Object, String>() {
            @Override
            public String get(Object o, int i) throws Exception {
                return "hello";
            }
        });

        GetterFactoryProperty property2 = GetterFactoryProperty.forType(Long.class, new IndexedGetter<Object, Long>() {
            @Override
            public Long get(Object o, int i) throws Exception {
                return 123l;
            }
        });

        FieldMapperColumnDefinition<SampleFieldKey> def = FieldMapperColumnDefinition.<SampleFieldKey>identity().add(property1, property2);

        assertEquals("hello", def.getCustomGetterFactoryFrom(Object.class).newGetter(String.class, new SampleFieldKey("sd", 3), null).get(null, null));
        assertEquals(123l, def.getCustomGetterFactoryFrom(Object.class).newGetter(Long.class, new SampleFieldKey("sd", 3), null).get(null, null));

    }
}
