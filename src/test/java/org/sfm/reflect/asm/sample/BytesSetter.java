package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbExtendedType;
import org.sfm.reflect.Setter;

public class BytesSetter implements Setter<DbExtendedType, byte[]> {

	@Override
	public void set(DbExtendedType target, byte[] value) throws Exception {
		target.setBytes(value);
	}
}
