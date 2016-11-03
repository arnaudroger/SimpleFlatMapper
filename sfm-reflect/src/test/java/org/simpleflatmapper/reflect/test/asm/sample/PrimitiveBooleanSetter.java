package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
