package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.LongSetter;

public class PrimitiveLongSetter implements Setter<DbPrimitiveObjectWithSetter, Long>, LongSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setLong(DbPrimitiveObjectWithSetter target, long value) throws Exception {
		target.setpLong(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Long value) throws Exception {
		target.setpLong(value.longValue());
	}
}
