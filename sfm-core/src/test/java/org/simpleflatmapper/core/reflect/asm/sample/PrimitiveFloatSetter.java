package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.primitive.FloatSetter;

public class PrimitiveFloatSetter implements Setter<DbPrimitiveObjectWithSetter, Float>, FloatSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setFloat(DbPrimitiveObjectWithSetter target, float value) throws Exception {
		target.setpFloat(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Float value) throws Exception {
		target.setpFloat(value.floatValue());
	}

}
