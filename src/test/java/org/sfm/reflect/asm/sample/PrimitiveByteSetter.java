package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.ByteSetter;

public class PrimitiveByteSetter implements Setter<DbPrimitiveObjectWithSetter, Byte>, ByteSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setByte(DbPrimitiveObjectWithSetter target, byte value) throws Exception {
		target.setpByte(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Byte value) throws Exception {
		target.setpByte(value.byteValue());
	}

}
