package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
