package org.sfm.reflect.asm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



class FactoryClassLoader extends ClassLoader {

	private class ClassInfo {
		private final byte[] bytes;
		private final Class<?> clazz;
		private ClassInfo(byte[] bytes, Class<?> clazz) {
			super();
			this.bytes = bytes;
			this.clazz = clazz;
		}
 	}

	private Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();
	private Lock lock = new ReentrantLock();
	
	public FactoryClassLoader(final ClassLoader parent) {
		super(parent);
	}

	public Class<?> registerClass(final String name, final byte[] bytes) {
		lock.lock();
		try {
			ClassInfo info = classes.get(name);
			
			if (info != null) {
				if (!Arrays.equals(info.bytes, bytes)) {
					throw new LinkageError("Class " + name  + " is defined with different bytecodes");
				}
			} else {
				Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
				info = new ClassInfo(bytes, clazz);
				classes.put(name, info);
			}
			
			return info.clazz;
		} finally {
			lock.unlock();
		}
	}
}