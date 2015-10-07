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
    public void testSubProperty() throws Exception {
        ClassMeta<Db1DeepObject> classMeta = ReflectionService.newInstance().getClassMeta(Db1DeepObject.class);

        PropertyMeta<Db1DeepObject, String> property = classMeta.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("dbObject_name", 0, false, false));

        assertTrue(property instanceof SubPropertyMeta);
        assertTrue(property.isSubProperty());
        assertTrue(property.toString().startsWith("SubPropertyMeta{" +
                "ownerProperty=ObjectPropertyMeta{"));

        SubPropertyMeta<Db1DeepObject, DbObject, String> subPropertyMeta = (SubPropertyMeta<Db1DeepObject, DbObject, String>) property;

        assertEquals(String.class, subPropertyMeta.getPropertyType());

        assertEquals("dbObject.name", subPropertyMeta.getPath());

        Db1DeepObject object = new Db1DeepObject();
        object.setDbObject(new DbObject());
        subPropertyMeta.getSetter().set(object, "n1");
        assertEquals("n1", subPropertyMeta.getGetter().get(object));

        ClassMeta<?> meta = subPropertyMeta.newPropertyClassMeta();
        assertEquals(String.class, meta.getType());
    }
}
