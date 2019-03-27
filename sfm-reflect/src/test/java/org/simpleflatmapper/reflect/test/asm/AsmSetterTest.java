package org.simpleflatmapper.reflect.test.asm;

import org.junit.Test;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.test.SetterHelperTest;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;
import org.simpleflatmapper.test.beans.BarField;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectFields;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.test.beans.FooField;


import static org.junit.Assert.assertEquals;

@SuppressWarnings("SpellCheckingInspection")
public class AsmSetterTest {

	AsmFactory factory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	DbPrimitiveObjectWithSetter object = new DbPrimitiveObjectWithSetter();
	DbPrimitiveObjectFields objectField = new DbPrimitiveObjectFields();
	
	
	public static class Foo2 {
	    private int i;
	    private Object o;

        public int getI() {
            return i;
        }

        public Foo2 setI(int i) {
            this.i = i;
            return this;
        }

        public Object getO() {
            return o;
        }

        public Foo2 setO(Object o) {
            this.o = o;
            return this;
        }
    }
    @Test
    public void testFluentAPI() throws Exception {
        Setter<Foo2, Object> setterO = factory.createSetter(Foo2.class.getDeclaredMethod("setO", Object.class));
        Setter<Foo2, Integer> setterI = factory.createSetter(Foo2.class.getDeclaredMethod("setI", int.class));
        
        Foo2 f = new Foo2();
        Object o = new Object();
        setterO.set(f, o);
        
        assertEquals(o, f.o);
        
        ((IntSetter<Foo2>)setterI).setInt(f, 1);
        assertEquals(1, f.i);
        setterI.set(f, 2);
        assertEquals(2, f.i);
	    
    }
	@Test
	public void testSet() throws Exception {
		Setter<Foo, String> setter = factory.createSetter(Foo.class.getDeclaredMethod("setFoo", String.class));
		SetterHelperTest.validateFooSetter(setter);
	}

	@Test
	public void testSetBoolean() throws Exception {
		@SuppressWarnings("unchecked")
        BooleanSetter<DbPrimitiveObjectWithSetter> setter =
				(BooleanSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpBoolean", boolean.class));
		setter.setBoolean(object, true);
		assertEquals(true, object.ispBoolean());
	}
	
	@Test
	public void testSetByte() throws Exception {
		@SuppressWarnings("unchecked")
        ByteSetter<DbPrimitiveObjectWithSetter> setter =
				(ByteSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpByte", byte.class));
		setter.setByte(object, (byte)0xc3);
		assertEquals((byte)0xc3, object.getpByte());
	}
	
	@Test
	public void testSetCharacter() throws Exception {
		@SuppressWarnings("unchecked")
        CharacterSetter<DbPrimitiveObjectWithSetter> setter =
				(CharacterSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpCharacter", char.class));
		setter.setCharacter(object, 'g');
		assertEquals('g', object.getpCharacter());
	}
	
	@Test
	public void testSetShort() throws Exception {
		@SuppressWarnings("unchecked")
		ShortSetter<DbPrimitiveObjectWithSetter> setter = 
				(ShortSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpShort", short.class));
		setter.setShort(object, (short)33);
		assertEquals((short)33, object.getpShort());
	}
	
	@Test
	public void testSetInt() throws Exception {
		@SuppressWarnings("unchecked")
        IntSetter<DbPrimitiveObjectWithSetter> setter =
				(IntSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpInt", int.class));
		setter.setInt(object, 35);
		assertEquals(35, object.getpInt());
	}
	
	@Test
	public void testSetLong() throws Exception {
		@SuppressWarnings("unchecked")
		LongSetter<DbPrimitiveObjectWithSetter> setter = 
				(LongSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpLong", long.class));
		setter.setLong(object, 35l);
		assertEquals(35l, object.getpLong());
	}
	
	@Test
	public void testSetFloat() throws Exception {
		@SuppressWarnings("unchecked")
        FloatSetter<DbPrimitiveObjectWithSetter> setter =
				(FloatSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpFloat", float.class));
		setter.setFloat(object, 3.14f);
		assertEquals(3.14f, object.getpFloat(), 0);
	}

	@Test
	public void testSetDouble() throws Exception {
		@SuppressWarnings("unchecked")
        DoubleSetter<DbPrimitiveObjectWithSetter> setter =
				(DoubleSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpDouble", double.class));
		setter.setDouble(object, 3.144);
		assertEquals(3.144, object.getpDouble(), 0);
	}



    @Test
    public void testField() throws Exception {
        Setter<FooField, String> setter = factory.createSetter(FooField.class.getDeclaredField("foo"));
        SetterHelperTest.validateFooField(setter);
        Setter<BarField, String> bar = factory.createSetter(FooField.class.getField("bar"));
        SetterHelperTest.validateBarField(bar);
    }

    @Test
    public void testFieldBoolean() throws Exception {
        @SuppressWarnings("unchecked")
        BooleanSetter<DbPrimitiveObjectFields> setter =
                (BooleanSetter<DbPrimitiveObjectFields>) factory.createSetter(DbPrimitiveObjectFields.class.getDeclaredField("pBoolean"));
        setter.setBoolean(objectField, true);
        assertEquals(true, objectField.ispBoolean());
    }

    @Test
    public void testFieldByte() throws Exception {
        @SuppressWarnings("unchecked")
        ByteSetter<DbPrimitiveObjectFields> setter =
                (ByteSetter<DbPrimitiveObjectFields>) factory.createSetter(DbPrimitiveObjectFields.class.getDeclaredField("pByte"));
        setter.setByte(objectField, (byte)0xc3);
        assertEquals((byte)0xc3, objectField.getpByte());
    }

    @Test
    public void testFieldCharacter() throws Exception {
        @SuppressWarnings("unchecked")
        CharacterSetter<DbPrimitiveObjectFields> setter =
                (CharacterSetter<DbPrimitiveObjectFields>) factory.createSetter(DbPrimitiveObjectFields.class.getDeclaredField("pCharacter"));
        setter.setCharacter(objectField, 'g');
        assertEquals('g', objectField.getpCharacter());
    }

    @Test
    public void testFieldShort() throws Exception {
        @SuppressWarnings("unchecked")
        ShortSetter<DbPrimitiveObjectFields> setter =
                (ShortSetter<DbPrimitiveObjectFields>) factory.createSetter(DbPrimitiveObjectFields.class.getDeclaredField("pShort"));
        setter.setShort(objectField, (short)33);
        assertEquals((short)33, objectField.getpShort());
    }

    @Test
    public void testFieldInt() throws Exception {
        @SuppressWarnings("unchecked")
        IntSetter<DbPrimitiveObjectFields> setter =
                (IntSetter<DbPrimitiveObjectFields>) factory.createSetter(DbPrimitiveObjectFields.class.getDeclaredField("pInt"));
        setter.setInt(objectField, 35);
        assertEquals(35, objectField.getpInt());
    }

    @Test
    public void testFieldLong() throws Exception {
        @SuppressWarnings("unchecked")
        LongSetter<DbPrimitiveObjectFields> setter =
                (LongSetter<DbPrimitiveObjectFields>) factory.createSetter(DbPrimitiveObjectFields.class.getDeclaredField("pLong"));
        setter.setLong(objectField, 35l);
        assertEquals(35l, objectField.getpLong());
    }

    @Test
    public void testFieldFloat() throws Exception {
        @SuppressWarnings("unchecked")
        FloatSetter<DbPrimitiveObjectFields> setter =
                (FloatSetter<DbPrimitiveObjectFields>) factory.createSetter(DbPrimitiveObjectFields.class.getDeclaredField("pFloat"));
        setter.setFloat(objectField, 3.14f);
        assertEquals(3.14f, objectField.getpFloat(), 0);
    }

    @Test
    public void testFieldDouble() throws Exception {
        @SuppressWarnings("unchecked")
        DoubleSetter<DbPrimitiveObjectFields> setter =
                (DoubleSetter<DbPrimitiveObjectFields>) factory.createSetter(DbPrimitiveObjectFields.class.getDeclaredField("pDouble"));
        setter.setDouble(objectField, 3.144);
        assertEquals(3.144, objectField.getpDouble(), 0);
    }
}
