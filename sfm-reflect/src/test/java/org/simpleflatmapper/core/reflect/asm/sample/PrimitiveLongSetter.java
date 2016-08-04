package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.primitive.LongSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
