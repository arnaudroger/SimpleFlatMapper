package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.ShortSetter;

public class PrimitiveShortSetter implements Setter<DbPrimitiveObjectWithSetter, Short>, ShortSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setShort(DbPrimitiveObjectWithSetter target, short value) throws Exception {
		target.setpShort(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Short value) throws Exception {
		target.setpShort(value.shortValue());
	}

}
