package org.sfm.reflect.asm;


class FactoryClassLoader extends ClassLoader {

	public FactoryClassLoader(final ClassLoader parent) {
		super(parent);
	}

	public Class<?> registerClass(final String name, final byte[] bytes) {
		return defineClass(name, bytes, 0, bytes.length);
	}
}