package org.simpleflatmapper.reflect.test.getter;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.reflect.getter.*;

import static org.junit.Assert.*;

public class ConstantGetterTest {

    @Test
    public void testBoolean() {
        assertTrue(new ConstantBooleanGetter<Object>(true).getBoolean(null));
        assertTrue(new ConstantBooleanGetter<Object>(true).get(null));
        assertFalse(new ConstantBooleanGetter<Object>(false).getBoolean(null));
        assertFalse(new ConstantBooleanGetter<Object>(false).get(null));
    }

    @Test
    public void testByte() {
        byte value = (byte) 13;
        Assert.assertEquals(value, new ConstantByteGetter<Object>(value).getByte(null));
        assertEquals(value, new ConstantByteGetter<Object>(value).get(null).byteValue());
    }
    @Test
    public void testCharacter() {
        char value = (char) 13;
        Assert.assertEquals(value, new ConstantCharacterGetter<Object>(value).getCharacter(null));
        assertEquals(value, new ConstantCharacterGetter<Object>(value).get(null).charValue());
    }
    @Test
    public void testShort() {
        short value = (short) 13;
        Assert.assertEquals(value, new ConstantShortGetter<Object>(value).getShort(null));
        assertEquals(value, new ConstantShortGetter<Object>(value).get(null).shortValue());
    }
    @Test
    public void testInteger() {
        int value = 13;
        Assert.assertEquals(value, new ConstantIntGetter<Object>(value).getInt(null));
        assertEquals(value, new ConstantIntGetter<Object>(value).get(null).intValue());
    }
    @Test
    public void testLong() {
        long value = 13;
        assertEquals(value, new ConstantLongGetter<Object>(value).getLong(null));
        assertEquals(value, new ConstantLongGetter<Object>(value).get(null).longValue());
    }

    @Test
    public void testFloat() {
        float value = 13;
        assertEquals(value, new ConstantFloatGetter<Object>(value).getFloat(null), 0.00000001);
        assertEquals(value, new ConstantFloatGetter<Object>(value).get(null).floatValue(), 0.00000001);
    }
    @Test
    public void testDouble() {
        double value = 13;
        assertEquals(value, new ConstantDoubleGetter<Object>(value).getDouble(null), 0.00000001);
        assertEquals(value, new ConstantDoubleGetter<Object>(value).get(null).doubleValue(), 0.00000001);
    }

    @Test
    public void testObject() {
        Object value = new Object();
        assertEquals(value, new ConstantGetter<Object, Object>(value).get(null));
    }

}