package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.FooField;
import org.simpleflatmapper.core.reflect.Setter;

public class FooFieldSetter implements Setter<FooField, String> {

    @Override
    public void set(FooField target, String value) throws Exception {
        target.foo = value;
    }
}
