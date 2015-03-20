package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbFinalObject;
import org.sfm.csv.impl.CsvMapperObjectSetters;
import org.sfm.csv.impl.DelayedGetter;
import org.sfm.reflect.Instantiator;

@SuppressWarnings("rawtypes")
public final class CsvDbFinalObjectInstantiator implements Instantiator<CsvMapperObjectSetters<DbFinalObject>, DbFinalObject> {
	
	DelayedGetter<Long> getter1;
	
	@SuppressWarnings("unchecked")
	@Override
	public DbFinalObject newInstance(CsvMapperObjectSetters<DbFinalObject> source) throws Exception {
		return new DbFinalObject(getter1.get(source).longValue(), null, null, null, null, null);
	}
}
