package org.sfm.reflect.meta;


import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.reflect.ReflectionService;

import static org.junit.Assert.assertEquals;

public class ConstructorPropertyMetaTest {

    @Test
    public void testMeta() throws Exception {
        ClassMeta<DbFinalObject> classMeta = ReflectionService.newInstance().getRootClassMeta(DbFinalObject.class);

        PropertyMeta<DbFinalObject, Object> property = classMeta.newPropertyFinder().findProperty(new DefaultPropertyNameMatcher("name", 0, false, false));

        DbFinalObject o = new DbFinalObject(0, "nn", "nn", null, null, null);

        assertEquals("ConstructorPropertyMeta{" +
                "owner=class org.sfm.beans.DbFinalObject, " +
                "constructorParameter=Parameter{name='name', type=class java.lang.String, resolvedType=class java.lang.String}}",
                property.toString());
        property.newSetter().set(o, "jj");
        assertEquals("nn", property.newGetter().get(o));
    }
}
