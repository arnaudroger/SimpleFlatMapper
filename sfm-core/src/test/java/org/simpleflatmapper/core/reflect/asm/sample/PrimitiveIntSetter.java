package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.primitive.IntSetter;

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
