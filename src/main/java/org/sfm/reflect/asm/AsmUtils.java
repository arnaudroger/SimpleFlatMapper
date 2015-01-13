package org.sfm.reflect.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sfm.reflect.TypeHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;
public class AsmUtils {

	public static String toType(final Type target) {
		return toType(TypeHelper.toClass(target));
	}
	public static String toType(final Class<?> target) {
		if (target.isPrimitive()) {
			return primitivesType.get(target);
		}
		return toType(getPublicOrInterfaceClass(target).getName());
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
	static final Map<Class<?>, Integer> defaultValue = new HashMap<Class<?>, Integer>() {
		{
			  put(boolean.class, ICONST_0);
		      put(byte.class, ICONST_0);
		      put(char.class, ICONST_0);
		      put(double.class, DCONST_0);
		      put(float.class, FCONST_0);
		      put(int.class, ICONST_0);
		      put(long.class, LCONST_0);
		      put(short.class, ICONST_0);
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
	
	public static boolean isStillGeneric(Class<? > clazz) {
		clazz = getPublicOrInterfaceClass(clazz);
		final TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
		return typeParameters != null && typeParameters.length > 0;
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
		
		class1 = getPublicOrInterfaceClass(class1);
		
		sb.append(toType(class1));
		
		TypeVariable<?>[] typeParameters = class1.getTypeParameters();
		
		if (typeParameters != null && typeParameters.length > 0) {
			sb.append("<");
			
			for(TypeVariable<?> t : typeParameters) {
				String typeName = t.getName();
				sb.append(toTypeParam(typeName));
			}
			
			sb.append(">");
		}
		
		return sb.toString();
	}

	public static String toTypeParam(String typeName) {
		if (typeName.startsWith("[")) {
			return typeName;
		} else {
			return "L" + toType(typeName) + ";";
		}
	}
	public static Type toGenericType(String sig, List<String> genericTypeNames, Type target) throws ClassNotFoundException {
		if (sig.length() == 1) {
			switch (sig.charAt(0)) {
			case 'Z': return boolean.class;
			case 'B': return byte.class;
			case 'C': return char.class;
			case 'D': return double.class;
			case 'F': return float.class;
			case 'I': return int.class;
			case 'J': return long.class;
			case 'S': return short.class;
			}
		}
		
		if (sig.startsWith("L")) {
			sig = sig.substring(1);
			if (sig.endsWith(";")) {
				sig = sig.substring(0, sig.length() - 1);
			}
		} else if (sig.startsWith("T")) {
			int indexOfParam = genericTypeNames.indexOf(sig.substring(1, sig.length() -1));
			if (target instanceof  ParameterizedType) {
				return ((ParameterizedType) target).getActualTypeArguments()[indexOfParam];
			} else {
				throw new IllegalArgumentException("Cannot resolve generic type " + sig + " from non ParameterizedType " + target);
			}
		}
		
		int indexOf = sig.indexOf('<');
		if (indexOf == -1) {
			return Class.forName(sig.replace('/','.'));
		} else {
			final Class<?> rawType = Class.forName(sig.substring(0, indexOf).replace('/','.'));

			final Type[] types = parseTypes(sig.substring(indexOf+ 1, sig.length() - 1), genericTypeNames, target);
			
			return new ParameterizedType() {
				
				@Override
				public Type getRawType() {
					return rawType;
				}
				
				@Override
				public Type getOwnerType() {
					return null;
				}
				
				@Override
				public Type[] getActualTypeArguments() {
					return types;
				}
			};
		}
	}


	public static Class<?> getPublicOrInterfaceClass(Class<?> clazz) {
		if (! Modifier.isPublic(clazz.getModifiers()) && ! Modifier.isStatic(clazz.getModifiers())) {
			Class<?>[] interfaces = clazz.getInterfaces();
			if (interfaces != null && interfaces.length > 0) {
				return interfaces[0];
			} else {
				return getPublicOrInterfaceClass(clazz.getSuperclass());
			}
		}
		
		return clazz;
	}

	public static void invoke(MethodVisitor mv, Class<?> target,
			String method, String sig) {
		Class<?> publicClass = getPublicOrInterfaceClass(target);
		boolean isinterface = publicClass.isInterface();
		mv.visitMethodInsn(isinterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL, toType(publicClass), method, sig, isinterface);
	}
	public static String toDeclaredLType(String sourceType) {
		if (sourceType.startsWith("[L") || sourceType.startsWith("L")) {
			return sourceType;
		}
		return "L" + sourceType + ";";
	}
	public static Class<?> toWrapperClass(Type type) {
		return wrappers.get(TypeHelper.toClass(type));
	}
	public static String toWrapperType(Type type) {
		return toType(toWrapperClass(type));
	}

	public static List<String> extractGenericTypeNames(String sig) {
		List<String> types = new ArrayList<String>();

		boolean nameDetected = false;
		int currentStart = -1;
		for(int i = 0; i < sig.length(); i++) {
			char c = sig.charAt(i);
			switch(c) {
				case '<' :
				case ';' :
					if (!nameDetected) {
						nameDetected = true;
						currentStart = i + 1;
					}
					break;
				case ':' :
					types.add(sig.substring(currentStart, i));
					nameDetected = false;
					break;
			}
		}

		return types;
	}

	private static Type[] parseTypes(String sig, List<String> genericTypeNames, Type target) throws ClassNotFoundException {
		List<Type> types = new ArrayList<Type>();

		int genericLevel = 0;
		int currentStart = 0;
		for(int i = 0; i < sig.length(); i++) {
			char c = sig.charAt(i);
			switch(c) {
				case '<': genericLevel ++; break;
				case '>': genericLevel --; break;
				case ';' :
					if (genericLevel == 0) {
						types.add(toGenericType(sig.substring(currentStart, i), genericTypeNames, target));
						currentStart = i;
					}
					break;
			}
		}

		return types.toArray(new Type[] {});
	}

	public static List<String> extractConstructorTypeNames(String sig) {
		List<String> types = new ArrayList<String>();
		int currentStart = 1;
		boolean array = false;
		for(int i = 1; i < sig.length() -2 ; i++) {
			char c = sig.charAt(i);
			switch (c) {
				case '[':
					array = true;
					break;
				case 'T':
				case 'L':

					int genericLevel = 0;
					i++;
					while(c != ';' || genericLevel != 0) {
						switch(c) {
							case '<':
								genericLevel++;
								break;
							case '>':
								genericLevel--;
								break;
						}

						i ++;
						c = sig.charAt(i);
					}
					types.add(sig.substring(currentStart, i + 1));
					currentStart = i + 1;
					array = false;
					break;


				case 'Z':
				case 'B':
				case 'C':
				case 'D':
				case 'F':
				case 'I':
				case 'J':
				case 'S':
					types.add(sig.substring(currentStart, i +1));
					currentStart = i + 1;
					array = false;
					break;
			}
		}

		return types;
	}
}
