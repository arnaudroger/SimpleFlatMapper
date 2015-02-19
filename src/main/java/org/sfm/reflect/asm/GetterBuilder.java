package org.sfm.reflect.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;

import java.lang.reflect.Method;

public class GetterBuilder implements Opcodes {

	private static final String GETTER_TYPE = AsmUtils.toType(Getter.class);
	private static final String ORG_SFM_REFLECT_PRIMITIVE = AsmUtils.toType(IntGetter.class.getPackage().getName());

	public static byte[] createObjectGetter(final String className, final Method method) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MethodVisitor mv;

		Class<?> target = method.getDeclaringClass();
		Class<?> property = method.getParameterTypes()[0];
		
		String targetType = toType(target);
		String propertyType = toType(property);
		String classType = toType(className);
		
		cw.visit(
				V1_6,
				ACC_PUBLIC + ACC_FINAL + ACC_SUPER,
				classType,
				"Ljava/lang/Object;L" + GETTER_TYPE + "<L" + targetType + ";" + AsmUtils.toTypeParam(propertyType) + ">;",
				"java/lang/Object", new String[] {GETTER_TYPE});

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
			mv = cw.visitMethod(ACC_PUBLIC, "get",
					"(L" + targetType + ";)" + AsmUtils.toTypeParam(propertyType), null,
					new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 1);

			AsmUtils.invoke(mv, target, method.getName(), "()" + AsmUtils.toTypeParam(propertyType));
			
			mv.visitInsn(ARETURN);
			mv.visitMaxs(1, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, targetType);
			mv.visitMethodInsn(INVOKEVIRTUAL, classType, "get", "(L" + targetType + ";)" + AsmUtils.toTypeParam(propertyType), false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}


        {
            mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("{");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("}");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());

	}

	public static byte[] createPrimitiveGetter(String className, Method method) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
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
		
		String valueMethodPrefix = methodSuffix.toLowerCase();
		if ("character".equals(valueMethodPrefix)) {
			valueMethodPrefix = "char";
		}
		
		String getMethod = "get" + methodSuffix;
		String valueMethod = valueMethodPrefix + "Value";

		
		int primitiveLoadOp = AsmUtils.loadOps.get(primitive);
		
		cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL  + ACC_SUPER,  classType, 
				"Ljava/lang/Object;"
				+ "L"  + GETTER_TYPE + "<L" + targetType + ";" + AsmUtils.toTypeParam(propertyType) + ">;"
				+ "L" + ORG_SFM_REFLECT_PRIMITIVE + methodSuffix + "Getter<L" + targetType + ";>;",
				"java/lang/Object", 
				new String[] {GETTER_TYPE,
								ORG_SFM_REFLECT_PRIMITIVE + "/" + methodSuffix + "Getter" });


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
		mv = cw.visitMethod(ACC_PUBLIC, getMethod, "(L" + targetType + ";)" + primitiveType , null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);

		AsmUtils.invoke(mv, target, method.getName(), "(" + primitiveType + ")V");
		
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 3);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "set", "(L" + targetType + ";" + AsmUtils.toTypeParam(propertyType) + ")V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		AsmUtils.invoke(mv, property, valueMethod, "()" + primitiveType);
		AsmUtils.invoke(mv, target, method.getName(), "(" + primitiveType + ")V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 3);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, getMethod, "(Ljava/lang/Object;" + primitiveType + ")V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST,  targetType );
		mv.visitVarInsn(primitiveLoadOp, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL,  classType , getMethod, "(L" + targetType + ";" + primitiveType + ")V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 3);
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
		mv.visitMethodInsn(INVOKEVIRTUAL,  classType , "set", "(L" + targetType + ";" + AsmUtils.toTypeParam(propertyType) + ")V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 3);
		mv.visitEnd();
		}

        {
            mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("{");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn("}");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);


            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());

	}

	private static String toType(Class<?> target) {
		String name = target.getName();
		return toType(name);
	}

	private static String toType(String name) {
		return name.replace('.', '/');
	}
}
