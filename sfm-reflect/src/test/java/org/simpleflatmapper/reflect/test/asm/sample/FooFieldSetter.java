package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.test.beans.FooField;

public class FooFieldSetter implements Setter<FooField, String> {

    @Override
    public void set(FooField target, String value) throws Exception {
        target.foo = value;
    }
}
