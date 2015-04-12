package org.sfm.reflect.meta;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.Foo;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.tuples.Tuple2;
import org.sfm.tuples.Tuples;

import static org.junit.Assert.*;

public class TuplesClassMetaTest {


    @Test
    public void testGenerateHeaders() {
        String[] names = {"element0_id", "element0_name", "element0_email", "element0_creationTime", "element0_typeOrdinal", "element0_typeName", "element1"};
        assertArrayEquals(
                names,
                ReflectionService.newInstance().getClassMeta(Tuples.typeDef(DbObject.class, String.class)).generateHeaders());
    }


    ClassMeta<Tuple2<Foo, Foo>> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<Tuple2<Foo, Foo>>() {}.getType());

    @Test
    public void testIndexStartingAtZero() {
        final PropertyFinder<Tuple2<Foo, Foo>> propertyFinder = classMeta.newPropertyFinder();

        final PropertyMeta<Tuple2<Foo, Foo>, String> t0_foo = propertyFinder.findProperty(newMatcher("t0_foo"));
        final PropertyMeta<Tuple2<Foo, Foo>, String> t0_bar = propertyFinder.findProperty(newMatcher("t0_bar"));
        final PropertyMeta<Tuple2<Foo, Foo>, String> t1_foo = propertyFinder.findProperty(newMatcher("t1_foo"));
        final PropertyMeta<Tuple2<Foo, Foo>, String> t1_bar = propertyFinder.findProperty(newMatcher("t1_bar"));

        validate(t0_foo, t0_bar, t1_foo, t1_bar);

    }

    private void validate(PropertyMeta<Tuple2<Foo, Foo>, String> t0_foo,
                          PropertyMeta<Tuple2<Foo, Foo>, String> t0_bar,
                          PropertyMeta<Tuple2<Foo, Foo>, String> t1_foo,
                          PropertyMeta<Tuple2<Foo, Foo>, String> t1_bar) {

        assertNotNull(t0_foo);
        assertIs("element0", "foo", t0_foo);
        assertNotNull(t0_bar);
        assertIs("element0", "bar", t0_bar);

        assertNotNull(t1_foo);
        assertIs("element0", "foo", t0_foo);
        assertNotNull(t1_foo);
        assertIs("element0", "bar", t0_bar);

    }

    private void assertIs(String elementName, String prop, PropertyMeta<Tuple2<Foo, Foo>, String> propertyMeta) {
        assertTrue(propertyMeta.isSubProperty());
        SubPropertyMeta<Tuple2<Foo, Foo>, String> subPropertyMeta = (SubPropertyMeta<Tuple2<Foo, Foo>, String>) propertyMeta;

        assertEquals(elementName, subPropertyMeta.getOwnerProperty().getName());
        assertEquals(prop, subPropertyMeta.getSubProperty().getName());
    }

    private PropertyNameMatcher newMatcher(String name) {
        return new DefaultPropertyNameMatcher(name, 0, false, false);
    }

//    @Test
//    public void testIndexStartingAtOne() {
//        final PropertyFinder<Tuple2<Foo, Foo>> propertyFinder = classMeta.newPropertyFinder();
//
//        final PropertyMeta<Tuple2<Foo, Foo>, String> t0_foo = propertyFinder.findProperty(newMatcher("t1_foo"));
//        final PropertyMeta<Tuple2<Foo, Foo>, String> t0_bar = propertyFinder.findProperty(newMatcher("t1_bar"));
//        final PropertyMeta<Tuple2<Foo, Foo>, String> t1_foo = propertyFinder.findProperty(newMatcher("t2_foo"));
//        final PropertyMeta<Tuple2<Foo, Foo>, String> t1_bar = propertyFinder.findProperty(newMatcher("t2_bar"));
//        validate(t0_foo, t0_bar, t1_foo, t1_bar);
//
//    }

    @Test
    public void testIndexStartingFlexiblePrefix() {
        final PropertyFinder<Tuple2<Foo, Foo>> propertyFinder = classMeta.newPropertyFinder();

        final PropertyMeta<Tuple2<Foo, Foo>, String> t0_foo = propertyFinder.findProperty(newMatcher("ta_foo"));
        final PropertyMeta<Tuple2<Foo, Foo>, String> t0_bar = propertyFinder.findProperty(newMatcher("ta_bar"));
        final PropertyMeta<Tuple2<Foo, Foo>, String> t1_foo = propertyFinder.findProperty(newMatcher("tb_foo"));
        final PropertyMeta<Tuple2<Foo, Foo>, String> t1_bar = propertyFinder.findProperty(newMatcher("tb_bar"));
        validate(t0_foo, t0_bar, t1_foo, t1_bar);

    }


}
