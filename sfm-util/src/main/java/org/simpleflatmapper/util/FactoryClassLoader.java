package org.simpleflatmapper.util;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class FactoryClassLoader extends ClassLoader {

	private class ClassInfo {
		private final byte[] bytes;
		private final Class<?> clazz;
		private ClassInfo(byte[] bytes, Class<?> clazz) {
			super();
			this.bytes = bytes;
			this.clazz = clazz;
		}
 	}

	private final Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();
 	private final WeakReference<ClassLoader> delegateClassLoader;
	private final Lock lock = new ReentrantLock();
	
	public FactoryClassLoader(final ClassLoader parent) {
		super(null);
		if (parent == null) throw new NullPointerException();
		this.delegateClassLoader = new WeakReference<ClassLoader>(parent);
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


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch(ClassNotFoundException e) {
			ClassLoader classLoader = delegateClassLoader.get();
			if (classLoader != null) return classLoader.loadClass(name);
			throw e;
        }
    }
}