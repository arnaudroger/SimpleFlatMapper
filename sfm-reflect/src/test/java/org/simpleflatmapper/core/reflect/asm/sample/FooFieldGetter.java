package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.test.beans.FooField;

public class FooFieldGetter implements Getter<FooField, String> {
    @Override
    public String get(FooField target) throws Exception {
        return target.foo;
    }
}
