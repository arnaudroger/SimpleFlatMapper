package org.simpleflatmapper.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private final List<ClassLoader> delegateClassLoader = new ArrayList<ClassLoader>();
	private final Lock lock = new ReentrantLock();
	
	public FactoryClassLoader(final ClassLoader parent) {
		super(parent);
	}

	public Class<?> registerClass(final String name, final byte[] bytes, ClassLoader classLoader) {
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
                if (classLoader != null && !isAlreadyAccessible(classLoader)) {
                    delegateClassLoader.add(classLoader);
                }
			}
			
			return info.clazz;
		} finally {
			lock.unlock();
		}
	}

    private boolean isAlreadyAccessible(ClassLoader classLoader) {
        if (isAccessibleFrom(classLoader, getParent())) {
            return true;
        }

        for(ClassLoader cl : delegateClassLoader) {
            if (isAccessibleFrom(classLoader, cl)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAccessibleFrom(ClassLoader classLoader, ClassLoader from) {

        ClassLoader parent = from;

        while(parent != null) {
            if (parent.equals(classLoader)) {
                return true;
            }
            parent = parent.getParent();
        }

        return false;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch(ClassNotFoundException e) {
            for(ClassLoader cl : delegateClassLoader) {
                try {
                    return cl.loadClass(name);
                } catch(ClassNotFoundException e2) {
                    ///
                }
            }
            throw e;
        }
    }
}