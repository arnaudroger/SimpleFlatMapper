package org.sfm.tuples;

import org.sfm.reflect.TypeHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Tuples {

    public static boolean isTuple(Type type) {
        Class<?> clazz = TypeHelper.toClass(type);
        return Tuple2.class.isAssignableFrom(clazz);
    }

    public static ParameterizedType typeDef(final Class<?> c1, final Class<?> c2) {
        return tupleImplementationTypeDef(Tuple2.class, c1, c2);
    }

    public static ParameterizedType typeDef(final Class<?> c1, final Class<?> c2, Class<?> c3) {
        return tupleImplementationTypeDef(Tuple3.class, c1, c2, c3);
    }

    public static ParameterizedType typeDef(final Class<?> c1, final Class<?> c2, Class<?> c3, Class<?> c4) {
        return tupleImplementationTypeDef(Tuple4.class, c1, c2, c3, c4);
    }

    public static ParameterizedType typeDef(final Class<?> c1, final Class<?> c2, Class<?> c3, Class<?> c4, Class<?> c5) {
        return tupleImplementationTypeDef(Tuple5.class, c1, c2, c3, c4, c5);
    }


    public static ParameterizedType tupleImplementationTypeDef(final Class<?> tupleImplementation, final Class<?>... tupleTypes) {
        if (tupleImplementation.getTypeParameters().length  != tupleTypes.length) {
            throw new IllegalArgumentException("Incompatible tupleImplementation and type defintion expected " + tupleImplementation.getTypeParameters().length + " type definition for " + tupleImplementation);
        } else {
            return new TupleParameterizedType(tupleImplementation, tupleTypes);
        }
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
