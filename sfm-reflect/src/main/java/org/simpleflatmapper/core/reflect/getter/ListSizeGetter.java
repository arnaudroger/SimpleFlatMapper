package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.primitive.IntGetter;

import java.util.List;

public class ListSizeGetter implements IntGetter<List<?>> {
    @Override
    public int getInt(List<?> target) throws Exception {
        return target.size();
    }
}
