package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.core.reflect.Instantiator;
import org.simpleflatmapper.core.reflect.InstantiatorFactoryTest;

import java.io.InputStream;


public final class MyClassWithFactoryMethodInstantiator implements Instantiator<InputStream, InstantiatorFactoryTest.MyClassWithFactoryMethod> {
	@Override
	public InstantiatorFactoryTest.MyClassWithFactoryMethod newInstance(InputStream source) throws Exception {
		return InstantiatorFactoryTest.MyClassWithFactoryMethod.valueOf(null);
	}
}
