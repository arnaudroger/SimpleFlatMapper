package org.sfm.reflect.asm.sample;

import org.sfm.beans.FooField;
import org.sfm.reflect.Setter;

/**
 * Created by aroger on 13/03/15.
 */
public class FooFieldSetter implements Setter<FooField, String> {

    @Override
    public void set(FooField target, String value) throws Exception {
        target.foo = value;
    }
}
