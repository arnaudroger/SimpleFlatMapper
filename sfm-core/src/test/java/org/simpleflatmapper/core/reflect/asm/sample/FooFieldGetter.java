package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.FooField;
import org.simpleflatmapper.core.reflect.Getter;

public class FooFieldGetter implements Getter<FooField, String> {
    @Override
    public String get(FooField target) throws Exception {
        return target.foo;
    }
}
