package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
