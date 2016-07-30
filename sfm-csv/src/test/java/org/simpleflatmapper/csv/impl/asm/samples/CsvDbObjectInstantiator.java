package org.simpleflatmapper.csv.impl.asm.samples;

import org.sfm.beans.DbObject;
import org.sfm.reflect.Instantiator;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;

@SuppressWarnings("rawtypes")
public final class CsvDbObjectInstantiator implements Instantiator<DelayedCellSetter[], DbObject> {
	@Override
	public DbObject newInstance(DelayedCellSetter[] source) throws Exception {
		return new DbObject();
	}
}
