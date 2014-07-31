package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.reflect.Instantiator;

public final class DbObjectInstantiator implements Instantiator<DbObject> {
	@Override
	public DbObject newInstance() throws Exception {
		return new DbObject();
	}
}
