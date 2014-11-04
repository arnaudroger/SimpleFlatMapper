package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.reflect.Setter;

public class DbObjectStringSetter implements Setter<DbObject, String> {

	@Override
	public void set(DbObject target, String value) throws Exception {
		target.setName(value);
	}

}
