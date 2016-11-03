package org.simpleflatmapper.reflect.test.meta;


import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.test.beans.Db1DeepObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.reflect.ReflectionService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SubPropertyMetaTest {

    @Test
    @SuppressWarnings("unchecked")
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
        Getter<Db1DeepObject, String> getter = subPropertyMeta.getGetter();
        assertEquals("n1", getter.get(object));

        Db1DeepObject objectNull = new Db1DeepObject();
        assertEquals(null, getter.get(objectNull));

        assertTrue(getter.toString().startsWith("GetterOnGetter{g1="));

        ClassMeta<?> meta = subPropertyMeta.getPropertyClassMeta();
        assertEquals(String.class, meta.getType());
    }
}
