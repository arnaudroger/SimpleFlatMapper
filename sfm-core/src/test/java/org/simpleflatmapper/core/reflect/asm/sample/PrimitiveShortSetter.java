package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.primitive.ShortSetter;

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
