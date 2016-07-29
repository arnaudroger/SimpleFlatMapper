package org.sfm.reflect.asm;


public class AsmHelper {
	
	
	private static final boolean asmPresent = _isAsmPresent();

	@SuppressWarnings("SpellCheckingInspection")
	private static boolean _isAsmPresent() {
		try {
			return !Class.forName("org.objectweb.asm.ClassVisitor", false, AsmHelper.class.getClassLoader()).isInterface();
		} catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isAsmPresent() {
		return asmPresent;
	}

}
