package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectFields;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.IntSetter;

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
