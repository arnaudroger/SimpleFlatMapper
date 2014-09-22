package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.csv.DelayedGetter;
import org.sfm.csv.DelayedSetter;
import org.sfm.reflect.Instantiator;

public final class CsvDbFinalObjectInstantiator implements Instantiator<DelayedSetter[], DbFinalObject> {
	
	DelayedGetter<Long> getter1;
	
	@Override
	public DbFinalObject newInstance(DelayedSetter[] source) throws Exception {
		return new DbFinalObject(getter1.get(source).longValue(), null, null, null, null, null);
	}
}
