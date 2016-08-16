package org.simpleflatmapper.reflect.asm;

import org.simpleflatmapper.ow2asm.ClassWriter;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.ow2asm.Opcodes;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GetterBuilder implements Opcodes {

	private static final String GETTER_TYPE = AsmUtils.toAsmType(Getter.class);
	private static final String ORG_SFM_REFLECT_PRIMITIVE = AsmUtils.toAsmType(IntGetter.class.getPackage().getName());


    public static byte[] createObjectGetter(final String className, final Field field) throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        Class<?> target = field.getDeclaringClass();
        Class<?> property = field.getType();

        String targetType = toType(target);
        String propertyType = toType(property);
        String classType = toType(className);

        cw.visit(
                V1_6,
                ACC_PUBLIC + ACC_FINAL + ACC_SUPER,
                classType,
                "Ljava/lang/Object;L" + GETTER_TYPE + "<L" + targetType + ";" + AsmUtils.toTargetTypeDeclaration(propertyType) + ">;",
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
                    "(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + AsmUtils.toTargetTypeDeclaration(propertyType), null,
                    new String[] { "java/lang/Exception" });
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);

            mv.visitFieldInsn(GETFIELD, targetType, field.getName(), AsmUtils.toTargetTypeDeclaration(propertyType));

            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 2);
            mv.visitEnd();
        }

        appendBridge(cw, targetType, propertyType, classType);

        appendToString(cw);

        cw.visitEnd();

        return AsmUtils.writeClassToFile(className, cw.toByteArray());

    }

	public static byte[] createObjectGetter(final String className, final Method method) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MethodVisitor mv;

		Class<?> target = method.getDeclaringClass();
		Class<?> property = method.getReturnType();
		
		String targetType = toType(target);
		String propertyType = toType(property);
		String classType = toType(className);
		
		cw.visit(
				V1_6,
				ACC_PUBLIC + ACC_FINAL + ACC_SUPER,
				classType,
				"Ljava/lang/Object;L" + GETTER_TYPE + "<L" + targetType + ";" + AsmUtils.toTargetTypeDeclaration(propertyType) + ">;",
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
					"(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + AsmUtils.toTargetTypeDeclaration(propertyType), null,
					new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 1);

			AsmUtils.invoke(mv, target, method.getName(), "()" + AsmUtils.toTargetTypeDeclaration(propertyType));

			mv.visitInsn(ARETURN);
			mv.visitMaxs(1, 2);
			mv.visitEnd();
		}

        appendBridge(cw, targetType, propertyType, classType);

        appendToString(cw);

		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());

	}

    private static void appendBridge(ClassWriter cw, String targetType, String propertyType, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, targetType);
        mv.visitMethodInsn(INVOKEVIRTUAL, classType, "get", "(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + AsmUtils.toTargetTypeDeclaration(propertyType), false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    public static byte[] createPrimitiveGetter(String className, Field field) throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        Class<?> target = field.getDeclaringClass();
        Class<?> primitive = field.getType();
        Class<?> property = AsmUtils.wrappers.get(primitive);

        String targetType = toType(target);
        String primitiveType = AsmUtils.primitivesType.get(primitive);
        String propertyType = toType(property);
        String classType = toType(className);

        String methodSuffix =  property.getSimpleName();
        if ("Integer".equals(methodSuffix)) {
            methodSuffix = "Int";
        }

        String getMethod = "get" + methodSuffix;

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL  + ACC_SUPER,  classType,
                "Ljava/lang/Object;"
                        + "L"  + GETTER_TYPE + "<L" + targetType + ";" + AsmUtils.toTargetTypeDeclaration(propertyType) + ">;"
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
            mv = cw.visitMethod(ACC_PUBLIC, getMethod, "(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + primitiveType , null, new String[] { "java/lang/Exception" });
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);

            mv.visitFieldInsn(GETFIELD, targetType, field.getName(), primitiveType);

            mv.visitInsn(AsmUtils.returnOps.get(primitive));
            mv.visitMaxs(1, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "get", "(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + AsmUtils.toTargetTypeDeclaration(propertyType), null, new String[] { "java/lang/Exception" });
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(GETFIELD, targetType, field.getName(), primitiveType);
            mv.visitMethodInsn(INVOKESTATIC, propertyType, "valueOf", "(" +  primitiveType + ")L" + propertyType + ";" , false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 2);
            mv.visitEnd();
        }
        appendPrimitiveBridges(cw, primitive, targetType, primitiveType, propertyType, classType, getMethod);

        appendToString(cw);
        cw.visitEnd();

        return AsmUtils.writeClassToFile(className, cw.toByteArray());

    }

    public static byte[] createPrimitiveGetter(String className, Method method) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MethodVisitor mv;

		Class<?> target = method.getDeclaringClass();
		Class<?> primitive = method.getReturnType();
		Class<?> property = AsmUtils.wrappers.get(primitive);
		
		String targetType = toType(target);
		String primitiveType = AsmUtils.primitivesType.get(primitive);
		String propertyType = toType(property);
		String classType = toType(className);
		
		String methodSuffix =  property.getSimpleName();
		if ("Integer".equals(methodSuffix)) {
			methodSuffix = "Int";
		}
		
		String getMethod = "get" + methodSuffix;

		cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL  + ACC_SUPER,  classType, 
				"Ljava/lang/Object;"
				+ "L"  + GETTER_TYPE + "<L" + targetType + ";" + AsmUtils.toTargetTypeDeclaration(propertyType) + ">;"
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
		mv = cw.visitMethod(ACC_PUBLIC, getMethod, "(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + primitiveType , null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);

		AsmUtils.invoke(mv, target, method.getName(), "()" +  primitiveType);
		
		mv.visitInsn(AsmUtils.returnOps.get(primitive));
		mv.visitMaxs(1, 2);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "get", "(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + AsmUtils.toTargetTypeDeclaration(propertyType), null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);
		AsmUtils.invoke(mv, target, method.getName(), "()" + primitiveType);
        mv.visitMethodInsn(INVOKESTATIC, propertyType, "valueOf", "(" +  primitiveType + ")L" + propertyType + ";" , false);
        mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 2);
		mv.visitEnd();
		}
        appendPrimitiveBridges(cw, primitive, targetType, primitiveType, propertyType, classType, getMethod);

        appendToString(cw);
        cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());

	}

    private static void appendPrimitiveBridges(ClassWriter cw, Class<?> primitive, String targetType, String primitiveType, String propertyType, String classType, String getMethod) {
        MethodVisitor mv;
        {
        mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, getMethod, "(Ljava/lang/Object;)" + primitiveType, null, new String[] { "java/lang/Exception" });
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, targetType);
        mv.visitMethodInsn(INVOKEVIRTUAL,  classType , getMethod, "(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + primitiveType, false);
        mv.visitInsn(AsmUtils.returnOps.get(primitive));
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        }
        {
        mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST,  targetType );
        mv.visitMethodInsn(INVOKEVIRTUAL,  classType , "get", "(" + AsmUtils.toTargetTypeDeclaration(targetType) + ")" + AsmUtils.toTargetTypeDeclaration(propertyType), false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        }
    }

    private static void appendToString(ClassWriter cw) {
        MethodVisitor mv;
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

    private static String toType(Class<?> target) {
		String name = target.getName();
		return toType(name);
	}

	private static String toType(String name) {
		return name.replace('.', '/');
	}
}
