package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.FloatSetter;

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
