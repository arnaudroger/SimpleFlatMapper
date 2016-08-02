package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.primitive.IntGetter;

public class PrimitiveIntGetter implements Getter<DbPrimitiveObjectWithSetter, Integer>, IntGetter<DbPrimitiveObjectWithSetter> {


    @Override
    public Integer get(DbPrimitiveObjectWithSetter target) throws Exception {
        return Integer.valueOf(target.getpInt());
    }

    @Override
    public int getInt(DbPrimitiveObjectWithSetter target) throws Exception {
        return target.getpInt();
    }
}
