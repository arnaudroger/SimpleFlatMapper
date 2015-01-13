package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.reflect.Instantiator;

import java.sql.ResultSet;

public final class DbObjectInstantiator implements Instantiator<ResultSet, DbObject> {
	@Override
	public DbObject newInstance(ResultSet source) throws Exception {
		return new DbObject();
	}
}
