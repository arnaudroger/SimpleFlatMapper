package org.sfm.reflect.asm.sample;

import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactoryTest;

import java.io.InputStream;


public final class MyClassWithFactoryMethodInstantiator implements Instantiator<InputStream, InstantiatorFactoryTest.MyClassWithFactoryMethod> {
	@Override
	public InstantiatorFactoryTest.MyClassWithFactoryMethod newInstance(InputStream source) throws Exception {
		return InstantiatorFactoryTest.MyClassWithFactoryMethod.valueOf(null);
	}
}
