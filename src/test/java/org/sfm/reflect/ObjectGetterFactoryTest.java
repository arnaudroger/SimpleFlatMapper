package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.*;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.MethodGetter;
import org.sfm.reflect.primitive.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

    private DbFinalPrimitiveObject dbFinalPrimitiveObject = new DbFinalPrimitiveObject(true, (byte)1, (char)2, (short)3, 4, 5l, 6.0f, 7.0);

    @Test
    public void testObjectGetterNoAsm() throws Exception {
        assertEquals(dbo.getName(), noAsm.getGetter(DbObject.class, "name").get(dbo));
        assertEquals(dbfo.getName(), noAsm.getGetter(DbFinalObject.class, "name").get(dbfo));
        assertEquals(dbpo.name, noAsm.getGetter(DbPublicObject.class, "name").get(dbpo));
    }
    @Test
    public void testObjectGetterAsm() throws Exception {
        assertEquals(dbo.getName(), asm.getGetter(DbObject.class, "name").get(dbo));
        assertFalse(asm.getGetter(DbObject.class, "name") instanceof MethodGetter);
        assertEquals(dbfo.getName(), asm.getGetter(DbFinalObject.class, "name").get(dbfo));
        assertFalse(asm.getGetter(DbFinalObject.class, "name") instanceof MethodGetter);

    }

    @Test
    public void testExtension() throws Exception {
        Foo foo = new Foo();
        new ObjectSetterFactory(null).getSetter(Foo.class, "bar").set(foo, "bar");
        assertEquals("bar", noAsm.getGetter(Foo.class, "bar").get(foo));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPrimitiveGetterAsm() throws Exception {
        assertEquals(dbFinalPrimitiveObject.ispBoolean(),    ((BooleanGetter<DbFinalPrimitiveObject>)asm.getGetter(DbFinalPrimitiveObject.class, "pBoolean")).getBoolean(dbFinalPrimitiveObject));
        assertEquals(dbFinalPrimitiveObject.getpByte(),      ((ByteGetter<DbFinalPrimitiveObject>)asm.getGetter(DbFinalPrimitiveObject.class, "pByte")).getByte(dbFinalPrimitiveObject));
        assertEquals(dbFinalPrimitiveObject.getpCharacter(), ((CharacterGetter<DbFinalPrimitiveObject>)asm.getGetter(DbFinalPrimitiveObject.class, "pCharacter")).getCharacter(dbFinalPrimitiveObject));
        assertEquals(dbFinalPrimitiveObject.getpShort(),     ((ShortGetter<DbFinalPrimitiveObject>)asm.getGetter(DbFinalPrimitiveObject.class, "pShort")).getShort(dbFinalPrimitiveObject));
        assertEquals(dbFinalPrimitiveObject.getpInt(),       ((IntGetter<DbFinalPrimitiveObject>)asm.getGetter(DbFinalPrimitiveObject.class, "pInt")).getInt(dbFinalPrimitiveObject));
        assertEquals(dbFinalPrimitiveObject.getpLong(),      ((LongGetter<DbFinalPrimitiveObject>)asm.getGetter(DbFinalPrimitiveObject.class, "pLong")).getLong(dbFinalPrimitiveObject));
        assertEquals(dbFinalPrimitiveObject.getpFloat(),     ((FloatGetter<DbFinalPrimitiveObject>)asm.getGetter(DbFinalPrimitiveObject.class, "pFloat")).getFloat(dbFinalPrimitiveObject), 0.0);
        assertEquals(dbFinalPrimitiveObject.getpDouble(),    ((DoubleGetter<DbFinalPrimitiveObject>)asm.getGetter(DbFinalPrimitiveObject.class, "pDouble")).getDouble(dbFinalPrimitiveObject), 0.0);

    }

}
