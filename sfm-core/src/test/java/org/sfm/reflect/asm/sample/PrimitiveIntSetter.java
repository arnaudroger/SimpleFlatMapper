package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.IntSetter;

public class PrimitiveIntSetter implements Setter<DbPrimitiveObjectWithSetter, Integer>, IntSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setInt(DbPrimitiveObjectWithSetter target, int value) throws Exception {
		target.setpInt(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Integer value) throws Exception {
		target.setpInt(value.intValue());
	}

}
