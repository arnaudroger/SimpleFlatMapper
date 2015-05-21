package org.sfm.reflect.meta;


import org.junit.Test;
import org.sfm.beans.Db1DeepObject;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SubPropertyMetaTest {

    @Test
    public void testSubProperty() {
        ClassMeta<Db1DeepObject> classMeta = ReflectionService.newInstance().getClassMeta(Db1DeepObject.class);

        PropertyMeta<Db1DeepObject, String> property = classMeta.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("dbObject_name", 0, false, false));

        assertTrue(property instanceof SubPropertyMeta);
        assertTrue(property.isSubProperty());
        assertEquals("SubPropertyMeta{" +
                "ownerProperty=MethodPropertyMeta{setter=public void org.sfm.beans.Db1DeepObject.setDbObject(org.sfm.beans.DbObject), getter=null, type=class org.sfm.beans.DbObject}, " +
                "subProperty=MethodPropertyMeta{setter=public void org.sfm.beans.DbObject.setName(java.lang.String), getter=null, type=class java.lang.String}}", property.toString());

        SubPropertyMeta<Db1DeepObject, DbObject, String> subPropertyMeta = (SubPropertyMeta<Db1DeepObject, DbObject, String>) property;

        assertEquals(String.class, subPropertyMeta.getPropertyType());

        assertEquals("dbObject.name", subPropertyMeta.getPath());

        try {
            subPropertyMeta.newGetter();
            fail();
        } catch(UnsupportedOperationException e) {
        }
        try {
            subPropertyMeta.newSetter();
            fail();
        } catch(UnsupportedOperationException e) {
        }

        ClassMeta<?> meta = subPropertyMeta.newPropertyClassMeta();
        assertEquals(String.class, meta.getType());
    }
}
