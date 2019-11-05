package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.reflect.property.OptionalProperty;
import org.simpleflatmapper.reflect.test.KeyTest;
import org.simpleflatmapper.reflect.test.KeyTestProperty;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.test.beans.DbPartialFinalObject;
import org.simpleflatmapper.util.Asserts;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
//IFJAVA8_START
import java.util.Optional;
import java.time.ZoneId;
//IFJAVA8_END

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ObjectClassMetaTest {


    private PropertyFinder.PropertyFilter propertyFilter = PropertyFinder.PropertyFilter.trueFilter();

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
        propertyFinder1.findProperty(DefaultPropertyNameMatcher.of("email"), new Object[0], (TypeAffinity)null, propertyFilter); // force non direct mode
        assertNotNull(propertyFinder1.findProperty(DefaultPropertyNameMatcher.of("myid"), new Object[0], (TypeAffinity)null, propertyFilter));
        assertNotNull(propertyFinder1.findProperty(DefaultPropertyNameMatcher.of("myname"), new Object[0], (TypeAffinity)null, propertyFilter));

        classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyFinder<DbObject> propertyFinder2 = classMeta.newPropertyFinder();
        propertyFinder2.findProperty(DefaultPropertyNameMatcher.of("email"), new Object[0], (TypeAffinity)null, propertyFilter); // force non direct mode
        assertNull(propertyFinder2.findProperty(DefaultPropertyNameMatcher.of("myid"), new Object[0], (TypeAffinity)null, propertyFilter));
        assertNull(propertyFinder2.findProperty(DefaultPropertyNameMatcher.of("myname"), new Object[0], (TypeAffinity)null, propertyFilter));

    }

    @Test
    public void testTypeVariable() {
        ClassMeta<TVObject<Date>> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<TVObject<Date>>() {} .getType());
        assertEquals(Date.class, classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("t"), new Object[0], (TypeAffinity)null, propertyFilter).getPropertyType());
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

        assertNotNull(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("string"), new Object[0], (TypeAffinity)null, propertyFilter));

    }

    @Test
    public void testFieldWithImcompatibleGetterType() throws Exception {
        IncompatibleGetter target = new IncompatibleGetter();
        target.value = "aa";

        ClassMeta<IncompatibleGetter> meta = ReflectionService.newInstance().getClassMeta(IncompatibleGetter.class);

        PropertyMeta<IncompatibleGetter, Object> pm = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, propertyFilter);

        assertEquals("aa", pm.getGetter().get(target));
    }


    @Test
    public void testGetterBetterThanName() throws Exception {
        GetterBetterThanName target = new GetterBetterThanName();

        ClassMeta<GetterBetterThanName> meta = ReflectionService.newInstance().getClassMeta(GetterBetterThanName.class);

        PropertyMeta<GetterBetterThanName, Object> pm = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, propertyFilter);

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
                .findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, propertyFilter);

        assertNotNull(pm);
        pm.getSetter().set(target, "aa");
        assertEquals("aa", pm.getGetter().get(target));
    }

    @Test
    public void testFieldWithCompatibleGetterType() throws Exception {
        CompatibleGetter target = new CompatibleGetter();

        ClassMeta<CompatibleGetter> meta = ReflectionService.newInstance().getClassMeta(CompatibleGetter.class);

        PropertyMeta<CompatibleGetter, Object> pm =
                meta
                        .newPropertyFinder()
                        .findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, TestPropertyFinderProbe.INSTANCE, propertyFilter);

        assertEquals("value", pm.getPath());

        assertEquals(Arrays.asList("aa"), pm.getGetter().get(target));

        PropertyMeta<CompatibleGetter, Object> pm2 = meta
                .newPropertyFinder()
                .findProperty(DefaultPropertyNameMatcher.of("value2"), new Object[0], (TypeAffinity)null, propertyFilter);
        assertEquals("value2", pm2.getPath());

        assertEquals(2, pm2.getGetter().get(target));

    }

    @Test
    public void testFieldWithCompatibleSetterType() throws Exception {
        CompatibleGetter target = new CompatibleGetter();

        ClassMeta<CompatibleGetter> meta = ReflectionService.newInstance().getClassMeta(CompatibleGetter.class);

        PropertyMeta<CompatibleGetter, Object> pm = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, propertyFilter);


        pm.getSetter().set(target, null);

        assertEquals(Arrays.asList("bb"), target.value);

        PropertyMeta<CompatibleGetter, Object> pm2 = meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value2"), new Object[0], (TypeAffinity)null, propertyFilter);

        pm2.getSetter().set(target, 2);
        assertEquals(3, target.value2);

    }

    @Test
    public void testSelfRefInvalidation() {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        PropertyFinder<DbObject> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<DbObject, ?> property = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("dddd"), new Object[0], (TypeAffinity)null, propertyFilter);
        assertNotNull(property);
        assertTrue(property.isSelf());
        assertTrue(property.isValid());

        assertNull(propertyFinder.findProperty(DefaultPropertyNameMatcher.of("cccc"), new Object[0], (TypeAffinity)null, propertyFilter));
        assertNotNull(propertyFinder.findProperty(DefaultPropertyNameMatcher.of("dddd"), new Object[0], (TypeAffinity)null, propertyFilter));

        PropertyMeta<DbObject, ?> idProperty = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("id"), new Object[0], (TypeAffinity)null, propertyFilter);
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
        PropertyMeta<Optional<DbObject>, ?> property = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("dddd"), new Object[0], (TypeAffinity)null, propertyFilter);
        assertNotNull(property);
        assertTrue(property.isValid());

        assertNull(propertyFinder.findProperty(DefaultPropertyNameMatcher.of("cccc"), new Object[0], (TypeAffinity)null, propertyFilter));
        assertNotNull(propertyFinder.findProperty(DefaultPropertyNameMatcher.of("dddd"), new Object[0], (TypeAffinity)null, propertyFilter));

        PropertyMeta<Optional<DbObject>, ?> idProperty = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("id"), new Object[0], (TypeAffinity)null, propertyFilter);
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

        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, propertyFilter).isConstructorProperty());

    }

    @Test
    public void testResolveConstructorParamWithDeductorNoNull() {
        ClassMeta<NonNullContainer> classMeta = ReflectionService.disableAsm().getClassMeta(NonNullContainer.class);

        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, propertyFilter).isConstructorProperty());
        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value2"), new Object[0], (TypeAffinity)null, propertyFilter).isConstructorProperty());

    }

    @Test
    public void testResolveConstructorParamWithDeductorNoNullInParam() {
        ClassMeta<TwoStringObjectNonNull> classMeta = ReflectionService.disableAsm().getClassMeta(TwoStringObjectNonNull.class);

        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, propertyFilter).isConstructorProperty());
        assertTrue(classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value2"), new Object[0], (TypeAffinity)null, propertyFilter).isConstructorProperty());

    }

    @Test
    public void testAnnotationsToProperty() {
        final ClassMeta<ObjectWithAnnotation> classMeta = ReflectionService.newInstance().getClassMeta(ObjectWithAnnotation.class);


        Object[] definedProperties = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("id"), new Object[0], (TypeAffinity)null, propertyFilter).getDefinedProperties();

        assertEquals(1, definedProperties.length);
        assertTrue(definedProperties[0] instanceof KeyTestProperty);
    }

    public static class ObjectWithAnnotation {
        @KeyTest
        public int id;
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
            this.value2 = Asserts.requireNonNull("value2", value2);
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
//IFJAVA8_START

    
    @Test
    public void test501ZoneId() {
        final ClassMeta<C501> classMeta = ReflectionService.newInstance().getClassMeta(C501.class);

        PropertyMeta<C501, Object> zoneId = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("zoneId"), new Object[0], (TypeAffinity)null, propertyFilter);

        System.out.println("zoneId = " + zoneId);

    } 
    
    public static class C501 {
        public final ZoneId zoneId;

        public C501(ZoneId zoneId) {
            this.zoneId = zoneId;
        }
    }
    //IFJAVA8_END




    @Test
    public void testTargetBuilder574() {
        ClassMeta<ImmutableFoobarValue.Builder> classMeta = ReflectionService.newInstance().getClassMeta(ImmutableFoobarValue.Builder.class);

        List<InstantiatorDefinition> instantiatorDefinitions = classMeta.getInstantiatorDefinitions();

        assertFalse(instantiatorDefinitions.isEmpty());
    }

}
