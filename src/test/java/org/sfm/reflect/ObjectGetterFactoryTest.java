package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.*;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.FieldGetter;
import org.sfm.reflect.impl.MethodGetter;
import org.sfm.reflect.primitive.*;

import static org.junit.Assert.*;

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
    public void testBytePrimitiveAsmIsByteGetter() throws Exception {
        final Getter<?, ?> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pByte");
        assertTrue(getter instanceof ByteGetter);
        assertSame(getter, ObjectGetterFactory.toByteGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpByte(), ((ByteGetter)getter).getByte(dbFinalPrimitiveObject));

    }
    @Test
    public void testBytePrimitiveNoAsmIsByteGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, ?> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pByte");
        assertFalse(getter instanceof ByteGetter);
        final ByteGetter<DbFinalPrimitiveObject> byteGetter = ObjectGetterFactory.toByteGetter(getter);
        assertTrue(byteGetter instanceof ByteGetter);
        assertEquals(dbFinalPrimitiveObject.getpByte(), byteGetter.getByte(dbFinalPrimitiveObject));
    }

    @Test
    public void testCharPrimitiveAsmIsCharGetter() throws Exception {
        final Getter<?, ?> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pChar");
        assertTrue(getter instanceof CharacterGetter);
        assertSame(getter, ObjectGetterFactory.toCharGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpCharacter(), ((CharacterGetter)getter).getCharacter(dbFinalPrimitiveObject));

    }
    @Test
    public void testCharPrimitiveNoAsmIsCharGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, ?> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pChar");
        assertFalse(getter instanceof CharacterGetter);
        final CharacterGetter<DbFinalPrimitiveObject> charGetter = ObjectGetterFactory.toCharGetter(getter);
        assertTrue(charGetter instanceof CharacterGetter);
        assertEquals(dbFinalPrimitiveObject.getpCharacter(), charGetter.getCharacter(dbFinalPrimitiveObject));
    }

    @Test
    public void testShortPrimitiveAsmIsShortGetter() throws Exception {
        final Getter<?, ?> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pShort");
        assertTrue(getter instanceof ShortGetter);
        assertSame(getter, ObjectGetterFactory.toShortGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpShort(), ((ShortGetter)getter).getShort(dbFinalPrimitiveObject));
    }
    @Test
    public void testShortPrimitiveNoAsmIsShortGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, ?> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pShort");
        assertFalse(getter instanceof ShortGetter);
        final ShortGetter<DbFinalPrimitiveObject> shortGetter = ObjectGetterFactory.toShortGetter(getter);
        assertTrue(shortGetter instanceof ShortGetter);
        assertEquals(3, shortGetter.getShort(dbFinalPrimitiveObject));
        assertEquals(dbFinalPrimitiveObject.getpShort(), shortGetter.getShort(dbFinalPrimitiveObject));
    }


    @Test
    public void testIntPrimitiveAsmIsIntGetter() throws Exception {
        final Getter<?, ?> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pInt");
        assertTrue(getter instanceof IntGetter);
        assertSame(getter, ObjectGetterFactory.toIntGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpInt(), ((IntGetter)getter).getInt(dbFinalPrimitiveObject));

    }
    @Test
    public void testIntPrimitiveNoAsmIsIntGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, ?> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pInt");
        assertFalse(getter instanceof IntGetter);
        final IntGetter<DbFinalPrimitiveObject> intGetter = ObjectGetterFactory.toIntGetter(getter);
        assertTrue(intGetter instanceof IntGetter);
        assertEquals(dbFinalPrimitiveObject.getpInt(), intGetter.getInt(dbFinalPrimitiveObject));

    }


    @Test
    public void testLongPrimitiveAsmIsLongGetter() throws Exception {
        final Getter<?, ?> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pLong");
        assertTrue(getter instanceof LongGetter);
        assertSame(getter, ObjectGetterFactory.toLongGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpLong(), ((LongGetter)getter).getLong(dbFinalPrimitiveObject));

    }
    @Test
    public void testLongPrimitiveNoAsmIsLongGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, ?> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pLong");
        assertFalse(getter instanceof LongGetter);
        final LongGetter<DbFinalPrimitiveObject> longGetter = ObjectGetterFactory.toLongGetter(getter);
        assertTrue(longGetter instanceof LongGetter);
        assertEquals(dbFinalPrimitiveObject.getpLong(), longGetter.getLong(dbFinalPrimitiveObject));

    }

    @Test
    public void testFloatPrimitiveAsmIsFloatGetter() throws Exception {
        final Getter<?, ?> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pFloat");
        assertTrue(getter instanceof FloatGetter);
        assertSame(getter, ObjectGetterFactory.toFloatGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpFloat(), ((FloatGetter)getter).getFloat(dbFinalPrimitiveObject), 0.0001);

    }
    @Test
    public void testFloatPrimitiveNoAsmIsFloatGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, ?> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pFloat");
        assertFalse(getter instanceof FloatGetter);
        final FloatGetter<DbFinalPrimitiveObject> longGetter = ObjectGetterFactory.toFloatGetter(getter);
        assertTrue(longGetter instanceof FloatGetter);
        assertEquals(dbFinalPrimitiveObject.getpFloat(), longGetter.getFloat(dbFinalPrimitiveObject), 0.0001);

    }


    @Test
    public void testDoublePrimitiveAsmIsDoubleGetter() throws Exception {
        final Getter<?, ?> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pDouble");
        assertTrue(getter instanceof DoubleGetter);
        assertSame(getter, ObjectGetterFactory.toDoubleGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpDouble(), ((DoubleGetter)getter).getDouble(dbFinalPrimitiveObject), 0.0001);

    }
    @Test
    public void testDoublePrimitiveNoAsmIsDoubleGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, ?> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pDouble");
        assertFalse(getter instanceof DoubleGetter);
        final DoubleGetter<DbFinalPrimitiveObject> longGetter = ObjectGetterFactory.toDoubleGetter(getter);
        assertTrue(longGetter instanceof DoubleGetter);
        assertEquals(dbFinalPrimitiveObject.getpDouble(), longGetter.getDouble(dbFinalPrimitiveObject), 0.0001);

    }

    @Test
    public void testBooleanPrimitiveAsmIsBooleanGetter() throws Exception {
        final Getter<?, ?> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pBoolean");
        assertTrue(getter instanceof BooleanGetter);
        assertSame(getter, ObjectGetterFactory.toBooleanGetter(getter));
        assertEquals(dbFinalPrimitiveObject.ispBoolean(), ((BooleanGetter)getter).getBoolean(dbFinalPrimitiveObject));

    }
    @Test
    public void testBooleanPrimitiveNoAsmIsBooleanGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, ?> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pBoolean");
        assertFalse(getter instanceof BooleanGetter);
        final BooleanGetter<DbFinalPrimitiveObject> longGetter = ObjectGetterFactory.toBooleanGetter(getter);
        assertTrue(longGetter instanceof BooleanGetter);
        assertEquals(dbFinalPrimitiveObject.ispBoolean(), longGetter.getBoolean(dbFinalPrimitiveObject));

    }


    @Test
    public void testObjectFieldGetterAsm() throws Exception {

        Getter<FooField, Object> getter = asm.getGetter(FooField.class, "foo");
        assertFalse(getter instanceof FieldGetter);
        FooField ff = new FooField();
        ff.foo = "foo1";
        assertEquals("foo1", getter.get(ff));

    }

    @Test
    public void testExtension() throws Exception {
        Foo foo = new Foo();
        new ObjectSetterFactory(null).getSetter(Foo.class, "bar").set(foo, "bar");
        assertEquals("bar", noAsm.getGetter(Foo.class, "bar").get(foo));
    }


}
