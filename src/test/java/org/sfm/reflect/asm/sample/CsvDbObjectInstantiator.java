package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.csv.DelayedCellSetter;
import org.sfm.reflect.Instantiator;

@SuppressWarnings("rawtypes")
public final class CsvDbObjectInstantiator implements Instantiator<DelayedCellSetter[], DbObject> {
	@Override
	public DbObject newInstance(DelayedCellSetter[] source) throws Exception {
		return new DbObject();
	}
}
