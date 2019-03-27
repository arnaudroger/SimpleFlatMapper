package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ObjectGetterFactory;
import org.simpleflatmapper.reflect.ObjectSetterFactory;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;
import org.simpleflatmapper.reflect.getter.FieldGetter;
import org.simpleflatmapper.reflect.getter.MethodGetter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbFinalPrimitiveObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbPublicObject;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.test.beans.FooField;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class ObjectGetterFactoryTest {

    private ObjectGetterFactory asm = new ObjectGetterFactory(new AsmFactoryProvider() {
        AsmFactory asmFactory = new AsmFactory(ObjectGetterFactoryTest.class.getClassLoader());
        @Override
        public AsmFactory getAsmFactory(ClassLoader classLoader) {
            return asmFactory;
        }
    });
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

        assertEquals("MethodGetter{method=public java.lang.String org.simpleflatmapper.test.beans.DbObject.getName()}", noAsm.getGetter(DbObject.class, "name").toString());
        assertEquals("MethodGetter{method=public java.lang.String org.simpleflatmapper.test.beans.DbFinalObject.getName()}", noAsm.getGetter(DbFinalObject.class, "name").toString());
        assertEquals("FieldGetter{field=public java.lang.String org.simpleflatmapper.test.beans.DbPublicObject.name}", noAsm.getGetter(DbPublicObject.class, "name").toString());
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
        final Getter<DbFinalPrimitiveObject, Byte> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pByte");
        assertTrue(getter instanceof ByteGetter);
        assertSame(getter, ObjectGetterFactory.toByteGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpByte(), ((ByteGetter)getter).getByte(dbFinalPrimitiveObject));

    }
    @Test
    public void testBytePrimitiveNoAsmIsByteGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Byte> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pByte");
        assertFalse(getter instanceof ByteGetter);
        final ByteGetter<? super DbFinalPrimitiveObject> byteGetter = ObjectGetterFactory.toByteGetter(getter);
        assertTrue(byteGetter instanceof ByteGetter);
        assertEquals(dbFinalPrimitiveObject.getpByte(), byteGetter.getByte(dbFinalPrimitiveObject));
    }

    @Test
    public void testCharPrimitiveAsmIsCharGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Character> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pChar");
        assertTrue(getter instanceof CharacterGetter);
        assertSame(getter, ObjectGetterFactory.toCharGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpCharacter(), ((CharacterGetter)getter).getCharacter(dbFinalPrimitiveObject));

    }
    @Test
    public void testCharPrimitiveNoAsmIsCharGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Character> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pChar");
        assertFalse(getter instanceof CharacterGetter);
        final CharacterGetter<? super DbFinalPrimitiveObject> charGetter = ObjectGetterFactory.toCharGetter(getter);
        assertTrue(charGetter instanceof CharacterGetter);
        assertEquals(dbFinalPrimitiveObject.getpCharacter(), charGetter.getCharacter(dbFinalPrimitiveObject));
    }

    @Test
    public void testShortPrimitiveAsmIsShortGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Short> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pShort");
        assertTrue(getter instanceof ShortGetter);
        assertSame(getter, ObjectGetterFactory.toShortGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpShort(), ((ShortGetter)getter).getShort(dbFinalPrimitiveObject));
    }
    @Test
    public void testShortPrimitiveNoAsmIsShortGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Short> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pShort");
        assertFalse(getter instanceof ShortGetter);
        final ShortGetter<? super DbFinalPrimitiveObject> shortGetter = ObjectGetterFactory.toShortGetter(getter);
        assertTrue(shortGetter instanceof ShortGetter);
        assertEquals(3, shortGetter.getShort(dbFinalPrimitiveObject));
        assertEquals(dbFinalPrimitiveObject.getpShort(), shortGetter.getShort(dbFinalPrimitiveObject));
    }


    @Test
    public void testIntPrimitiveAsmIsIntGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Integer> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pInt");
        assertTrue(getter instanceof IntGetter);
        assertSame(getter, ObjectGetterFactory.toIntGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpInt(), ((IntGetter)getter).getInt(dbFinalPrimitiveObject));

    }
    @Test
    public void testIntPrimitiveNoAsmIsIntGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Integer> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pInt");
        assertFalse(getter instanceof IntGetter);
        final IntGetter<? super DbFinalPrimitiveObject> intGetter = ObjectGetterFactory.toIntGetter(getter);
        assertTrue(intGetter instanceof IntGetter);
        assertEquals(dbFinalPrimitiveObject.getpInt(), intGetter.getInt(dbFinalPrimitiveObject));

    }


    @Test
    public void testLongPrimitiveAsmIsLongGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Long> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pLong");
        assertTrue(getter instanceof LongGetter);
        assertSame(getter, ObjectGetterFactory.toLongGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpLong(), ((LongGetter)getter).getLong(dbFinalPrimitiveObject));

    }
    @Test
    public void testLongPrimitiveNoAsmIsLongGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Long> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pLong");
        assertFalse(getter instanceof LongGetter);
        final LongGetter<? super DbFinalPrimitiveObject> longGetter = ObjectGetterFactory.toLongGetter(getter);
        assertTrue(longGetter instanceof LongGetter);
        assertEquals(dbFinalPrimitiveObject.getpLong(), longGetter.getLong(dbFinalPrimitiveObject));

    }

    @Test
    public void testFloatPrimitiveAsmIsFloatGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Float> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pFloat");
        assertTrue(getter instanceof FloatGetter);
        assertSame(getter, ObjectGetterFactory.toFloatGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpFloat(), ((FloatGetter)getter).getFloat(dbFinalPrimitiveObject), 0.0001);

    }
    @Test
    public void testFloatPrimitiveNoAsmIsFloatGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Float> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pFloat");
        assertFalse(getter instanceof FloatGetter);
        final FloatGetter<? super DbFinalPrimitiveObject> longGetter = ObjectGetterFactory.toFloatGetter(getter);
        assertTrue(longGetter instanceof FloatGetter);
        assertEquals(dbFinalPrimitiveObject.getpFloat(), longGetter.getFloat(dbFinalPrimitiveObject), 0.0001);

    }


    @Test
    public void testDoublePrimitiveAsmIsDoubleGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Double> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pDouble");
        assertTrue(getter instanceof DoubleGetter);
        assertSame(getter, ObjectGetterFactory.toDoubleGetter(getter));
        assertEquals(dbFinalPrimitiveObject.getpDouble(), ((DoubleGetter)getter).getDouble(dbFinalPrimitiveObject), 0.0001);

    }
    @Test
    public void testDoublePrimitiveNoAsmIsDoubleGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Double> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pDouble");
        assertFalse(getter instanceof DoubleGetter);
        final DoubleGetter<? super DbFinalPrimitiveObject> longGetter = ObjectGetterFactory.toDoubleGetter(getter);
        assertTrue(longGetter instanceof DoubleGetter);
        assertEquals(dbFinalPrimitiveObject.getpDouble(), longGetter.getDouble(dbFinalPrimitiveObject), 0.0001);

    }

    @Test
    public void testBooleanPrimitiveAsmIsBooleanGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Boolean> getter = asm.getGetter(DbFinalPrimitiveObject.class, "pBoolean");
        assertTrue(getter instanceof BooleanGetter);
        assertSame(getter, ObjectGetterFactory.toBooleanGetter(getter));
        assertEquals(dbFinalPrimitiveObject.ispBoolean(), ((BooleanGetter)getter).getBoolean(dbFinalPrimitiveObject));

    }
    @Test
    public void testBooleanPrimitiveNoAsmIsBooleanGetter() throws Exception {
        final Getter<DbFinalPrimitiveObject, Boolean> getter = noAsm.getGetter(DbFinalPrimitiveObject.class, "pBoolean");
        assertFalse(getter instanceof BooleanGetter);
        final BooleanGetter<? super DbFinalPrimitiveObject> longGetter = ObjectGetterFactory.toBooleanGetter(getter);
        assertTrue(longGetter instanceof BooleanGetter);
        assertEquals(dbFinalPrimitiveObject.ispBoolean(), longGetter.getBoolean(dbFinalPrimitiveObject));

    }

    @Test
    public void testBoxedBooleanAsm() throws Exception {
        final Getter<DbBoxed, Boolean> getter = asm.getGetter(DbBoxed.class, "propBoolean");
        assertFalse(getter instanceof BooleanGetter);
        final BooleanGetter<DbBoxed> pGetter = ObjectGetterFactory.toBooleanGetter(getter);
        assertTrue(pGetter instanceof BooleanGetter);
        assertEquals(false, pGetter.getBoolean(new DbBoxed()));
    }

    @Test
    public void testBoxedBooleanNoAsm() throws Exception {
        final Getter<DbBoxed, Boolean> getter = asm.getGetter(DbBoxed.class, "propBoolean");
        assertFalse(getter instanceof BooleanGetter);
        final BooleanGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toBooleanGetter(getter);
        assertTrue(pGetter instanceof BooleanGetter);
        assertEquals(false, pGetter.getBoolean(new DbBoxed()));
    }

    @Test
    public void testBoxedByteAsm() throws Exception {
        final Getter<DbBoxed, Byte> getter = asm.getGetter(DbBoxed.class, "propByte");
        assertFalse(getter instanceof ByteGetter);
        final ByteGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toByteGetter(getter);
        assertTrue(pGetter instanceof ByteGetter);
        assertEquals(0, pGetter.getByte(new DbBoxed()));
    }

    @Test
    public void testBoxedByteNoAsm() throws Exception {
        final Getter<DbBoxed, Byte> getter = asm.getGetter(DbBoxed.class, "propByte");
        assertFalse(getter instanceof ByteGetter);
        final ByteGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toByteGetter(getter);
        assertTrue(pGetter instanceof ByteGetter);
        assertEquals(0, pGetter.getByte(new DbBoxed()));
    }

    @Test
    public void testBoxedCharacterAsm() throws Exception {
        final Getter<DbBoxed, Character> getter = asm.getGetter(DbBoxed.class, "propCharacter");
        assertFalse(getter instanceof CharacterGetter);
        final CharacterGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toCharGetter(getter);
        assertTrue(pGetter instanceof CharacterGetter);
        assertEquals(0, pGetter.getCharacter(new DbBoxed()));
    }

    @Test
    public void testBoxedCharacterNoAsm() throws Exception {
        final Getter<DbBoxed, Character> getter = asm.getGetter(DbBoxed.class, "propCharacter");
        assertFalse(getter instanceof CharacterGetter);
        final CharacterGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toCharGetter(getter);
        assertTrue(pGetter instanceof CharacterGetter);
        assertEquals(0, pGetter.getCharacter(new DbBoxed()));
    }

    @Test
    public void testBoxedShortAsm() throws Exception {
        final Getter<DbBoxed, Short> getter = asm.getGetter(DbBoxed.class, "propShort");
        assertFalse(getter instanceof ShortGetter);
        final ShortGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toShortGetter(getter);
        assertTrue(pGetter instanceof ShortGetter);
        assertEquals(0, pGetter.getShort(new DbBoxed()));
    }

    @Test
    public void testBoxedShortNoAsm() throws Exception {
        final Getter<DbBoxed, Short> getter = asm.getGetter(DbBoxed.class, "propShort");
        assertFalse(getter instanceof ShortGetter);
        final ShortGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toShortGetter(getter);
        assertTrue(pGetter instanceof ShortGetter);
        assertEquals(0, pGetter.getShort(new DbBoxed()));
    }


    @Test
    public void testBoxedIntegerAsm() throws Exception {
        final Getter<DbBoxed, Integer> getter = asm.getGetter(DbBoxed.class, "propInt");
        assertFalse(getter instanceof IntGetter);
        final IntGetter<? super DbBoxed> intGetter = ObjectGetterFactory.toIntGetter(getter);
        assertTrue(intGetter instanceof IntGetter);
        assertEquals(0, intGetter.getInt(new DbBoxed()));
    }

    @Test
    public void testBoxedIntegerNoAsm() throws Exception {
        final Getter<DbBoxed, Integer> getter = asm.getGetter(DbBoxed.class, "propInt");
        assertFalse(getter instanceof IntGetter);
        final IntGetter<? super DbBoxed> intGetter = ObjectGetterFactory.toIntGetter(getter);
        assertTrue(intGetter instanceof IntGetter);
        assertEquals(0, intGetter.getInt(new DbBoxed()));
    }

    @Test
    public void testBoxedLongAsm() throws Exception {
        final Getter<DbBoxed, Long> getter = asm.getGetter(DbBoxed.class, "propLong");
        assertFalse(getter instanceof LongGetter);
        final LongGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toLongGetter(getter);
        assertTrue(pGetter instanceof LongGetter);
        assertEquals(0, pGetter.getLong(new DbBoxed()));
    }

    @Test
    public void testBoxedLongNoAsm() throws Exception {
        final Getter<DbBoxed, Long> getter = asm.getGetter(DbBoxed.class, "propLong");
        assertFalse(getter instanceof LongGetter);
        final LongGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toLongGetter(getter);
        assertTrue(pGetter instanceof LongGetter);
        assertEquals(0, pGetter.getLong(new DbBoxed()));
    }

    @Test
    public void testBoxedFloatAsm() throws Exception {
        final Getter<DbBoxed, Float> getter = asm.getGetter(DbBoxed.class, "propFloat");
        assertFalse(getter instanceof FloatGetter);
        final FloatGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toFloatGetter(getter);
        assertTrue(pGetter instanceof FloatGetter);
        assertEquals(0.00, pGetter.getFloat(new DbBoxed()), 0.00001);
    }

    @Test
    public void testBoxedFloatNoAsm() throws Exception {
        final Getter<DbBoxed, Float> getter = asm.getGetter(DbBoxed.class, "propFloat");
        assertFalse(getter instanceof FloatGetter);
        final FloatGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toFloatGetter(getter);
        assertTrue(pGetter instanceof FloatGetter);
        assertEquals(0.0, pGetter.getFloat(new DbBoxed()), 0.00001);
    }

    @Test
    public void testBoxedDoubleAsm() throws Exception {
        final Getter<DbBoxed, Double> getter = asm.getGetter(DbBoxed.class, "propDouble");
        assertFalse(getter instanceof DoubleGetter);
        final DoubleGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toDoubleGetter(getter);
        assertTrue(pGetter instanceof DoubleGetter);
        assertEquals(0.0, pGetter.getDouble(new DbBoxed()), 0.0001);
    }

    @Test
    public void testBoxedDoubleNoAsm() throws Exception {
        final Getter<DbBoxed, Double> getter = asm.getGetter(DbBoxed.class, "propDouble");
        assertFalse(getter instanceof DoubleGetter);
        final DoubleGetter<? super DbBoxed> pGetter = ObjectGetterFactory.toDoubleGetter(getter);
        assertTrue(pGetter instanceof DoubleGetter);
        assertEquals(0.0, pGetter.getDouble(new DbBoxed()), 0.00001);
    }


    public static class DbBoxed {
        Boolean propBoolean;
        Byte propByte;
        Character propCharacter;
        Short propShort;
        Integer propInt;
        Long propLong;
        Float propFloat;
        Double propDouble;

        public Integer getPropInt() {
            return propInt;
        }

        public void setPropInt(Integer propInt) {
            this.propInt = propInt;
        }

        public Long getPropLong() {
            return propLong;
        }

        public void setPropLong(Long propLong) {
            this.propLong = propLong;
        }

        public Boolean getPropBoolean() {
            return propBoolean;
        }

        public void setPropBoolean(Boolean propBoolean) {
            this.propBoolean = propBoolean;
        }

        public Byte getPropByte() {
            return propByte;
        }

        public void setPropByte(Byte propByte) {
            this.propByte = propByte;
        }

        public Character getPropCharacter() {
            return propCharacter;
        }

        public void setPropCharacter(Character propCharacter) {
            this.propCharacter = propCharacter;
        }

        public Short getPropShort() {
            return propShort;
        }

        public void setPropShort(Short propShort) {
            this.propShort = propShort;
        }

        public Float getPropFloat() {
            return propFloat;
        }

        public void setPropFloat(Float propFloat) {
            this.propFloat = propFloat;
        }

        public Double getPropDouble() {
            return propDouble;
        }

        public void setPropDouble(Double propDouble) {
            this.propDouble = propDouble;
        }
    }

    @Test
    public void testObjectFieldGetter() throws Exception {

        FooField ff = new FooField();
        ff.foo = "foo1";

        Getter<FooField, Object> getter = asm.getGetter(FooField.class, "foo");
        assertFalse(getter instanceof FieldGetter);
        assertEquals("foo1", getter.get(ff));

        getter = noAsm.getGetter(FooField.class, "foo");
        assertEquals("foo1", getter.get(ff));
        assertEquals("FieldGetter{field=public java.lang.String org.simpleflatmapper.test.beans.FooField.foo}", getter.toString());
    }

    @Test
    public void testExtension() throws Exception {
        Foo foo = new Foo();
        new ObjectSetterFactory(null).getSetter(Foo.class, "bar").set(foo, "bar");
        new ObjectSetterFactory(null).getFieldSetter(Foo.class, "bar").set(foo, "bar");

        assertEquals("bar", noAsm.getGetter(Foo.class, "bar").get(foo));
        assertEquals("bar", noAsm.getFieldGetter(Foo.class, "bar").get(foo));
    }



}
