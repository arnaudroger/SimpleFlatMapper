package org.sfm.jdbc;

import org.sfm.reflect.asm.AsmFactory;

public class AsmHelper {
	
	public static AsmFactory getAsmSetterFactory() {
		return asmPresent ? new AsmFactory() : null;
	}
	
	private static final boolean asmPresent = isAsmPresent();

	public static boolean isAsmPresent() {
		try {
			Class.forName("org.objectweb.asm.Opcodes");
			return true;
		} catch(Exception e) {
			return false;
		}
	}

}
