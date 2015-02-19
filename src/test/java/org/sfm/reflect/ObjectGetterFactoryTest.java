package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPublicObject;
import org.sfm.reflect.asm.AsmFactory;

import static org.junit.Assert.assertEquals;

public class ObjectGetterFactoryTest {

    private ObjectGetterFactory asm = new ObjectGetterFactory(new AsmFactory(getClass().getClassLoader()));
    private ObjectGetterFactory noAsm = new ObjectGetterFactory(null);

    private DbObject dbo = new DbObject();
    {
        dbo.setName("v1");
    }
    private DbFinalObject dbfo = new DbFinalObject(0, "v1", null, null, null, null);

    private DbPublicObject dbpo = new DbPublicObject();
    {
        dbpo.name = "v1";
    }

    @Test
    public void testObjectGetterNoAsm() throws Exception {
        assertEquals(dbo.getName(), noAsm.getGetter(DbObject.class, "name").get(dbo));
        assertEquals(dbfo.getName(), noAsm.getGetter(DbFinalObject.class, "name").get(dbfo));
        assertEquals(dbpo.name, noAsm.getGetter(DbPublicObject.class, "name").get(dbpo));
    }
    @Test
    public void testObjectGetterAsm() throws Exception {
        assertEquals(dbo.getName(), asm.getGetter(DbObject.class, "name").get(dbo));
        assertEquals(dbfo.getName(), asm.getGetter(DbFinalObject.class, "name").get(dbfo));

    }
}
