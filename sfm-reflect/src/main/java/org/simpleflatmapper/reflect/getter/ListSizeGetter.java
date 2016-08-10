package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.primitive.IntGetter;

import java.util.List;

public class ListSizeGetter implements IntGetter<List<?>> {
    @Override
    public int getInt(List<?> target) {
        return target.size();
    }
}
