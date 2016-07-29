package org.sfm.reflect.asm.sample;

import org.sfm.beans.FooField;
import org.sfm.reflect.Setter;

public class FooFieldSetter implements Setter<FooField, String> {

    @Override
    public void set(FooField target, String value) throws Exception {
        target.foo = value;
    }
}
