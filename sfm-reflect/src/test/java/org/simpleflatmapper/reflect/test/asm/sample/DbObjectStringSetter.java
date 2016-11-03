package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.test.beans.DbObject;

public class DbObjectStringSetter implements Setter<DbObject, String> {

	@Override
	public void set(DbObject target, String value) throws Exception {
		target.setName(value);
	}

}
