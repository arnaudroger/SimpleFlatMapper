package org.sfm.reflect.asm;


public class AsmHelper {
	
	
	private static final boolean asmPresent = _isAsmPresent();

	private static boolean _isAsmPresent() {
		try {
			return !Class.forName("org.objectweb.asm.ClassVisitor").isInterface();
		} catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isAsmPresent() {
		return asmPresent;
	}

}
