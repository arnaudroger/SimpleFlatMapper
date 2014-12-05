package org.sfm.reflect.meta;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;
import org.sfm.tuples.Tuple2;
import org.sfm.tuples.Tuples;

/**
 * Created by aroger on 05/12/14.
 */
public class PropertyFinderTest {


    @Test
    public void testFindElementOnArray() {

        ClassMeta<DbObject[]> classMeta = new ReflectionService().getClassMeta(DbObject[].class);

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty("elt0_id");
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty("2_id");
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty("id");
        assertNotNull(propEltId);
        assertEquals(3, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?>) propEltId).getOwnerProperty()).getIndex());


        propEltId = propertyFinder.findProperty("id");
        assertNotNull(propEltId);
        assertEquals(4, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?>) propEltId).getOwnerProperty()).getIndex());


        propEltId = propertyFinder.findProperty("name");
        assertNotNull(propEltId);
        assertEquals(0, ((ArrayElementPropertyMeta<?, ?>) ((SubPropertyMeta<?, ?>) propEltId).getOwnerProperty()).getIndex());

    }

    @Test
    public void testFindElementOnTuple() {
        ClassMeta<DbObject[]> classMeta = new ReflectionService().getClassMeta(Tuples.typeDef(String.class, DbObject.class));

        PropertyFinder<DbObject[]> propertyFinder = classMeta.newPropertyFinder();

        PropertyMeta<DbObject[], ?> propEltId = propertyFinder.findProperty("element2_id");
        assertNotNull(propEltId);

        propEltId = propertyFinder.findProperty("element1");
        assertNotNull(propEltId);

//        propEltId = propertyFinder.findProperty("elt1");
//        assertNotNull(propEltId);
//
//        propEltId = propertyFinder.findProperty("1");
//        assertNotNull(propEltId);

    }
}
