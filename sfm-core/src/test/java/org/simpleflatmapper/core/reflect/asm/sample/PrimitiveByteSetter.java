package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.primitive.ByteSetter;

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
