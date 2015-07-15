package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.BooleanGetter;

public class PrimitiveBooleanGetter implements Getter<DbPrimitiveObjectWithSetter, Boolean>, BooleanGetter<DbPrimitiveObjectWithSetter> {


    @Override
    public Boolean get(DbPrimitiveObjectWithSetter target) throws Exception {
        return Boolean.valueOf(target.ispBoolean());
    }

    @Override
    public boolean getBoolean(DbPrimitiveObjectWithSetter target) throws Exception {
        return target.ispBoolean();
    }
}
