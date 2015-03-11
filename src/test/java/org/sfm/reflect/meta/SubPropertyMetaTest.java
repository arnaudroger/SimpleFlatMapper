package org.sfm.reflect.meta;


import org.junit.Test;
import org.sfm.beans.Db1DeepObject;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubPropertyMetaTest {

    @Test
    public void testSubProperty() {
        ClassMeta<Db1DeepObject> classMeta = ReflectionService.newInstance().getRootClassMeta(Db1DeepObject.class);

        PropertyMeta<Db1DeepObject, String> property = classMeta.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("dbObject_name", 0, false, false));

        assertTrue(property instanceof SubPropertyMeta);
        assertTrue(property.isSubProperty());
        assertEquals("SubPropertyMeta{" +
                "ownerProperty=MethodPropertyMeta{setter=public void org.sfm.beans.Db1DeepObject.setDbObject(org.sfm.beans.DbObject), getter=null, type=class org.sfm.beans.DbObject}, " +
                "subProperty=MethodPropertyMeta{setter=public void org.sfm.beans.DbObject.setName(java.lang.String), getter=null, type=class java.lang.String}}", property.toString());

        SubPropertyMeta<Db1DeepObject, String> subPropertyMeta = (SubPropertyMeta<Db1DeepObject, String>) property;

        assertEquals(DbObject.class, subPropertyMeta.getType());
        assertEquals(String.class, subPropertyMeta.getLeafType());

        assertEquals("dbObject.name", subPropertyMeta.getPath());
        assertEquals(subPropertyMeta.getOwnerProperty().newGetter(), subPropertyMeta.newGetter());
        assertEquals(subPropertyMeta.getOwnerProperty().newSetter(), subPropertyMeta.newSetter());
        assertEquals(subPropertyMeta.getOwnerProperty().getClassMeta(), subPropertyMeta.newClassMeta());


    }
}
