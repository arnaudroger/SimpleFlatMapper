package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

public class PrimitiveDoubleSetter implements Setter<DbPrimitiveObjectWithSetter, Double>, DoubleSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setDouble(DbPrimitiveObjectWithSetter target, double value) throws Exception {
		target.setpDouble(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Double value) throws Exception {
		target.setpDouble(value.doubleValue());
	}

}
