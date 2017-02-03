package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;

import java.util.Collection;

public class AppendCollectionSetter<E> implements Setter<Collection<E>, E> {

    public AppendCollectionSetter() {
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
