package org.simpleflatmapper.tuple;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class Tuples {

    private Tuples() {
    }

    public static ParameterizedType typeDef(final Type... types) {
        return tupleImplementationTypeDef(getTupleClass(types.length), types);
    }

    private static final Class<?>[] tupleClasses = new Class[] {
            Tuple2.class,
            Tuple3.class,
            Tuple4.class,
            Tuple5.class,
            Tuple6.class,
            Tuple7.class,
            Tuple8.class,
            Tuple9.class,
            Tuple10.class,
            Tuple11.class,
            Tuple12.class,
            Tuple13.class,
            Tuple14.class,
            Tuple15.class,
            Tuple16.class,
            Tuple17.class,
            Tuple18.class,
            Tuple19.class,
            Tuple20.class,
            Tuple21.class,
            Tuple22.class,
            Tuple23.class,
            Tuple24.class,
            Tuple25.class,
            Tuple26.class,
            Tuple27.class,
            Tuple28.class,
            Tuple29.class,
            Tuple30.class,
            Tuple31.class,
            Tuple32.class,
    };

    private static Class<?> getTupleClass(int length) {
        if (length > tupleClasses.length) throw new IllegalArgumentException("No tuple definition for size " + length);
        return tupleClasses[length - 2];
    }

    public static ParameterizedType tupleImplementationTypeDef(final Class<?> tupleImplementation, final Type... tupleTypes) {
        if (tupleImplementation.getTypeParameters().length  != tupleTypes.length) {
            throw new IllegalArgumentException("Incompatible tupleImplementation and type definition expected " + tupleImplementation.getTypeParameters().length + " type definition for " + tupleImplementation);
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
