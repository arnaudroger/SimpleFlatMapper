package org.sfm.jdbc;


public class AsmHelper {
	
	
	private static final boolean asmPresent = _isAsmPresent();

	private static boolean _isAsmPresent() {
		try {
			Class.forName("org.objectweb.asm.Opcodes");
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isAsmPresent() {
		return asmPresent;
	}

}
