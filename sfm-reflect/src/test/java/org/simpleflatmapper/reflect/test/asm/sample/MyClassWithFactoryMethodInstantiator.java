package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.test.InstantiatorFactoryTest;

import java.io.InputStream;


public final class MyClassWithFactoryMethodInstantiator implements Instantiator<InputStream, InstantiatorFactoryTest.MyClassWithFactoryMethod> {
	@Override
	public InstantiatorFactoryTest.MyClassWithFactoryMethod newInstance(InputStream source) throws Exception {
		return InstantiatorFactoryTest.MyClassWithFactoryMethod.valueOf(null);
	}
}
