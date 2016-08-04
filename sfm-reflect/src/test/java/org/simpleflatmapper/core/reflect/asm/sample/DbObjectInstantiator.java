package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.core.reflect.Instantiator;
import org.simpleflatmapper.test.beans.DbObject;

import java.io.InputStream;


public final class DbObjectInstantiator implements Instantiator<InputStream, DbObject> {
	@Override
	public DbObject newInstance(InputStream source) throws Exception {
		return new DbObject();
	}
}
