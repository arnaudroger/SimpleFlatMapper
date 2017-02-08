package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;

import java.util.Collection;

public class AppendCollectionSetter<E> implements Setter<Collection<E>, E> {
    public static final AppendCollectionSetter INSTANCE = new AppendCollectionSetter();

    @SuppressWarnings("unchecked")
    public static <E> AppendCollectionSetter<E> setter() {
        return INSTANCE;
    }

    private AppendCollectionSetter() {
    }

    @Override
    public void set(Collection<E> target, E value) throws Exception {
        target.add(value);
    }

    @Override
    public String toString() {
        return "AppendCollectionSetter{}";
    }
}
