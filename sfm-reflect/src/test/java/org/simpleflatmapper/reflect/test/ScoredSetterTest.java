package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.ScoredSetter;
import org.simpleflatmapper.reflect.Setter;

import static org.junit.Assert.*;


public class ScoredSetterTest {

    @Test
    public void setMethodBetterThanNameMatch() throws Exception {
        ScoredSetter<Object, Object> setMethod = ScoredSetter.ofMethod(Setters.class.getDeclaredMethod("setValue", String.class), new Setter<Object, Object>() {
            @Override
            public void set(Object target, Object value) throws Exception {
                ((Setters)target).value = "setValue";
            }
        });
        ScoredSetter<Object, Object> method = ScoredSetter.ofMethod(Setters.class.getMethod("value", String.class), new Setter<Object, Object>() {
            @Override
            public void set(Object target, Object value) throws Exception {
                ((Setters)target).value = "value";
            }
        });
        Setters setters = new Setters();
        assertTrue(setMethod.isBetterThan(method));
        setMethod.getSetter().set(setters, "val2");
        assertEquals("setValue", setters.value);
    }

    @Test
    public void methodBetterThanField() throws Exception {
        ScoredSetter<Object, Object> setMethod = ScoredSetter.ofMethod(Setters.class.getDeclaredMethod("setValue", String.class), new Setter<Object, Object>() {
            @Override
            public void set(Object target, Object value) throws Exception {
                ((Setters)target).value = "setValue";
            }
        });
        ScoredSetter<Object, Object> field = ScoredSetter.ofField(Setters.class.getDeclaredField("value"), new Setter<Object, Object>() {
            @Override
            public void set(Object target, Object value) throws Exception {
                ((Setters) target).value = "value";
            }
        });
        Setters setters = new Setters();
        assertTrue(setMethod.isBetterThan(field));
        setMethod.getSetter().set(setters, "val2");
        assertEquals("setValue", setters.value);
    }

    public static class Setters {
        public String value;

        public void setValue(String val) {
        }

        public void value(String val) {
        }
    }

}