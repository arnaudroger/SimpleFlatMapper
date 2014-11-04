package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.BooleanSetter;

public final class PrimitiveBooleanSetter implements Setter<DbPrimitiveObjectWithSetter, Boolean>, BooleanSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setBoolean(DbPrimitiveObjectWithSetter target, boolean value) throws Exception {
		target.setpBoolean(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Boolean value) throws Exception {
		target.setpBoolean(value.booleanValue());
	}

}
