package org.sfm.tuples;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by e19224 on 04/12/2014.
 */
public class Tuples {
    public static <T1, T2> ParameterizedType typeDef(final Class<T1> c1, final Class<T2> c2) {
        return new TupleParameterizedType(Tuple2.class, c1, c2);
    }

    public static <T1, T2, T3> ParameterizedType typeDef(final Class<T1> c1, final Class<T2> c2, Class<T3> c3) {
        return new TupleParameterizedType(Tuple3.class, c1, c2, c3);
    }

    public static <T1, T2, T3, T4> ParameterizedType typeDef(final Class<T1> c1, final Class<T2> c2, Class<T3> c3, Class<T4> c4) {
        return new TupleParameterizedType(Tuple4.class, c1, c2, c3, c4);
    }

    public static <T1, T2, T3, T4, T5> ParameterizedType typeDef(final Class<T1> c1, final Class<T2> c2, Class<T3> c3, Class<T4> c4, Class<T5> c5) {
        return new TupleParameterizedType(Tuple5.class, c1, c2, c3, c4, c5);
    }

    private static class TupleParameterizedType implements ParameterizedType {
        private final Type[] types;
        private final Class<?> rawType;

        public TupleParameterizedType(Class<?> rawType, Type... types) {
            this.types = types;
            this.rawType = rawType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return types;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
