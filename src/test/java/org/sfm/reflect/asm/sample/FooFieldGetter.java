package org.sfm.reflect.asm.sample;

import org.sfm.beans.FooField;
import org.sfm.reflect.Getter;

public class FooFieldGetter implements Getter<FooField, String> {
    @Override
    public String get(FooField target) throws Exception {
        return target.foo;
    }
}
