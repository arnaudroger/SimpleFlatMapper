package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
