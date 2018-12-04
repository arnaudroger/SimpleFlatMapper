package org.simpleflatmapper.lightningcsv.impl;


import org.simpleflatmapper.ow2asm.Opcodes;
import org.simpleflatmapper.util.TypeHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AsmUtils {

	public static final String ASM_DUMP_TARGET_DIR = "asm.dump.target.dir";

	static File targetDir = null;

	static {
		String targetDirStr = System.getProperty(ASM_DUMP_TARGET_DIR);
		if (targetDirStr != null) {
			targetDir = new File(targetDirStr);
			targetDir.mkdirs();
		}
	}
	
	public static final int API;
	static {
		//IFJAVA8_START
		if (true) API = Opcodes.ASM7_EXPERIMENTAL; else
			//IFJAVA8_END
			API = Opcodes.ASM5;
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
	
	public static String toAsmType(final String name) {
		return name.replace('.', '/');
	}

	public static String toAsmType(final Type type) {
		if (TypeHelper.isPrimitive(type)) {
			return primitivesType.get(TypeHelper.toClass(type));
		}
		return toAsmType(TypeHelper.toClass(type).getName());
	}

	public static String toTargetTypeDeclaration(Type targetType) {
		if (TypeHelper.isPrimitive(targetType)) {
			return primitivesType.get(TypeHelper.toClass(targetType));
		}
		return toTargetTypeDeclaration(AsmUtils.toAsmType(targetType));

	}
	public static String toTargetTypeDeclaration(String targetType) {
		if (targetType.startsWith("[")) {
			return targetType;
		} else {
			return "L" + targetType+ ";";
		}
	}

	public static String toGenericAsmType(final Type type) {
		StringBuilder sb = new StringBuilder();


		sb.append(toAsmType(type));

		Type[] typeParameters = null;

		if (type instanceof ParameterizedType) {
			typeParameters = ((ParameterizedType) type).getActualTypeArguments();
		}

		if (typeParameters != null && typeParameters.length > 0) {
			sb.append("<");

			for(Type t : typeParameters) {
				sb.append(toTargetTypeDeclaration(toGenericAsmType(t)));
			}

			sb.append(">");
		}

		return sb.toString();
	}


}
