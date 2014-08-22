package org.sfm.reflect.asm;

import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.LLOAD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class AsmUtils {
	public static String toType(final Class<?> target) {
		String name = target.getName();
		return toType(name);
	}

	public static String toType(final String name) {
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
	static final Map<String, String> stringToPrimitivesType = new HashMap<String, String>() {
		{
			  put("Boolean", "Z");
		      put("Byte", "B");
		      put("Character", "C");
		      put("Double", "D");
		      put("Float", "F");
		      put("Int", "I");
		      put("Long", "J");
		      put("Short", "S");
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
	
	
	static File targetDir = null;
	static {
		String targetDirStr = System.getProperty("asm.dump.target.dir");
		if (targetDirStr != null) {
			targetDir = new File(targetDirStr);
			targetDir.mkdirs();
		} 
	}
	
	public static byte[] writeClassToFile (final String classname, final byte[] bytes) throws IOException {
		if (targetDir != null) {
			final int lastIndex = classname.lastIndexOf('.');
			final String filename = classname.substring(lastIndex + 1) + ".class";
			final String directory = classname.substring(0, lastIndex).replace('.', '/');
			final File packageDir = new File(targetDir, directory);
			packageDir.mkdirs();
			
			final FileOutputStream fos = new FileOutputStream(new File(packageDir, filename ));
			try {
				fos.write(bytes);
			} finally {
				fos.close();
			}
		}		
		return bytes;
	}

	public static String toTypeWithParam(Class<?> class1) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(toType(class1));
		
		TypeVariable<?>[] typeParameters = class1.getTypeParameters();
		
		if (typeParameters != null && typeParameters.length > 0) {
			sb.append("<");
			
			for(TypeVariable<?> t : typeParameters) {
				sb.append("L").append(toType(t.getName())).append(";");
			}
			
			sb.append(">");
		}
		
		return sb.toString();
	}
}
