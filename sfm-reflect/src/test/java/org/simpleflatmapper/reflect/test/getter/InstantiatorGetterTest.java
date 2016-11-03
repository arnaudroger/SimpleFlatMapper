package org.simpleflatmapper.reflect.test.getter;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.getter.InstantiatorGetter;

import static org.junit.Assert.assertEquals;

public class InstantiatorGetterTest {


    @Test
    public void test() throws Exception {
        Getter<T, S> subGetter = new Getter<T, S>() {
            @Override
            public S get(T target) throws Exception {
                return target.s;
            }
        };
        Instantiator<S, P> instantiator = new Instantiator<S, P>() {
            @Override
            public P newInstance(S s) throws Exception {
                return new P(s);
            }
        };
        InstantiatorGetter<S, T, P> getter =
                new InstantiatorGetter<S, T, P>(instantiator, subGetter);

        T t = new T(new S());
        P p = getter.get(t);
        assertEquals(t.s, p.s);
    }

    public static class S {
    }

    public static class T {
        public final S s;

        public T(S s) {
            this.s = s;
        }
    }

    public static class P {
        public final S s;

        public P(S s) {
            this.s = s;
        }
    }

}