package org.sfm.reflect.asm;

import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class SetterBuilder implements Opcodes {

	public static byte[] createObjectSetter(String className, Method method) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		Class<?> target = method.getDeclaringClass();
		Class<?> property = method.getParameterTypes()[0];
		
		String targetType = toType(target);
		String propertyType = toType(property);
		String classType = toType(className);
		
		cw.visit(
				V1_7,
				ACC_PUBLIC + ACC_FINAL + ACC_SUPER,
				classType,
				"Ljava/lang/Object;Lorg/sfm/reflect/Setter<L" + targetType + ";L" + propertyType + ";>;",
				"java/lang/Object", new String[] { "org/sfm/reflect/Setter" });

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "set",
					"(L" + targetType + ";L" + propertyType + ";)V", null,
					new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL,  targetType , method.getName(), "(L" + propertyType + ";)V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getPropertyType", "()Ljava/lang/Class;", "()Ljava/lang/Class<+L" + propertyType + ";>;", null);
			mv.visitCode();
			mv.visitLdcInsn(Type.getType("L" + propertyType + ";"));
			mv.visitInsn(ARETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, targetType);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(CHECKCAST,  propertyType );
			mv.visitMethodInsn(INVOKEVIRTUAL, classType , "set", "(L" + targetType + ";L" + propertyType + ";)V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}

	public static byte[] createPrimitiveSetter(String className, Method method) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		Class<?> target = method.getDeclaringClass();
		Class<?> primitive = method.getParameterTypes()[0];
		Class<?> property = AsmUtils.wrappers.get(primitive);
		
		String targetType = toType(target);
		String primitiveType = AsmUtils.primitivesType.get(primitive);
		String propertyType = toType(property);
		String classType = toType(className);
		
		String methodSuffix =  property.getSimpleName();
		if ("Integer".equals(methodSuffix)) {
			methodSuffix = "Int";
		}
		
		boolean _64bits = primitive.equals(long.class) || primitive.equals(double.class);
		
		String setMethod = "set" + methodSuffix;
		String valueMethod = methodSuffix.toLowerCase() + "Value";

		
		int primitiveLoadOp = AsmUtils.loadOps.get(primitive);
		
		cw.visit(V1_7, ACC_PUBLIC + ACC_FINAL  + ACC_SUPER,  classType, 
				"Ljava/lang/Object;"
				+ "Lorg/sfm/reflect/Setter<L" + targetType + ";L" + propertyType + ";>;"
				+ "Lorg/sfm/reflect/primitive/" + methodSuffix + "Setter<L" + targetType + ";>;", 
				"java/lang/Object", 
				new String[] { "org/sfm/reflect/Setter", 
								"org/sfm/reflect/primitive/" + methodSuffix + "Setter" });


		{
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, setMethod, "(L" + targetType + ";" + primitiveType + ")V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(primitiveLoadOp, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL,  targetType , method.getName(), "(" + primitiveType + ")V", false);
		mv.visitInsn(RETURN);
		if (_64bits) {
			mv.visitMaxs(3, 4);
		} else {
			mv.visitMaxs(2, 3);
		}
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "set", "(L" + targetType + ";L" + propertyType + ";)V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL,  propertyType , valueMethod, "()" + primitiveType + "", false);
		mv.visitMethodInsn(INVOKEVIRTUAL,  targetType , method.getName(), "(" + primitiveType + ")V", false);
		mv.visitInsn(RETURN);
		if (_64bits) {
			mv.visitMaxs(3, 3);
		} else {
			mv.visitMaxs(2, 3);
		}
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "getPropertyType", "()Ljava/lang/Class;", "()Ljava/lang/Class<+L" + propertyType + ";>;", null);
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC,  propertyType , "TYPE", "Ljava/lang/Class;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, setMethod, "(Ljava/lang/Object;" + primitiveType + ")V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST,  targetType );
		mv.visitVarInsn(primitiveLoadOp, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL,  classType , setMethod, "(L" + targetType + ";" + primitiveType + ")V", false);
		mv.visitInsn(RETURN);
		if (_64bits) {
			mv.visitMaxs(4, 4);
		} else {
			mv.visitMaxs(3, 3);
		}
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST,  targetType );
		mv.visitVarInsn(ALOAD, 2);
		mv.visitTypeInsn(CHECKCAST,  propertyType );
		mv.visitMethodInsn(INVOKEVIRTUAL,  classType , "set", "(L" + targetType + ";L" + propertyType + ";)V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 3);
		mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}

	private static String toType(Class<?> target) {
		String name = target.getName();
		return toType(name);
	}

	private static String toType(String name) {
		return name.replace('.', '/');
	}
}
