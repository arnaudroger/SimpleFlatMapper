package org.sfm.benchmark;

import org.junit.Assert;
import org.sfm.beans.DbObject;
import org.sfm.utils.Handler;

public final class ValidateHandler implements Handler<DbObject> {
	public long c;

	@Override
	public void handle(DbObject t) throws Exception {
		Assert.assertNotNull(t.getName());
		c++;
	}
}