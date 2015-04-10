package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbObject;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactoryTest;

import java.sql.ResultSet;

public final class MyClassWithFactoryMethodInstantiator implements Instantiator<ResultSet, InstantiatorFactoryTest.MyClassWithFactoryMethod> {
	@Override
	public InstantiatorFactoryTest.MyClassWithFactoryMethod newInstance(ResultSet source) throws Exception {
		return InstantiatorFactoryTest.MyClassWithFactoryMethod.valueOf(null);
	}
}
