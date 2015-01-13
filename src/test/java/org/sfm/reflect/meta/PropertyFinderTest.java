package org.sfm.reflect.meta;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;
import org.sfm.tuples.Tuples;

import static org.junit.Assert.*;

/**
 * Created by aroger on 05/12/14.
 */
public class PropertyFinderTest {


    @Test
    public void testFindElementOnArray() {

        ClassMeta<DbObject[]> classMeta = ReflectionService.newInstance().getClassMeta(DbObject[].class);

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty(matcher("elt0_id"));
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("2_id"));
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("id"));
        assertNotNull(propEltId);
        assertEquals(1, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?>) propEltId).getOwnerProperty()).getIndex());


        propEltId = propertyFinder.findProperty(matcher("id"));
        assertNotNull(propEltId);
        assertEquals(3, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?>) propEltId).getOwnerProperty()).getIndex());


        propEltId = propertyFinder.findProperty(matcher("name"));
        assertNotNull(propEltId);
        assertEquals(0, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?>) propEltId).getOwnerProperty()).getIndex());

        propEltId = propertyFinder.findProperty(matcher("2_notid"));
        assertNull(propEltId);


        propEltId = propertyFinder.findProperty(matcher("notid"));
        assertNull(propEltId);


    }

    private PropertyNameMatcher matcher(String col) {
        return new DefaultPropertyNameMatcher(col);
    }

    @Test
    public void testFindElementOnTuple() {
        ClassMeta<DbObject[]> classMeta = ReflectionService.newInstance().getClassMeta(Tuples.typeDef(String.class, DbObject.class, DbObject.class));

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty(matcher("element2_id"));
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("element1"));
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("elt1"));
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("1"));
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("id"));
        assertNotNull(propEltId);


        propEltId = propertyFinder.findProperty(matcher("4_id"));
        assertNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("2_notid"));
        assertNull(propEltId);

        propEltId = propertyFinder.findProperty(matcher("notid"));
        assertNull(propEltId);

    }


    // https://github.com/arnaudroger/SimpleFlatMapper/issues/56
    @Test
    public void testArrayElementConstructorInjectionWithIncompatibleConstructorUseCompatibleOutlay() {
        ClassMeta<ObjectWithIncompatibleConstructor[]> classMeta = ReflectionService.newInstance().getClassMeta(ObjectWithIncompatibleConstructor[].class);

        PropertyFinder<ObjectWithIncompatibleConstructor[]> propertyFinder = classMeta.newPropertyFinder();

        assertNotNull(propertyFinder.findProperty(matcher("1_arg1")));
        assertNotNull(propertyFinder.findProperty(matcher("1_arg3")));
        assertNotNull(propertyFinder.findProperty(matcher("2_arg1")));
        assertNotNull(propertyFinder.findProperty(matcher("2_arg2")));
    }

    @Test
    public void testArrayElementConstructorInjectionWithIncompatibleConstructorUseIncompatibleOutlay() {
        ClassMeta<ObjectWithIncompatibleConstructor[]> classMeta = ReflectionService.newInstance().getClassMeta(ObjectWithIncompatibleConstructor[].class);

        PropertyFinder<ObjectWithIncompatibleConstructor[]> propertyFinder = classMeta.newPropertyFinder();

        assertNotNull(propertyFinder.findProperty(matcher("1_arg1")));
        assertNotNull(propertyFinder.findProperty(matcher("1_arg3")));
        assertNull(propertyFinder.findProperty(matcher("1_arg2")));
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
}
