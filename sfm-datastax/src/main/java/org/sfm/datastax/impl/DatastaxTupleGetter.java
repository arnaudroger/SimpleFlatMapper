package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.TupleType;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.TypeHelper;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DatastaxTupleGetter<T extends Tuple2<?, ?>> implements Getter<GettableByIndexData, T> {
    private final Instantiator<GettableByIndexData, T> instantiator;
    private final int index;

    public DatastaxTupleGetter(Instantiator<GettableByIndexData, T> instantiator, int index) {
        this.instantiator = instantiator;
        this.index = index;
    }

    @Override
    public T get(GettableByIndexData target) throws Exception {
        return instantiator.newInstance(target.getTupleValue(index));
    }

    @SuppressWarnings("unchecked")
    public static <P extends  Tuple2<?, ?>> Getter<GettableByIndexData, P> newInstance(Type target, TupleType tt, int index, RowGetterFactory rowGetterFactory) {

        final Class<P> tupleClass = TypeHelper.toClass(target);
        final Constructor<P> constructor = (Constructor<P>) tupleClass.getConstructors()[0];
        final Getter<GettableByIndexData, ?>[] getters = new Getter[constructor.getParameterCount()];

        final ParameterizedType pt = (ParameterizedType) target;

        for(int i = 0; i < getters.length; i ++) {
            getters[i] = rowGetterFactory.newGetter(pt.getActualTypeArguments()[i], new DatastaxColumnKey("elt" + i, i, tt.getComponentTypes().get(i)), null);
        }

        Instantiator<GettableByIndexData, P> instantiator = new Instantiator<GettableByIndexData, P>() {
            @Override
            public P newInstance(GettableByIndexData gettableData) throws Exception {

                Object[] args = new Object[getters.length];

                for (int i = 0; i < args.length; i++) {
                    args[i] = getters[i].get(gettableData);
                }

                return constructor.newInstance(args);
            }
        };
        return new DatastaxTupleGetter<P>(instantiator, index);
    }
}
