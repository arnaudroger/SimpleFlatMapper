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

	public static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

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
	
	static final Map<Class<?>, Class<?>> wrappers = new HashMap<Class<?>, Class<?>>();
	static {
		wrappers.put(boolean.class, Boolean.class);
		wrappers.put(byte.class, Byte.class);
		wrappers.put(char.class, Character.class);
		wrappers.put(double.class, Double.class);
		wrappers.put(float.class, Float.class);
		wrappers.put(int.class, Integer.class);
		wrappers.put(long.class, Long.class);
		wrappers.put(short.class, Short.class);
	}
	
	static final Map<Class<?>, String> primitivesType = new HashMap<Class<?>, String>();
	static {
		primitivesType.put(boolean.class, "Z");
		primitivesType.put(byte.class, "B");
		primitivesType.put(char.class, "C");
		primitivesType.put(double.class, "D");
		primitivesType.put(float.class, "F");
		primitivesType.put(int.class, "I");
		primitivesType.put(long.class, "J");
		primitivesType.put(short.class, "S");
	}
	
	static final Map<String, String> stringToPrimitivesType = new HashMap<String, String>();
	static {
		stringToPrimitivesType.put("Boolean", "Z");
		stringToPrimitivesType.put("Byte", "B");
		stringToPrimitivesType.put("Character", "C");
		stringToPrimitivesType.put("Double", "D");
		stringToPrimitivesType.put("Float", "F");
		stringToPrimitivesType.put("Int", "I");
		stringToPrimitivesType.put("Long", "J");
		stringToPrimitivesType.put("Short", "S");
	}
	
	static final Map<Class<?>, Integer> loadOps = new HashMap<Class<?>, Integer>();
	static {
		loadOps.put(boolean.class, ILOAD);
		loadOps.put(byte.class, ILOAD);
		loadOps.put(char.class, ILOAD);
		loadOps.put(double.class, DLOAD);
		loadOps.put(float.class, FLOAD);
		loadOps.put(int.class, ILOAD);
		loadOps.put(long.class, LLOAD);
		loadOps.put(short.class, ILOAD);
	}
	static final Map<Class<?>, Integer> defaultValue = new HashMap<Class<?>, Integer>();
	static {
		defaultValue.put(boolean.class, ICONST_0);
		defaultValue.put(byte.class, ICONST_0);
		defaultValue.put(char.class, ICONST_0);
		defaultValue.put(double.class, DCONST_0);
		defaultValue.put(float.class, FCONST_0);
		defaultValue.put(int.class, ICONST_0);
		defaultValue.put(long.class, LCONST_0);
		defaultValue.put(short.class, ICONST_0);
	}
	static final Set<Class<?>> primitivesClassAndWrapper = new HashSet<Class<?>>();
	static {
		primitivesClassAndWrapper.addAll(wrappers.keySet());
		primitivesClassAndWrapper.addAll(wrappers.values());
	}
	
	
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
	
	public static byte[] writeClassToFile (final String className, final byte[] bytes) throws IOException {
		if (targetDir != null) {
			final int lastIndex = className.lastIndexOf('.');
			final String filename = className.substring(lastIndex + 1) + ".class";
			final String directory = className.substring(0, lastIndex).replace('.', '/');
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
            String templateType = sig.substring(1, sig.length() - (sig.endsWith(";") ? 1 : 0));
            int indexOfParam = genericTypeNames.indexOf(templateType);
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
		boolean isInterface = publicClass.isInterface();
		mv.visitMethodInsn(isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL, toType(publicClass), method, sig, isInterface);
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
						currentStart = i + 1;
					}
					break;
			}
		}

		return types.toArray(EMPTY_TYPE_ARRAY);
	}

	public static List<String> extractConstructorTypeNames(String sig) {
		List<String> types = new ArrayList<String>();
		int currentStart = 1;
		for(int i = 1; i < sig.length() -2 ; i++) {
			char c = sig.charAt(i);
			switch (c) {
				case '[':
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
					break;
			}
		}

		return types;
	}
}
