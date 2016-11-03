package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectFields;

public class PrimitiveIntFieldSetter implements Setter<DbPrimitiveObjectFields, Integer>, IntSetter<DbPrimitiveObjectFields> {

	@Override
	public void setInt(DbPrimitiveObjectFields target, int value) throws Exception {
		target.pInt = value;
	}

	@Override
	public void set(DbPrimitiveObjectFields target, Integer value) throws Exception {
		target.pInt = value.intValue();
	}

}
