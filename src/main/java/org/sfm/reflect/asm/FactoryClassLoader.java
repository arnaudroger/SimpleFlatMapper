package org.sfm.reflect.asm;

import java.util.HashMap;
import java.util.Map;

class FactoryClassLoader extends ClassLoader {

	public FactoryClassLoader(final ClassLoader parent) {
		super(parent);
	}

	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		final Class<?> type = classes.get(name);
		
		if (type != null) {
			return type; 
		} else {
			return super.findClass(name);
		}
	}
	
	public Class<?> registerClass(final String name, final byte[] bytes) {
		Class<?> type = classes.get(name);
		if (type == null) {
			type = defineClass(name, bytes, 0, bytes.length);
			return type;
		} else {
			throw new RuntimeException("Class " + name + " already defined");
		}
	}
}