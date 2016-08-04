package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.test.beans.DbExtendedType;

public class BytesSetter implements Setter<DbExtendedType, byte[]> {

	@Override
	public void set(DbExtendedType target, byte[] value) throws Exception {
		target.setBytes(value);
	}
}
