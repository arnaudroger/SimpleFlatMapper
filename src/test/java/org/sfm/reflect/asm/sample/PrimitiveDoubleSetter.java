package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.DoubleSetter;

public class PrimitiveDoubleSetter implements Setter<DbPrimitiveObjectWithSetter, Double>, DoubleSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setDouble(DbPrimitiveObjectWithSetter target, double value) throws Exception {
		target.setpDouble(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Double value) throws Exception {
		target.setpDouble(value.doubleValue());
	}

	@Override
	public Class<? extends Double> getPropertyType() {
		return double.class;
	}


}
