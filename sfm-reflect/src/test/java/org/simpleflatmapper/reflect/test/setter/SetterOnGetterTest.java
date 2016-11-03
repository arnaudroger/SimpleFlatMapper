package org.simpleflatmapper.reflect.test.setter;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.setter.SetterOnGetter;

import static org.junit.Assert.*;

public class SetterOnGetterTest {


    @Test
    public void testSetterOnGetter() throws Exception {
        Getter<P, I> subGetter = ReflectionService.newInstance().getObjectGetterFactory().getGetter(P.class, "i");
        Setter<T, I> subSetter = ReflectionService.newInstance().getObjectSetterFactory().getSetter(T.class, "i");
        SetterOnGetter<T, I, P> setter = new SetterOnGetter<T, I, P>(subSetter, subGetter);

        I i = new I();
        T t = new T();
        P p = new P();
        p.i = i;

        setter.set(t, p);

        assertEquals(p.i, t.i);
    }


    public static class T {
        public I i;
    }

    public static class I {

    }
    public static class P {
        public I i;

    }
}