package org.simpleflatmapper.csv.test.impl.asm.samples;

import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;

@SuppressWarnings("rawtypes")
public final class CsvDbObjectInstantiator implements Instantiator<DelayedCellSetter[], DbObject> {
	@Override
	public DbObject newInstance(DelayedCellSetter[] source) throws Exception {
		return new DbObject();
	}
}
