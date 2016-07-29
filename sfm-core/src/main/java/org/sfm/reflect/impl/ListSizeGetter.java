package org.sfm.reflect.impl;

import org.sfm.reflect.primitive.IntGetter;

import java.util.List;

public class ListSizeGetter implements IntGetter<List<?>> {
    @Override
    public int getInt(List<?> target) throws Exception {
        return target.size();
    }
}
