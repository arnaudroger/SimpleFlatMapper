package org.simpleflatmapper.reflect.meta;

import org.junit.Test;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.test.beans.DbPartialFinalObject;
import org.simpleflatmapper.util.Asserts;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
//IFJAVA8_START
import java.util.Optional;
//IFJAVA8_END

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ObjectClassMetaTest {


    @Test
    public void testAliasProvider() {
        ReflectionService reflectionService = ReflectionService.newInstance().withAliasProvider(new AliasProvider() {
            @Override
            public String getAliasForMethod(Method method) {
                if ("getName".equals(method.getName())) {
                    return "myname";
                }
                return null;
            }

            @Override
            public String getAliasForField(Field field) {
                if ("id".equals(field.getName())) {
                    return "myid";
                }
                return null;
            }

            @Override
            public Table getTable(Class<?> target) {
                return null;
            }
        });

        ClassMeta<DbObject> classMeta = reflectionService.getClassMeta(DbObject.class);
        PropertyFinder<DbObject> propertyFinder1 = classMeta.newPropertyFinder();
        propertyFinder1.findProperty(DefaultPropertyNameMatcher.of("email")); // force non direct mode
        assertNotNull(propertyFinder1.findProperty(DefaultPropertyNameMatcher.of("myid")));
        assertNotNull(propertyFinder1.findProperty(DefaultPropertyNameMatcher.of("myname")));

        classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyFinder<DbObject> propertyFinder2 = classMeta.newPropertyFinder();
        propertyFinder2.findProperty(DefaultPropertyNameMatcher.of("email")); // force non direct mode
        assertNull(propertyFinder2.findProperty(DefaultPropertyNameMatcher.of("myid")));
        assertNull(propertyFinder2.findProperty(DefaultPropertyNameMatcher.of("myname")));

    }

    @Test
    public void testTypeVariable() {
        ClassMeta<TVObject<Date>> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<TVObject<Date>>() {} .getType());
        assertEquals(Date.class, classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("t")).getPropertyType());
    }

    public static class TVObject<T> {
        public T t;
    }

    @Test
    public void testNumberOfProperties() {
        ObjectClassMeta<?> classMeta = (ObjectClassMeta<?>) ReflectionService.newInstance().getClassMeta(DbObject.class);
        assertEquals(7, classMeta.getNumberOfProperties());

        classMeta = (ObjectClassMeta<?>) ReflectionService.newInstance().getClassMeta(DbFinalObject.class);
        assertEquals(6, classMeta.getNumberOfProperties());

        classMeta = (ObjectClassMeta<?>) ReflectionService.newInstance().getClassMeta(DbPartialFinalObject.class);
        assertEquals(6, classMeta.getNumberOfProperties());
    }

    @Test
    public void testGetFirstProperty() {
        ObjectClassMeta<?> classMeta = (ObjectClassMeta<?>) ReflectionService.newInstance().getClassMeta(DbObject.class);
        assertEquals("object", classMeta.getFirstProperty().getPath());

        classMeta = (ObjectClassMeta<?>) ReflectionService.newInstance().getClassMeta(DbFinalObject.class);
        assertEquals("id", classMeta.getFirstProperty().getPath());

        classMeta = (ObjectClassMeta<?>) ReflectionService.newInstance().getClassMeta(DbPartialFinalObject.class);
        assertEquals("email", classMeta.getFirstProperty().getPath());
    }

    @Test
    public void testGetterOnly() {
        ClassMeta<GetterOnly> classMeta = ReflectionService.newInstance().getClassMeta(GetterOnly.class);

        assertNotNull(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("string")));

    }

    @Test
    public void testFieldWithImcompatibleGetterType() throws Exception {
        IncompatibleGetter target = new IncompatibleGetter();
        target.value = "aa";

        ClassMeta<IncompatibleGetter> meta = ReflectionService.newInstance().getClassMeta(IncompatibleGetter.class);

        PropertyMeta<IncompatibleGetter, Object> pm = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"));

        assertEquals("aa", pm.getGetter().get(target));
    }


    @Test
    public void testGetterBetterThanName() throws Exception {
        GetterBetterThanName target = new GetterBetterThanName();

        ClassMeta<GetterBetterThanName> meta = ReflectionService.newInstance().getClassMeta(GetterBetterThanName.class);

        PropertyMeta<GetterBetterThanName, Object> pm = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"));

        assertEquals("getValue", pm.getGetter().get(target));
    }
    @Test
    public void testGetterSetterWithoutPrefix() throws Exception {
        UnprefixedBean target = new UnprefixedBean();

        ClassMeta<UnprefixedBean> meta =
                ReflectionService.newInstance()
                        .getClassMeta(UnprefixedBean.class);

        PropertyMeta<UnprefixedBean, Object> pm = meta
                .newPropertyFinder()
                .findProperty(DefaultPropertyNameMatcher.of("value"));

        assertNotNull(pm);
        pm.getSetter().set(target, "aa");
        assertEquals("aa", pm.getGetter().get(target));
    }

    @Test
    public void testFieldWithCompatibleGetterType() throws Exception {
        CompatibleGetter target = new CompatibleGetter();

        ClassMeta<CompatibleGetter> meta = ReflectionService.newInstance().getClassMeta(CompatibleGetter.class);

        PropertyMeta<CompatibleGetter, Object> pm = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"));

        assertEquals(Arrays.asList("aa"), pm.getGetter().get(target));

        PropertyMeta<CompatibleGetter, Object> pm2 = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value2"));

        assertEquals(2, pm2.getGetter().get(target));

    }

    @Test
    public void testFieldWithCompatibleSetterType() throws Exception {
        CompatibleGetter target = new CompatibleGetter();

        ClassMeta<CompatibleGetter> meta = ReflectionService.newInstance().getClassMeta(CompatibleGetter.class);

        PropertyMeta<CompatibleGetter, Object> pm = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"));


        pm.getSetter().set(target, null);

        assertEquals(Arrays.asList("bb"), target.value);

        PropertyMeta<CompatibleGetter, Object> pm2 = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value2"));

        pm2.getSetter().set(target, 2);
        assertEquals(3, target.value2);

    }

    @Test
    public void testSelfRefInvalidation() {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyFinder<DbObject> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<DbObject, ?> property = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("dddd"));
        assertNotNull(property);
        assertTrue(property.isSelf());
        assertTrue(property.isValid());

        assertNull(propertyFinder.findProperty(DefaultPropertyNameMatcher.of("cccc")));
        assertNotNull(propertyFinder.findProperty(DefaultPropertyNameMatcher.of("dddd")));

        PropertyMeta<DbObject, ?> idProperty = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("id"));
        assertNotNull(idProperty);
        assertFalse(idProperty.isSelf());
        assertTrue(idProperty.isValid());


        assertFalse(property.isValid());
    }

    //IFJAVA8_START
    @Test
    public void testSelfRefInvalidationOnOptional() {
        ClassMeta<Optional<DbObject>> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<Optional<DbObject>>() {
        }.getType());
        PropertyFinder<Optional<DbObject>> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<Optional<DbObject>, ?> property = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("dddd"));
        assertNotNull(property);
        assertTrue(property.isValid());

        assertNull(propertyFinder.findProperty(DefaultPropertyNameMatcher.of("cccc")));
        assertNotNull(propertyFinder.findProperty(DefaultPropertyNameMatcher.of("dddd")));

        PropertyMeta<Optional<DbObject>, ?> idProperty = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("id"));
        assertNotNull(idProperty);
        assertTrue(idProperty.isValid());


        assertFalse(property.isValid());
    }
    //IFJAVA8_END

    public static class GetterBetterThanName {
        public String getValue() {
            return "getValue";
        }
        public String value() {
            return "value";
        }
    }

    public static class IncompatibleGetter {
        public String value;

        public int getValue() {
            return 1;
        }
    }

    public static class CompatibleGetter {
        private List<String> value;
        private Number value2;


        public List getValue() {
            return Arrays.asList("aa");
        }

        public void setValue(ArrayList list) {
            this.value = Arrays.asList("bb");
        }

        public Integer getValue2() {
            return 2;
        }

        public void setValue2(int o) {
            value2 = 3;
        }
    }

    public static class GetterOnly {
        public String getString() {
            return "value";
        }

        public int intValue() {
            return 3;
        }
    }
    public static class MyClass{
        private String id;

        private DbObject o;

        public MyClass(String id) {
            this.id = id;
        }
        public MyClass() {

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public DbObject getO() {
            return o;
        }

        public void setO(DbObject o) {
            this.o = o;
        }

    }

    private class UnprefixedBean {
        private String alt;
        private int elt;
        public void value(String value) {
            this.alt = value;
        }
        public String value() {
            return alt;
        }
    }

    @Test
    public void testForEach() {
        final List<String> names = new ArrayList<String>();
        ReflectionService.newInstance().getClassMeta(DbObject.class).forEachProperties(new Consumer<PropertyMeta<DbObject, ?>>() {
            @Override
            public void accept(PropertyMeta<DbObject, ?> dbObjectPropertyMeta) {
                names.add(dbObjectPropertyMeta.getName());
            }
        });

        assertEquals(Arrays.asList("object", "id", "name", "email", "creationTime", "typeOrdinal", "typeName"), names);
    }


    @Test
    public void testResolveConstructorParamWithDeductor() {
        ClassMeta<StringObject> classMeta = ReflectionService.disableAsm().getClassMeta(StringObject.class);

        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value")).isConstructorProperty());

    }

    @Test
    public void testResolveConstructorParamWithDeductorNoNull() {
        ClassMeta<NonNullContainer> classMeta = ReflectionService.disableAsm().getClassMeta(NonNullContainer.class);

        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value")).isConstructorProperty());
        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value2")).isConstructorProperty());

    }

    @Test
    public void testResolveConstructorParamWithDeductorNoNullInParam() {
        ClassMeta<TwoStringObjectNonNull> classMeta = ReflectionService.disableAsm().getClassMeta(TwoStringObjectNonNull.class);

        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value")).isConstructorProperty());
        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value2")).isConstructorProperty());

    }

    public static class StringObject {
        private final String value;

        public StringObject(String value) {
            this.value = value;
        }

    }

    public static class TwoStringObjectNonNull {
        private final String value;
        private final String value2;

        public TwoStringObjectNonNull(String value, String value2) {
            this.value = Asserts.requireNonNull("value", value);
            this.value2 = Asserts.requireNonNull("value2", value2);;
        }

    }

    public static class NonNullContainer {
        private final TwoStringObjectNonNull value;
        private final TwoStringObjectNonNull value2;


        public NonNullContainer(TwoStringObjectNonNull value, TwoStringObjectNonNull value2) {
            this.value = Asserts.requireNonNull("", value);
            this.value2 = Asserts.requireNonNull("", value2);
        }
    }


}
