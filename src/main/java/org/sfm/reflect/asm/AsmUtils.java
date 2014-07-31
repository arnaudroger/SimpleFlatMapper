package org.sfm.reflect.asm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static org.objectweb.asm.Opcodes.*;
public class AsmUtils {
	public static String toType(Class<?> target) {
		String name = target.getName();
		return toType(name);
	}

	public static String toType(String name) {
		return name.replace('.', '/');
	}
	
	@SuppressWarnings("serial")
	static final Map<Class<?>, Class<?>> wrappers = new HashMap<Class<?>, Class<?>>() {
		{
			  put(boolean.class, Boolean.class);
		      put(byte.class, Byte.class);
		      put(char.class, Character.class);
		      put(double.class, Double.class);
		      put(float.class, Float.class);
		      put(int.class, Integer.class);
		      put(long.class, Long.class);
		      put(short.class, Short.class);
		}
	};
	
	@SuppressWarnings("serial")
	static final Map<Class<?>, String> primitivesType = new HashMap<Class<?>, String>() {
		{
			  put(boolean.class, "Z");
		      put(byte.class, "B");
		      put(char.class, "C");
		      put(double.class, "D");
		      put(float.class, "F");
		      put(int.class, "I");
		      put(long.class, "J");
		      put(short.class, "S");
		}
	};	
	
	@SuppressWarnings("serial")
	static final Map<Class<?>, Integer> loadOps = new HashMap<Class<?>, Integer>() {
		{
			  put(boolean.class, ILOAD);
		      put(byte.class, ILOAD);
		      put(char.class, ILOAD);
		      put(double.class, DLOAD);
		      put(float.class, FLOAD);
		      put(int.class, ILOAD);
		      put(long.class, LLOAD);
		      put(short.class, ILOAD);
		}
	};	
	
	@SuppressWarnings("serial")
	static final Set<Class<?>> primitivesClassAndWrapper = new HashSet<Class<?>>() {
		{
			addAll(wrappers.keySet());
			addAll(wrappers.values());
		}
	};
}
