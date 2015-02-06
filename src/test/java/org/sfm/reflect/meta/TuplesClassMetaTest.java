package org.sfm.reflect.meta;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;
import org.sfm.tuples.Tuples;

import static org.junit.Assert.assertArrayEquals;

public class TuplesClassMetaTest {


    @Test
    public void testGenerateHeaders() {
        String[] names = {"element0_id", "element0_name", "element0_email", "element0_creationTime", "element0_typeOrdinal", "element0_typeName", "element1"};
        assertArrayEquals(
                names,
                ReflectionService.newInstance().getRootClassMeta(Tuples.typeDef(DbObject.class, String.class)).generateHeaders());
    }

}
