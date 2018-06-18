package org.simpleflatmapper.csv.test.impl.asm.samples;


import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.csv.impl.CsvMapperCellHandlerImpl;
import org.simpleflatmapper.csv.impl.DelayedGetter;

@SuppressWarnings("rawtypes")
public final class CsvDbFinalObjectInstantiator implements Instantiator<CsvMapperCellHandlerImpl<DbFinalObject>, DbFinalObject> {
	
	DelayedGetter<DbFinalObject, Long> getter1;
	
	@SuppressWarnings("unchecked")
	@Override
	public DbFinalObject newInstance(CsvMapperCellHandlerImpl<DbFinalObject> source) throws Exception {
		return new DbFinalObject(getter1.get(source).longValue(), null, null, null, null, null);
	}
}
