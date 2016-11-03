package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.test.beans.FooField;

public class FooFieldGetter implements Getter<FooField, String> {
    @Override
    public String get(FooField target) throws Exception {
        return target.foo;
    }
}
