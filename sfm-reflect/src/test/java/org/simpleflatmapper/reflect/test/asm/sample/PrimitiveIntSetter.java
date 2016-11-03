package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
