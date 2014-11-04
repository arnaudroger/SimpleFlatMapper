package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbExtentedType;
import org.sfm.reflect.Setter;

public class BytesSetter implements Setter<DbExtentedType, byte[]> {

	@Override
	public void set(DbExtentedType target, byte[] value) throws Exception {
		target.setBytes(value);
	}
}
