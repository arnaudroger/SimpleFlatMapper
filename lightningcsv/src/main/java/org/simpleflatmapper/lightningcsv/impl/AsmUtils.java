package org.simpleflatmapper.lightningcsv.impl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

}
