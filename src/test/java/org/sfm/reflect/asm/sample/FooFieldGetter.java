package org.sfm.reflect.asm.sample;

import org.sfm.beans.FooField;
import org.sfm.reflect.Getter;

/**
 * Created by aroger on 13/03/15.
 */
public class FooFieldGetter implements Getter<FooField, String> {
    @Override
    public String get(FooField target) throws Exception {
        return target.foo;
    }
}
