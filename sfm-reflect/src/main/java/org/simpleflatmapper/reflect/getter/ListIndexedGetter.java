package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.IndexedGetter;

import java.util.List;

public class ListIndexedGetter<P> implements IndexedGetter<List<P>,P> {
    @Override
    public P get(List<P> target, int index) {
        return target.get(index);
    }
}
