package org.simpleflatmapper.core.reflect.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.simpleflatmapper.util.TypeHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;

public class AsmUtils {

	public static final String ASM_DUMP_TARGET_DIR = "asm.dump.target.dir";

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
		wrappers.put(void.class, Void.class);
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
		primitivesType.put(void.class, "V");
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
	static final Map<Class<?>, Integer> returnOps = new HashMap<Class<?>, Integer>();

    static {
        returnOps.put(boolean.class, IRETURN);
        returnOps.put(byte.class, IRETURN);
        returnOps.put(char.class, IRETURN);
        returnOps.put(double.class, DRETURN);
        returnOps.put(float.class, FRETURN);
        returnOps.put(int.class, IRETURN);
        returnOps.put(long.class, LRETURN);
        returnOps.put(short.class, IRETURN);
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
		String targetDirStr = System.getProperty(ASM_DUMP_TARGET_DIR);
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
		return writeClassToFileInDir(className, bytes,  AsmUtils.targetDir);
	}

	public static byte[] writeClassToFileInDir(String className, byte[] bytes, File targetDir) throws IOException {
		if (targetDir != null) {
			_writeClassToFileInDir(className, bytes, targetDir);
		}
		return bytes;
	}

	private static void _writeClassToFileInDir(String className, byte[] bytes, File targetDir) throws IOException {
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
				// meethod parameter
				return null;
			}
		}
		
		int indexOf = sig.indexOf('<');
		if (indexOf == -1) {
			return Class.forName(sig.replace('/','.'));
		} else {
			final Class<?> rawType = Class.forName(sig.substring(0, indexOf).replace('/','.'));

			final Type[] types = parseTypes(sig.substring(indexOf+ 1, sig.length() - 1), genericTypeNames, target);
			
			return new ParameterizedTypeImpl(rawType, types);
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
		final Class<?> clazz = TypeHelper.toClass(type);
		if (clazz.isPrimitive()) {
			return wrappers.get(clazz);
		} else return clazz;
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

	public static List<String> extractTypeNames(String sig) {
		final List<String> types = new ArrayList<String>();

		SignatureReader reader = new SignatureReader(sig);
		reader.accept(new SignatureVisitor(Opcodes.ASM5) {

			//TypeSignature =
			// visitBaseType | visitTypeVariable | visitArrayType | ( visitClassType visitTypeArgument* ( visitInnerClassType visitTypeArgument* )* visitEnd ) )

			class AppendType extends SignatureVisitor {

				StringBuilder sb = new StringBuilder();
				int l = 0;
				public AppendType() {
					super(Opcodes.ASM5);
				}

				@Override
				public void visitBaseType(char descriptor) {
					if (descriptor != 'V') {
						sb.append(descriptor);
						visitEnd();
					}
				}

				@Override
				public void visitTypeVariable(String name) {
					sb.append("T");
					sb.append(name);
					visitEnd();
				}

				@Override
				public SignatureVisitor visitArrayType() {
					sb.append("[");
					return this;
				}

				@Override
				public void visitClassType(String name) {
					sb.append("L");
					sb.append(name);
					visitEnd();
				}

				@Override
				public void visitInnerClassType(String name) {
					visitClassType(name);
				}

				@Override
				public void visitTypeArgument() {
				}

				@Override
				public SignatureVisitor visitTypeArgument(char wildcard) {
					l++;
					if (sb.length() == 0) {
						String t = types.remove(types.size() - 1);
						if (t.endsWith(";")) {
							t = t.substring(0, t.length() -1);
						}
						sb.append(t);
						sb.append("<");
					}
					if  (wildcard != '=') {
						sb.append(wildcard);
					}
					return this;
				}

				@Override
				public void visitEnd() {
					if (l == 0) {
						flush();
					} else {
						sb.append(";>");
						l--;
					}
				}

				private void flush() {
					if (sb.length() >0) {
						if (sb.charAt(0) == 'L' || sb.charAt(0) == 'T') {
							sb.append(";");
						}
						types.add(sb.toString());

						sb = new StringBuilder();
					}
				}
			}

			@Override
			public void visitFormalTypeParameter(String name) {
			}

			@Override
			public SignatureVisitor visitClassBound() {
				return super.visitInterfaceBound();
			}

			@Override
			public SignatureVisitor visitInterfaceBound() {
				return super.visitInterfaceBound();
			}

			@Override
			public SignatureVisitor visitParameterType() {
				return new AppendType();
			}

			@Override
			public SignatureVisitor visitReturnType() {
				return new AppendType();
			}

			@Override
			public SignatureVisitor visitExceptionType() {
				return new AppendType();
			}
		});



		return types;
	}

    public static String toDeclaredLType(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return primitivesType.get(clazz);
		}
        return toDeclaredLType(toType(clazz));
    }

	public static String toSignature(Method exec) {
		StringBuilder sb = new StringBuilder();

		sb.append("(");
		for(Class<?> clazz : exec.getParameterTypes()) {
			sb.append(AsmUtils.toDeclaredLType(clazz));
		}
		sb.append(")");

		sb.append(AsmUtils.toDeclaredLType(exec.getReturnType()));

		return sb.toString();
	}


	private static class ParameterizedTypeImpl implements ParameterizedType {

        private final Class<?> rawType;
        private final Type[] types;

        public ParameterizedTypeImpl(Class<?> rawType, Type[] types) {
            this.rawType = rawType;
            this.types = types;
        }

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

        @Override
        public String toString() {
            return "ParameterizedTypeImpl{" +
                    "rawType=" + rawType +
                    ", types=" + Arrays.toString(types) +
                    '}';
        }

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ParameterizedType)) return false;

			ParameterizedType that = (ParameterizedType) o;

			if (!rawType.equals(that.getRawType())) return false;
			// Probably incorrect - comparing Object[] arrays with Arrays.equals
			return Arrays.equals(types, that.getActualTypeArguments()) && that.getOwnerType() == null;

		}

		@Override
		public int hashCode() {
			int result = rawType.hashCode();
			result = 31 * result + Arrays.hashCode(types);
			return result;
		}
	}

    public static void addIndex(MethodVisitor mv, int i) {
        switch(i) {
            case 0:
                mv.visitInsn(ICONST_0);
                return;
            case 1:
                mv.visitInsn(ICONST_1);
                return;
            case 2:
                mv.visitInsn(ICONST_2);
                return;
            case 3:
                mv.visitInsn(ICONST_3);
                return;
            case 4:
                mv.visitInsn(ICONST_4);
                return;
            case 5:
                mv.visitInsn(ICONST_5);
                return;
            default:
                if (i <= Byte.MAX_VALUE) {
                    mv.visitIntInsn(BIPUSH, i);
                } else if (i <= Short.MAX_VALUE) {
                    mv.visitIntInsn(SIPUSH, i);
                } else {
                    mv.visitLdcInsn(i);
                }
        }
    }
}
