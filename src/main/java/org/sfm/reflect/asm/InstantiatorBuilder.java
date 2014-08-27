package org.sfm.reflect.asm;

import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import java.sql.ResultSet;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.sfm.reflect.Getter;



public class InstantiatorBuilder {

	public static <S,T>  byte[] createInstantiator(final String className, final Class<S> sourceClass,
			 final ConstructorDefinition<T> constructorDefinition,final Map<Parameter, Getter<S, ?>> injections) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MethodVisitor mv;
		FieldVisitor fv;

		Class<T> targetClass= constructorDefinition.getConstructor().getDeclaringClass();
		
		String targetType = AsmUtils.toType(targetClass);
		String sourceType = AsmUtils.toType(sourceClass);
		String classType = AsmUtils.toType(className);

		cw.visit(V1_7, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType,
				"Ljava/lang/Object;Lorg/sfm/reflect/Instantiator<L"
						+ targetType + ";>;", "java/lang/Object",
				new String[] { "org/sfm/reflect/Instantiator" });
		
		
		for(Entry<Parameter, Getter<S, ?>> entry : injections.entrySet()) {
			fv = cw.visitField(ACC_FINAL, "getter_" + entry.getKey().getName(), "L" + AsmUtils.toType(entry.getValue().getClass()) +";", null, null);
			fv.visitEnd();
		}

		{
			
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/Map;)V", "(Ljava/util/Map<Ljava.lang.String;Lorg/sfm/reflect/Getter<Ljava/sql/ResultSet;*>;>;)V", null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			
			
			
			for(Entry<Parameter, Getter<S, ?>> entry : injections.entrySet()) {
				String name = entry.getKey().getName();
				String getterType = AsmUtils.toType(entry.getValue().getClass());
				
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitLdcInsn(name);
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
				mv.visitTypeInsn(CHECKCAST, getterType);
				mv.visitFieldInsn(PUTFIELD, classType, "getter_" + name, "L" + getterType +";");

			}

			mv.visitInsn(RETURN);
			
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		
		{
			mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(L" + sourceType + ";)L" + targetType + ";", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitTypeInsn(NEW, targetType);
			mv.visitInsn(DUP);
			
			
			Parameter[] parameters = constructorDefinition.getParameters();
			StringBuilder sb = new StringBuilder();
			
 			for(int i = 0; i < parameters.length; i++) {
 				Parameter p = parameters[i];
 				Getter<S, ?> getter = injections.get(p);
				
 				
 				String propertyType = AsmUtils.toType(p.getType());
				
 				if (p.getType().isPrimitive()) {
					sb.append(propertyType);
				} else {
					sb.append("L").append(propertyType).append(";");
				}
				
 				if (getter == null) {
 					if (p.getType().isPrimitive()) {
 						mv.visitInsn(AsmUtils.defaultValue.get(p.getType()));
 					} else {
 						mv.visitInsn(ACONST_NULL);
 					}
 				} else {
 					String getterType = AsmUtils.toType(getter.getClass());
 					
 					mv.visitVarInsn(ALOAD, 0);
 					mv.visitFieldInsn(GETFIELD, classType, "getter_" + p.getName(), "L" + getterType + ";");
 					mv.visitVarInsn(ALOAD, 1);
 					
					if (p.getType().isPrimitive()) {
 						String methodSuffix =  AsmUtils.wrappers.get(p.getType()).getSimpleName();
 						if ("Integer".equals(methodSuffix)) {
 							methodSuffix = "Int";
 						}
 						mv.visitMethodInsn(INVOKEVIRTUAL, getterType, "get" + methodSuffix, "(Ljava/sql/ResultSet;)" +propertyType , false);
 					} else {
 						if (AsmUtils.isStillGeneric(getter.getClass())) {
 							mv.visitMethodInsn(INVOKEVIRTUAL, getterType, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
 							mv.visitTypeInsn(CHECKCAST, propertyType);
 						} else {
 							mv.visitMethodInsn(INVOKEVIRTUAL, getterType, "get", "(Ljava/sql/ResultSet;)L"+ AsmUtils.toType(getter.getClass().getMethod("get", ResultSet.class).getReturnType()) + ";", false);
 						}
 					}
 					
 				}
 			}
			mv.visitMethodInsn(INVOKESPECIAL, targetType, "<init>", "(" + sb.toString() + ")V", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3 , 2);
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
