package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.reflect.Instantiator;

import java.io.InputStream;


public final class DbObjectInstantiator implements Instantiator<InputStream, DbObject> {
	@Override
	public DbObject newInstance(InputStream source) throws Exception {
		return new DbObject();
	}
}
