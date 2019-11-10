package org.simpleflatmapper.reflect.test.meta;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.reflect.property.*;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.tuple.Tuples;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.util.List;

import static org.junit.Assert.*;

public class PropertyFinderTest {


    private PropertyFinder.PropertyFilter isValidPropertyMeta = PropertyFinder.PropertyFilter.trueFilter();


    @Test
    public void testIssue665Speculative() {

        ClassMeta<AA> classMeta = ReflectionService.newInstance().getClassMeta(AA.class);

        PropertyFinder<AA> finder = classMeta.newPropertyFinder();
        Object[] properties = {SpeculativeArrayIndexResolutionProperty.INSTANCE};

        PropertyMeta<AA, ?> a = finder.findProperty(matcher("cols_a"), properties, (TypeAffinity)null, isValidPropertyMeta);
        PropertyMeta<AA, ?> b = finder.findProperty(matcher("cols_b"), properties, (TypeAffinity)null, isValidPropertyMeta).compressSubSelf();

        assertEquals("cols[0].a", a.getPath());
        assertEquals("cols[1]", b.getPath());

    }

    @Test
    public void testIssue665NonSpeculative() {
        ClassMeta<AA> classMeta = ReflectionService.newInstance().getClassMeta(AA.class);

        PropertyFinder<AA> finder = classMeta.newPropertyFinder();
        Object[] properties = {};

        PropertyMeta<AA, ?> a = finder.findProperty(matcher("cols_a"), properties, (TypeAffinity)null, isValidPropertyMeta);
        PropertyMeta<AA, ?> b = finder.findProperty(matcher("cols_b"), properties, (TypeAffinity)null, isValidPropertyMeta);

        assertEquals("cols[0].a", a.getPath());
        assertNull(b);
    }

    public static class AA {
        public List<BB> cols;
    }
    public static class BB {
        public String a;
    }
    @Test
    public void testTestArrayStartAt1() {

        ClassMeta<DbObject[]> classMeta = ReflectionService.newInstance().getClassMeta(DbObject[].class);

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();
        Object[] properties = {ArrayIndexStartAtProperty.ONE};

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty(matcher("elt1_id"), properties, (TypeAffinity) null, isValidPropertyMeta);
        assertNotNull(propEltId);
        assertEquals("[0].id", propEltId.getPath());
    }
    @Test
    public void testFindElementOnArraySpeculative() {

        ClassMeta<DbObject[]> classMeta = ReflectionService.newInstance().getClassMeta(DbObject[].class);

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();
        Object[] properties = {SpeculativeArrayIndexResolutionProperty.INSTANCE};

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty(matcher("elt0_id"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("2_id"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("id"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);
        Assert.assertEquals(1, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?, ?>) propEltId).getOwnerProperty()).getIndex());


        propEltId = propertyFinder.findProperty(matcher("id"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);
        assertEquals(3, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?, ?>) propEltId).getOwnerProperty()).getIndex());


        propEltId = propertyFinder.findProperty(matcher("name"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);
        assertEquals(0, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?, ?>) propEltId).getOwnerProperty()).getIndex());

        propEltId = propertyFinder.findProperty(matcher("2_notid"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNull(propEltId);


        propEltId = propertyFinder.findProperty(matcher("notid"), properties, (TypeAffinity)null, isValidPropertyMeta).compressSubSelf(); // will safe match
        assertTrue(propEltId instanceof  ArrayElementPropertyMeta);
        assertEquals(4, ((ArrayElementPropertyMeta)propEltId).getIndex());


    }

    @Test
    public void testFindElementOnArrayNonSpeculative__() {

        ClassMeta<DbObject[]> classMeta = ReflectionService.newInstance().getClassMeta(DbObject[].class);

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();
        Object[] properties = {};

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty(matcher("elt0_id"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);
        propEltId = propertyFinder.findProperty(matcher("id"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, isValidPropertyMeta);
        assertNull(propEltId);
    }

    @Test
    public void testFindElementOnArrayNonSpeculative() {

        ClassMeta<DbObject[]> classMeta = ReflectionService.newInstance().getClassMeta(DbObject[].class);

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();
        Object[] properties = {};

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty(matcher("elt0_id"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("2_id"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("id"), properties, (TypeAffinity)null,TestPropertyFinderProbe.INSTANCE, isValidPropertyMeta);
        assertNull(propEltId);


        propEltId = propertyFinder.findProperty(matcher("id"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNull(propEltId);


        propEltId = propertyFinder.findProperty(matcher("name"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);
        assertEquals(0, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?, ?>) propEltId).getOwnerProperty()).getIndex());

        propEltId = propertyFinder.findProperty(matcher("2_notid"), properties, (TypeAffinity)null, isValidPropertyMeta);
        assertNull(propEltId);


        propEltId = propertyFinder.findProperty(matcher("notid"), properties, (TypeAffinity)null, isValidPropertyMeta); // will safe match
        assertNull(propEltId);


    }

    private PropertyNameMatcher matcher(String col) {
        return new DefaultPropertyNameMatcher(col, 0, false, false);
    }

    @Test
    public void testFindElementOnTuple() {
        ClassMeta<DbObject[]> classMeta = ReflectionService.newInstance().getClassMeta(Tuples.typeDef(String.class, DbObject.class, DbObject.class));

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty(matcher("element2_id"), new Object[0], (TypeAffinity)null,  isValidPropertyMeta);
        assertEquals("element2.id", propEltId.getPath());

        propEltId = propertyFinder.findProperty(matcher("element1"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertEquals("element1", propEltId.getPath());

        propEltId = propertyFinder.findProperty(matcher("elt1"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertEquals("element1", propEltId.getPath());

        propEltId = propertyFinder.findProperty(matcher("1"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("id"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(propEltId);


        propEltId = propertyFinder.findProperty(matcher("4_id"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("2_notid"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertNull(propEltId);


        propEltId = propertyFinder.findProperty(matcher("elt0"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertEquals("element0", propEltId.getPath());

        propEltId = propertyFinder.findProperty(matcher("notid"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertNull(propEltId);

    }


    // https://github.com/arnaudroger/SimpleFlatMapper/issues/56
    @Test
    public void testArrayElementConstructorInjectionWithIncompatibleConstructorUseCompatibleOutlay() {
        ClassMeta<ObjectWithIncompatibleConstructor[]> classMeta = ReflectionService.newInstance().getClassMeta(ObjectWithIncompatibleConstructor[].class);

        PropertyFinder<ObjectWithIncompatibleConstructor[]> propertyFinder = classMeta.newPropertyFinder();

        assertNotNull(propertyFinder.findProperty(matcher("1_arg1"), new Object[0], (TypeAffinity)null, isValidPropertyMeta));
        assertNotNull(propertyFinder.findProperty(matcher("1_arg3"), new Object[0], (TypeAffinity)null, isValidPropertyMeta));
        assertNotNull(propertyFinder.findProperty(matcher("2_arg1"), new Object[0], (TypeAffinity)null, isValidPropertyMeta));
        assertNotNull(propertyFinder.findProperty(matcher("2_arg2"), new Object[0], (TypeAffinity)null, isValidPropertyMeta));
    }

    @Test
    public void testArrayElementConstructorInjectionWithIncompatibleConstructorUseIncompatibleOutlay() {
        ClassMeta<ObjectWithIncompatibleConstructor[]> classMeta = ReflectionService.newInstance().getClassMeta(ObjectWithIncompatibleConstructor[].class);

        PropertyFinder<ObjectWithIncompatibleConstructor[]> propertyFinder = classMeta.newPropertyFinder();

        assertNotNull(propertyFinder.findProperty(matcher("1_arg1"), new Object[0], (TypeAffinity)null, isValidPropertyMeta));
        assertNotNull(propertyFinder.findProperty(matcher("1_arg3"), new Object[0], (TypeAffinity)null, isValidPropertyMeta));
        assertNull(propertyFinder.findProperty(matcher("1_arg2"), new Object[0], (TypeAffinity)null, isValidPropertyMeta));
    }

    static class ObjectWithIncompatibleConstructor {
        private final String arg1;
        private final Long arg2;
        private final Integer arg3;

        public ObjectWithIncompatibleConstructor(String arg1, Long arg2) {
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = null;
        }
        public ObjectWithIncompatibleConstructor(String arg1, Integer arg3) {
            this.arg1 = arg1;
            this.arg2 = null;
            this.arg3 = arg3;
        }
    }
    
    @Test
    public void testIsEnabled531() {
        ClassMeta<O531> classMeta = ReflectionService.newInstance().getClassMeta(O531.class);

        Predicate<PropertyMeta<?, ?>> p = new Predicate<PropertyMeta<?, ?>>() {
            @Override
            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                return !NullGetter.isNull(propertyMeta.getGetter());
            }
        };

        PropertyFinder.PropertyFilter predicate = new PropertyFinder.PropertyFilter(p);

        PropertyFinder<O531> propertyFinder = classMeta.newPropertyFinder();
        propertyFinder.findProperty(DefaultPropertyNameMatcher.of("id"), new Object[0], (TypeAffinity) null, predicate);
        PropertyMeta<O531, Object> property = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("is_enabled"), new Object[0], (TypeAffinity) null, predicate);

        assertNotNull(property);

    }

    public static class O531 {
        private final boolean isEnabled;

        public O531(boolean isEnabled, int id) {
            this.isEnabled = isEnabled;
            this.id = id;
        }

        private final int id;

        public boolean isEnabled() {
            return isEnabled;
        }

        public int getId() {
            return id;
        }
    }


    @Test
    public void testOptionalPropReturNotMappedProperty664() {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);

        PropertyFinder<DbObject> finder = classMeta.newPropertyFinder();

        PropertyMeta<DbObject, Object> id = finder.findProperty(DefaultPropertyNameMatcher.of("id"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(id);

        PropertyMeta<DbObject, Object> type = finder.findProperty(DefaultPropertyNameMatcher.of("typo"), new Object[] { new OptionalProperty() {}, new EligibleAsNonMappedProperty() {}}, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(type);
        assertTrue(type.isNonMapped());

    }

    @Test
    public void testOptionalPropReturNotMappedPropertyOnDeepObject664() {
        ClassMeta<A> classMeta = ReflectionService.newInstance().getClassMeta(A.class);

        PropertyFinder<A> finder = classMeta.newPropertyFinder();

        finder.findProperty(DefaultPropertyNameMatcher.of("b_c_val"), new Object[] { }, (TypeAffinity)null, isValidPropertyMeta);
        PropertyMeta<A, Object> type = finder.findProperty(DefaultPropertyNameMatcher.of("b_c_type"), new Object[] { new OptionalProperty() {}, new EligibleAsNonMappedProperty() {}}, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(type);
        assertTrue(type instanceof  SubPropertyMeta);
        assertEquals("b.c.type", type.getPath());

    }

    @Test
    public void testOptionalPropReturnNotMappedPropertyOnSelfInList664() {
        ClassMeta<D> classMeta = ReflectionService.newInstance().getClassMeta(D.class);

        PropertyFinder<D> finder = classMeta.newPropertyFinder();

        assertNotNull(finder.findProperty(DefaultPropertyNameMatcher.of("items_item"), new Object[] { }, (TypeAffinity)null, isValidPropertyMeta));
        PropertyMeta<D, Object> type = finder.findProperty(DefaultPropertyNameMatcher.of("items_type"), new Object[] { new OptionalProperty() {}, new EligibleAsNonMappedProperty() {}}, (TypeAffinity)null, isValidPropertyMeta);
        assertNotNull(type);
        assertTrue(type instanceof  SubPropertyMeta);
        assertEquals("items[0].type", type.getPath());

    }

    @Test
    public void testOptionalPropReturnNotMappedPropertyOnSelfInList664_difforder() {
        ClassMeta<D> classMeta = ReflectionService.newInstance().getClassMeta(D.class);

        PropertyFinder<D> finder = classMeta.newPropertyFinder();

        PropertyMeta<D, Object> type = finder.findProperty(DefaultPropertyNameMatcher.of("items_type"), new Object[] { new OptionalProperty() {}, new EligibleAsNonMappedProperty() {}}, (TypeAffinity)null, isValidPropertyMeta);
        PropertyMeta<D, Object> items_item = finder.findProperty(DefaultPropertyNameMatcher.of("items_item"), new Object[]{}, (TypeAffinity) null, isValidPropertyMeta);
        //assertNotNull(items_item);
        //assertFalse(type.isValid());
    }


    public static class A {
        public B b;
    }
    public static class B {
        public C c;
    }

    public static class C {
        public String val;
    }

    public static class D {
        public List<String> items;
    }


    @Test
    public void testPR678() {
        ClassMeta<Entity> classMeta = ReflectionService.newInstance().getClassMeta(Entity.class);

        PropertyFinder<Entity> finder = classMeta.newPropertyFinder();

        PropertyMeta<Entity, Object> start = finder.findProperty(DefaultPropertyNameMatcher.of("scheduled_start_date"), new Object[] { }, (TypeAffinity)null, TestPropertyFinderProbe.INSTANCE, isValidPropertyMeta);
        assertEquals("scheduledDate.startDate", start.getPath());


        PropertyMeta<Entity, Object> end = finder.findProperty(DefaultPropertyNameMatcher.of("scheduled_end_date"), new Object[]{}, (TypeAffinity) null, isValidPropertyMeta);
        assertEquals("scheduledDate.endDate", end.getPath());
    }
    public static class Entity {
        public DateRange scheduledDate;
    }
    public static class DateRange {
        public String startDate;
        public String endDate;
    }


    @Test
    public void testPrefixMatchScoring() {
        ClassMeta<List<DbObject>> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<List<DbObject>>() {}.getType());

        PropertyFinder<List<DbObject>> finder = classMeta.newPropertyFinder();

        PropertyMeta<List<DbObject>, Object> t1 = finder
                .findProperty(DefaultPropertyNameMatcher.of("type_name"), new Object[] {SpeculativeArrayIndexResolutionProperty.INSTANCE}, (TypeAffinity)null, TestPropertyFinderProbe.INSTANCE, isValidPropertyMeta);
        assertNotNull(t1);
        assertEquals("[0].typeName", t1.getPath());

        PropertyMeta<List<DbObject>, Object> t2 = finder.findProperty(DefaultPropertyNameMatcher.of("type_name"), new Object[] { SpeculativeArrayIndexResolutionProperty.INSTANCE}, (TypeAffinity)null, TestPropertyFinderProbe.INSTANCE, isValidPropertyMeta);
        assertNotNull(t2);
        assertEquals("[1].typeName", t2.getPath());
    }

    @Test
    public void testPartialMatch() {
        ClassMeta<PatialMatch> classMeta = ReflectionService.newInstance().getClassMeta(PatialMatch.class);

        PropertyMeta<PatialMatch, Object> localDate = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("localDate"), new Object[0], (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());

        assertEquals("localDate", localDate.getPath());
    }


    public static class PatialMatch {
        public String localDate;
        public String localDateTime;
    }



    @Test
    public void test668() {

        ClassMeta<Root> classMeta = ReflectionService.newInstance().getClassMeta(Root.class);


        PropertyFinder<Root> rootPropertyFinder = classMeta.newPropertyFinder();

        PropertyMeta<Root, Object> bar1 = rootPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("foos_bar1_id"), new Object[0], (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertEquals("foos[0].bar1.id", bar1.getPath());

        PropertyMeta<Root, Object> bar2 = rootPropertyFinder.findProperty(DefaultPropertyNameMatcher.of("foos_bar2_id"), new Object[0], (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertEquals("foos[0].bar2.id", bar2.getPath());

    }


    public static class Root {
        private String id;
        private String name;
        private List<Foo> foos;

        public Root() {
        }

        public Root(final String id, final String name, final List<Foo> foos) {
            this.id = id;
            this.name = name;
            this.foos = foos;
        }
    }

    public static class Foo {
        private String id;
        private Bar bar1;
        private Bar bar2;

        public Foo() {
        }

        public Foo(final String id, final Bar bar1, final Bar bar2) {
            this.id = id;
            this.bar1 = bar1;
            this.bar2 = bar2;
        }
    }

    public static class Bar {
        private String id;

        public Bar() {
        }

        public Bar(final String id) {
            this.id = id;
        }
    }


    @Test
    public void testSpeculativeObject() {
        ClassMeta<S1> classMeta = ReflectionService.newInstance().getClassMeta(S1.class);


        PropertyFinder<S1> pf = classMeta.newPropertyFinder();

        Object[] properties = new Object[] {SpeculativeObjectLookUpProperty.INSTANCE};
        PropertyMeta<S1, Object> pm = pf.findProperty(DefaultPropertyNameMatcher.of("foo"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNotNull(pm);
        assertEquals("s2.s3.foo", pm.getPath());

        pm = pf.findProperty(DefaultPropertyNameMatcher.of("bar"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNotNull(pm);
        assertEquals("s2.s4.bar", pm.getPath());

        pm = pf.findProperty(DefaultPropertyNameMatcher.of("foo"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNull(pm);

        pm = pf.findProperty(DefaultPropertyNameMatcher.of("bar"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNull(pm);


        pm = pf.findProperty(DefaultPropertyNameMatcher.of("id"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNotNull(pm);
        assertEquals("id", pm.getPath());

        pm = pf.findProperty(DefaultPropertyNameMatcher.of("id"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNotNull(pm);
        assertEquals("s2.id", pm.getPath());

        pm = pf.findProperty(DefaultPropertyNameMatcher.of("id"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNotNull(pm);
        assertEquals("s2.s3.id", pm.getPath());

        pm = pf.findProperty(DefaultPropertyNameMatcher.of("id"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNotNull(pm);
        assertEquals("s2.s4.id", pm.getPath());

        pm = pf.findProperty(DefaultPropertyNameMatcher.of("id"), properties, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());
        assertNotNull(pm);
        assertEquals("id", pm.getPath());





    }

    private static class S1 {
        public String id;
        public S2 s2;
    }
    private static class S2 {
        public String id;
        public S3 s3;
        public S4 s4;
    }
    private static class S3 {
        public String id;
        public String foo;
    }
    private static class S4 {
        public String id;
        public String bar;

    }


    @Test
    public void testPlural() {
        ClassMeta<P> classMeta = ReflectionService.newInstance().getClassMeta(P.class);

        PropertyMeta<P, Object> prop = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value_v"), new Object[]{}, (TypeAffinity) null, TestPropertyFinderProbe.INSTANCE, PropertyFinder.PropertyFilter.trueFilter());

        assertEquals("values[0].{this}", prop.getPath());

    }

    public static class P {
        public final List<String> values;

        public P(List<String> values) {
            this.values = values;
        }
    }
}
