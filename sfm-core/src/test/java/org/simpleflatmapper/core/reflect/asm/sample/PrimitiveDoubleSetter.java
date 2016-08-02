package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.primitive.DoubleSetter;

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
