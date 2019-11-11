package org.simpleflatmapper.test.map.property;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.property.*;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

//IFJAVA8_START
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.simpleflatmapper.map.property.time.JavaDateTimeFormatterProperty;
import org.simpleflatmapper.map.property.time.JavaZoneIdProperty;
//IFJAVA8_END

import java.util.TimeZone;

import static junit.framework.TestCase.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

public class PropertyTest {

    @Test
    public void testConstantValueProperty() {
        ConstantValueProperty<String> cvp = new ConstantValueProperty<String>("aa", String.class);
        assertEquals("aa", cvp.getValue());
        assertEquals(String.class, cvp.getType());
        assertEquals("ConstantValue{aa}", cvp.toString());
    }

    @Test
    public void testDateFormatProperty() {
        DateFormatProperty dfp = new DateFormatProperty("pattern");
        assertEquals("pattern", dfp.get());
        assertEquals("DateFormat{'pattern'}", dfp.toString());
    }

    @Test
    public void testDefaultDateFormatProperty() {
        DefaultDateFormatProperty dfp = new DefaultDateFormatProperty("pattern");
        assertEquals("pattern", dfp.get());
        assertEquals("DefaultDateFormat{'pattern'}", dfp.toString());
    }

    @Test
    public void testDefaultValueProperty() {
        DefaultValueProperty<String> property = new DefaultValueProperty<String>("hello");
        assertEquals("hello", property.getValue());
    }

    @Test
    public void testEnumOrdinalFormatProperty() {
        Assert.assertEquals("EnumOrdinalFormat", new EnumOrdinalFormatProperty().toString());
    }

    @Test
    public void testFieldMapperProperty() {
        FieldMapper<Object, Object> fieldMapper = new FieldMapper<Object, Object>() {
            @Override
            public void mapTo(Object source, Object target, MappingContext<? super Object> context) throws Exception {

            }

            @Override
            public String toString() {
                return "FM";
            }
        };
        FieldMapperProperty fieldMapperProperty = new FieldMapperProperty(fieldMapper);

        assertSame(fieldMapper, fieldMapperProperty.getFieldMapper());
        assertEquals("FieldMapper{FM}", fieldMapperProperty.toString());
    }

    @Test
    public void testFormatProperty() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd") {
            @Override
            public String toString() {
                return "SDF";
            }
        };
        FormatProperty property = new FormatProperty(format);
        assertNotSame(format, property.format());
        assertEquals(format, property.format());
        assertEquals("Format{SDF}", property.toString());
    }

    @Test
    public void testGetterFactoryProperty() {
        ContextualGetterFactory<Object, Object> getterFactory = new ContextualGetterFactory<Object, Object>() {
            @Override
            public <P> ContextualGetter<Object, P> newGetter(Type target, Object key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                return null;
            }

            @Override
            public String toString() {
                return "GF";
            }
        };
        GetterFactoryProperty getterFactoryProperty = new GetterFactoryProperty(getterFactory);

        assertSame(getterFactory, getterFactoryProperty.getGetterFactory());
        assertEquals("GetterFactory{GF}", getterFactoryProperty.toString());
    }

    @Test
    public void testGetterProperty() {

        Getter<Object, Integer> getter = new Getter<Object, Integer>() {
            @Override
            public Integer get(Object target) throws Exception {
                return null;
            }

            @Override
            public String toString() {
                return "G";
            }
        };

        GetterProperty getterProperty = new GetterProperty(getter);

        assertSame(getter, getterProperty.getGetter());
        assertEquals(Integer.class, getterProperty.getReturnType());
        assertEquals("Getter{G}", getterProperty.toString());
    }

    @Test
    public void testIgnoreProperty() {
        IgnoreProperty property = new IgnoreProperty();
        assertEquals("Ignore{}", property.toString());
    }

    @Test
    public void testKeyProperty() {
        Predicate<PropertyMeta<?, ?>> p1 = new Predicate<PropertyMeta<?, ?>>() {
            @Override
            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                return false;
            }

            @Override
            public String toString() {
                return "P1";
            }
        };
        Predicate<PropertyMeta<?, ?>> p2 = new Predicate<PropertyMeta<?, ?>>() {
            @Override
            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                return false;
            }

            @Override
            public String toString() {
                return "P2";
            }
        };
        KeyProperty key1 = new KeyProperty(p1);
        KeyProperty key2 = new KeyProperty(p2);

        assertEquals(key1, key1);
        assertEquals(key1.hashCode(), key1.hashCode());

        assertEquals(p1, key1.getAppliesTo());
        assertNotEquals(key1, key2);
        assertNotEquals(key1.hashCode(), key2.hashCode());
        assertEquals("Key{P1}", key1.toString());
    }

    @Test
    public void testRenameProperty() {
        RenameProperty p1 = new RenameProperty("name1");
        RenameProperty p2 = new RenameProperty("name2");

        assertEquals(p1, p1);
        assertEquals(p1.hashCode(), p1.hashCode());
        assertNotEquals(p1, p2);
        assertNotEquals(p1.hashCode(), p2.hashCode());
        assertEquals("Rename{'name1'}", p1.toString());
    }

    @Test
    public void testSetterFactoryProperty() {
        SetterFactory<Object, Object> setterFactory = new SetterFactory<Object, Object>() {
            @Override
            public <P> Setter<Object, P> getSetter(Object arg) {
                return null;
            }

            @Override
            public String toString() {
                return "SF";
            }
        };
        SetterFactoryProperty property = new SetterFactoryProperty(setterFactory);

        assertSame(setterFactory, property.getSetterFactory());
        assertEquals("SetterFactory{SF}", property.toString());
    }

    @Test
    public void testSetterProperty() {
        Setter<Object, Object> setter = new Setter<Object, Object>() {
            @Override
            public void set(Object target, Object value) throws Exception {
            }

            @Override
            public String toString() {
                return "S";
            }
        };
        SetterProperty property = new SetterProperty(setter);

        assertSame(setter, property.getSetter());
        assertEquals("Setter{S}", property.toString());
    }

    @Test
    public void testTimeZoneProperty() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        TimeZoneProperty property = new TimeZoneProperty(timeZone);

        assertSame(timeZone, property.get());
        assertEquals("TimeZone{UTC}", property.toString());
    }

    //IFJAVA8_START
    @Test
    public void testJavaZoneIdProperty() {
        ZoneId zoneId = ZoneId.of("UTC");
        JavaZoneIdProperty property = new JavaZoneIdProperty(zoneId);

        assertSame(zoneId, property.get());
        assertEquals("ZoneId{UTC}", property.toString());
    }

    @Test
    public void testJavaDateTimeFormatterProperty() {
        JavaDateTimeFormatterProperty property = new JavaDateTimeFormatterProperty(DateTimeFormatter.ISO_DATE);

        assertSame(DateTimeFormatter.ISO_DATE, property.get());
        assertEquals("DateTimeFormatter{" +  DateTimeFormatter.ISO_DATE + "}", property.toString());
    }
    //IFJAVA8_END

}
