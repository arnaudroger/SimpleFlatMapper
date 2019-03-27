package org.simpleflatmapper.reflect.test.asm;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectFields;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.test.beans.FooField;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("SpellCheckingInspection")
public class AsmGetterTest {

	AsmFactory factory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	DbPrimitiveObjectWithSetter object = new DbPrimitiveObjectWithSetter();
	DbPrimitiveObjectFields objectField = new DbPrimitiveObjectFields();
	@Test
	public void testGet() throws Exception {
		Getter<Foo, String> getter = factory.createGetter(Foo.class.getMethod("getFoo"));

        Foo foo = new Foo();
        foo.setFoo("foo!");

        assertEquals("foo!", getter.get(foo));
	}

	@Test
	public void testGetBoolean() throws Exception {
		@SuppressWarnings("unchecked")
        BooleanGetter<DbPrimitiveObjectWithSetter> getter =
				(BooleanGetter<DbPrimitiveObjectWithSetter>) factory.createGetter(DbPrimitiveObjectWithSetter.class.getMethod("ispBoolean"));

        object.setpBoolean(true);
		assertEquals(true, getter.getBoolean(object));
	}
	
	@Test
	public void testGetByte() throws Exception {
		@SuppressWarnings("unchecked")
		ByteGetter<DbPrimitiveObjectWithSetter> getter =
				(ByteGetter<DbPrimitiveObjectWithSetter>) factory.createGetter(DbPrimitiveObjectWithSetter.class.getMethod("getpByte"));
		object.setpByte((byte) 0xc3);
		assertEquals((byte)0xc3, getter.getByte(object));
	}
	
	@Test
	public void testGetCharacter() throws Exception {
		@SuppressWarnings("unchecked")
        CharacterGetter<DbPrimitiveObjectWithSetter> getter =
				(CharacterGetter<DbPrimitiveObjectWithSetter>) factory.createGetter(DbPrimitiveObjectWithSetter.class.getMethod("getpCharacter"));
        object.setpCharacter('g');
		assertEquals('g', getter.getCharacter(object));
	}
	
	@Test
	public void testGetShort() throws Exception {
		@SuppressWarnings("unchecked")
        ShortGetter<DbPrimitiveObjectWithSetter> getter =
				(ShortGetter<DbPrimitiveObjectWithSetter>) factory.createGetter(DbPrimitiveObjectWithSetter.class.getMethod("getpShort"));
		object.setpShort((short) 33);
		assertEquals((short)33, getter.getShort(object));
	}
	
	@Test
	public void testGetInt() throws Exception {
		@SuppressWarnings("unchecked")
        IntGetter<DbPrimitiveObjectWithSetter> getter =
				(IntGetter<DbPrimitiveObjectWithSetter>) factory.createGetter(DbPrimitiveObjectWithSetter.class.getMethod("getpInt"));
		object.setpInt(35);
		assertEquals(35, getter.getInt(object));
	}
	
	@Test
	public void testGetLong() throws Exception {
		@SuppressWarnings("unchecked")
        LongGetter<DbPrimitiveObjectWithSetter> getter =
				(LongGetter<DbPrimitiveObjectWithSetter>) factory.createGetter(DbPrimitiveObjectWithSetter.class.getMethod("getpLong"));
		object.setpLong(35l);
		assertEquals(35l, getter.getLong(object));
	}
	
	@Test
	public void testGetFloat() throws Exception {
		@SuppressWarnings("unchecked")
        FloatGetter<DbPrimitiveObjectWithSetter> getter =
				(FloatGetter<DbPrimitiveObjectWithSetter>) factory.createGetter(DbPrimitiveObjectWithSetter.class.getMethod("getpFloat"));
		object.setpFloat(3.14f);
		assertEquals(3.14f, getter.getFloat(object), 0);
	}

	@Test
	public void testGetDouble() throws Exception {
		@SuppressWarnings("unchecked")
        DoubleGetter<DbPrimitiveObjectWithSetter> getter =
				(DoubleGetter<DbPrimitiveObjectWithSetter>) factory.createGetter(DbPrimitiveObjectWithSetter.class.getMethod("getpDouble"));
		object.setpDouble(3.144);
		assertEquals(3.144, getter.getDouble(object), 0);
	}



    @Test
    public void testField() throws Exception {
        Getter<FooField, String> getter = factory.createGetter(FooField.class.getDeclaredField("foo"));

        FooField foo = new FooField();
        foo.foo = ("foo!");

        assertEquals("foo!", getter.get(foo));
    }

    @Test
    public void testFieldBoolean() throws Exception {
        @SuppressWarnings("unchecked")
        BooleanGetter<DbPrimitiveObjectFields> getter =
                (BooleanGetter<DbPrimitiveObjectFields>) factory.createGetter(DbPrimitiveObjectFields.class.getDeclaredField("pBoolean"));
        objectField.pBoolean = (true);
        assertEquals(true, getter.getBoolean(objectField));
    }

    @Test
    public void testFieldByte() throws Exception {
        @SuppressWarnings("unchecked")
        ByteGetter<DbPrimitiveObjectFields> getter =
                (ByteGetter<DbPrimitiveObjectFields>) factory.createGetter(DbPrimitiveObjectFields.class.getDeclaredField("pByte"));
        objectField.pByte =  (byte)0xc3;
        assertEquals((byte)0xc3, getter.getByte(objectField));
    }

    @Test
    public void testFieldCharacter() throws Exception {
        @SuppressWarnings("unchecked")
        CharacterGetter<DbPrimitiveObjectFields> getter =
                (CharacterGetter<DbPrimitiveObjectFields>) factory.createGetter(DbPrimitiveObjectFields.class.getDeclaredField("pCharacter"));
        objectField.pCharacter = 'g';
        assertEquals('g', getter.getCharacter(objectField));
    }

    @Test
    public void testFieldShort() throws Exception {
        @SuppressWarnings("unchecked")
        ShortGetter<DbPrimitiveObjectFields> getter =
                (ShortGetter<DbPrimitiveObjectFields>) factory.createGetter(DbPrimitiveObjectFields.class.getDeclaredField("pShort"));
        objectField.pShort = (short)33;
        assertEquals((short)33, getter.getShort(objectField));
    }

    @Test
    public void testFieldInt() throws Exception {
        @SuppressWarnings("unchecked")
        IntGetter<DbPrimitiveObjectFields> getter =
                (IntGetter<DbPrimitiveObjectFields>) factory.createGetter(DbPrimitiveObjectFields.class.getDeclaredField("pInt"));
        objectField.pInt = 35;
        assertEquals(35, getter.getInt(objectField));
    }

    @Test
    public void testFieldLong() throws Exception {
        @SuppressWarnings("unchecked")
        LongGetter<DbPrimitiveObjectFields> getter =
                (LongGetter<DbPrimitiveObjectFields>) factory.createGetter(DbPrimitiveObjectFields.class.getDeclaredField("pLong"));
        objectField.pLong = 35l;
        assertEquals(35l, getter.getLong(objectField));
    }

    @Test
    public void testFieldFloat() throws Exception {
        @SuppressWarnings("unchecked")
        FloatGetter<DbPrimitiveObjectFields> getter =
                (FloatGetter<DbPrimitiveObjectFields>) factory.createGetter(DbPrimitiveObjectFields.class.getDeclaredField("pFloat"));
        objectField.pFloat = 3.14f;
        assertEquals(3.14f, getter.getFloat(objectField), 0);
    }

    @Test
    public void testFieldDouble() throws Exception {
        @SuppressWarnings("unchecked")
        DoubleGetter<DbPrimitiveObjectFields> getter =
                (DoubleGetter<DbPrimitiveObjectFields>) factory.createGetter(DbPrimitiveObjectFields.class.getDeclaredField("pDouble"));
        objectField.pDouble = 3.144;
        assertEquals(3.144, getter.getDouble(objectField), 0);
    }
}
