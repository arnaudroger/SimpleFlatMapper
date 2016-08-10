package org.simpleflatmapper.reflect.asm;


public class AsmHelper {


	private static final boolean asmPresent = _isAsmPresent();
	//public static final String ASM_CLASS = "org.objectweb.asm.ClassVisitor";
	public static final String ASM_CLASS = "org.simpleflatmapper.ow2asm.ClassVisitor";

	@SuppressWarnings("SpellCheckingInspection")
	private static boolean _isAsmPresent() {
		try {
			return !Class.forName(ASM_CLASS, false, AsmHelper.class.getClassLoader()).isInterface();
		} catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isAsmPresent() {
		return asmPresent;
	}

}
