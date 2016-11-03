package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
