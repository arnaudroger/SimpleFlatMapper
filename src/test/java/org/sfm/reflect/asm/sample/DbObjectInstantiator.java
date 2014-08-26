package org.sfm.reflect.asm.sample;

import java.sql.ResultSet;

import org.sfm.beans.DbObject;
import org.sfm.reflect.Instantiator;

public final class DbObjectInstantiator implements Instantiator<ResultSet, DbObject> {
	@Override
	public DbObject newInstance(ResultSet source) throws Exception {
		return new DbObject();
	}
}
