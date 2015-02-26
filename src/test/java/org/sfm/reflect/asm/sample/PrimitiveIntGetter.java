package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

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
