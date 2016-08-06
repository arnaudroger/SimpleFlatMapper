package org.simpleflatmapper.reflect.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.simpleflatmapper.reflect.Instantiator;

import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_6;

public class ConstructorBuilder {
	public static byte[] createEmptyConstructor(final String className, final Class<?> sourceClass,
			final Class<?> targetClass) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MethodVisitor mv;

		String targetType = AsmUtils.toType(targetClass);
		String sourceType = AsmUtils.toType(sourceClass);
		String classType = AsmUtils.toType(className);
		String instantiatorType = AsmUtils.toType(Instantiator.class);

		cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType,
				"Ljava/lang/Object;L" + instantiatorType + "<L"
						+ targetType + ";>;", "java/lang/Object",
				new String[] {  instantiatorType });

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(L" + sourceType + ";)L" + targetType
					+ ";", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitTypeInsn(NEW, targetType);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, targetType, "<init>", "()V",
					false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC,
					"newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", null,
					new String[] { "java/lang/Exception" });
			
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, sourceType);
			mv.visitMethodInsn(INVOKEVIRTUAL, classType, "newInstance", "(L" + sourceType + ";)L"
					+ targetType + ";", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}
}
