package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.NullGetter;

import static org.junit.Assert.*;

public class ScoredGetterTest {

    @Test
    public void getMethodBetterThanNameMatch() throws Exception {
        ScoredGetter<Object, Object> getMethod = ScoredGetter.ofMethod(Getters.class.getDeclaredMethod("getValue"), new ConstantGetter<Object, Object>("val"));
        ScoredGetter<Object, Object> method = ScoredGetter.ofMethod(Getters.class.getMethod("value"), NullGetter.getter());

        assertTrue(getMethod.isBetterThan(method));
        assertEquals("val", getMethod.getGetter().get(null));
    }

    @Test
    public void methodBetterThanField() throws Exception {
        ScoredGetter<Object, Object> method = ScoredGetter.ofMethod(Getters.class.getDeclaredMethod("getValue"), new ConstantGetter<Object, Object>("val"));
        ScoredGetter<Object, Object> field = ScoredGetter.ofField(Getters.class.getDeclaredField("value"), NullGetter.getter());

        assertTrue(method.isBetterThan(field));
        assertEquals("val", method.getGetter().get(null));
    }

    public static class Getters {
        public String value;

        public String getValue() {
            return value;
        }

        public String value() {
            return value;
        }
    }
}