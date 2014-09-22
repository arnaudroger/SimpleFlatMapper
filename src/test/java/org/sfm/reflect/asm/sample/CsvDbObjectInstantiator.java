package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.csv.DelayedSetter;
import org.sfm.reflect.Instantiator;

public final class CsvDbObjectInstantiator implements Instantiator<DelayedSetter[], DbObject> {
	@Override
	public DbObject newInstance(DelayedSetter[] source) throws Exception {
		return new DbObject();
	}
}
