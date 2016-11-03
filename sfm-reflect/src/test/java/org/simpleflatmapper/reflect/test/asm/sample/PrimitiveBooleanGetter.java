package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
