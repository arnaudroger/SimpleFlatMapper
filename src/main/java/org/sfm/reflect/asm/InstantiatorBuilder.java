package org.sfm.reflect.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.sfm.reflect.*;

import java.util.Map;
import java.util.Map.Entry;

import static org.objectweb.asm.Opcodes.*;


public class InstantiatorBuilder {

	public static <S,T>  byte[] createInstantiator(final String className, final Class<?> sourceClass,
			 final ConstructorDefinition<T> constructorDefinition,final Map<ConstructorParameter, Getter<S, ?>> injections) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MethodVisitor mv;
		FieldVisitor fv;

		Class<? extends T> targetClass= constructorDefinition.getConstructor().getDeclaringClass();
		
		String targetType = AsmUtils.toType(targetClass);
		String sourceType = AsmUtils.toType(sourceClass);
		String classType = AsmUtils.toType(className);
		String instantiatorType = AsmUtils.toType(Instantiator.class);

		cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "Ljava/lang/Object;L" + instantiatorType + "<L"	+ targetType + ";>;", "java/lang/Object",
				new String[] { instantiatorType });
		
		
		for(Entry<ConstructorParameter, Getter<S, ?>> entry : injections.entrySet()) {
			fv = cw.visitField(ACC_FINAL, "getter_" + entry.getKey().getName(), "L" + AsmUtils.toType(entry.getValue().getClass()) +";", null, null);
			fv.visitEnd();
		}

		{
			
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/Map;)V", "(Ljava/util/Map<Ljava.lang.String;Lorg/sfm/reflect/Getter<" + AsmUtils.toDeclaredLType(sourceType) + "*>;>;)V", null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			
			
			
			for(Entry<ConstructorParameter, Getter<S, ?>> entry : injections.entrySet()) {
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
			mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L" + targetType + ";", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitTypeInsn(NEW, targetType);
			mv.visitInsn(DUP);
			
			
			ConstructorParameter[] parameters = constructorDefinition.getParameters();
			StringBuilder sb = new StringBuilder();
			
 			for(int i = 0; i < parameters.length; i++) {
 				ConstructorParameter p = parameters[i];
 				Getter<S, ?> getter = injections.get(p);
				
 				
 				String propertyType = AsmUtils.toType(p.getType());
				
 				if (TypeHelper.isPrimitive(p.getType())) {
					sb.append(propertyType);
				} else if (TypeHelper.isArray(p.getType())) {
					sb.append(propertyType);
				} else {
					sb.append("L").append(propertyType).append(";");
				}
				
 				if (getter == null) {
 					if (TypeHelper.isPrimitive(p.getType())) {
 						mv.visitInsn(AsmUtils.defaultValue.get(p.getType()));
 					} else {
 						mv.visitInsn(ACONST_NULL);
 					}
 				} else {
 					
 					Class<?> getterPublicType = AsmUtils.getPublicOrInterfaceClass(getter.getClass());
 					String getterType = AsmUtils.toType(getterPublicType);
 					
 					mv.visitVarInsn(ALOAD, 0);
 					mv.visitFieldInsn(GETFIELD, classType, "getter_" + p.getName(), "L" + getterType + ";");
 					mv.visitVarInsn(ALOAD, 1);
 					
					if (TypeHelper.isPrimitive(p.getType())) {
 						String methodSuffix =  AsmUtils.wrappers.get(p.getType()).getSimpleName();
 						if ("Integer".equals(methodSuffix)) {
 							methodSuffix = "Int";
 						}
 						String methodName = "get" + methodSuffix;
 						try {
 							getterPublicType.getMethod(methodName);
 							AsmUtils.invoke(mv, getterPublicType, "get" + methodSuffix, "(" + AsmUtils.toDeclaredLType(sourceType) + ")" +propertyType);
 						} catch(NoSuchMethodException e){
 	 						if (AsmUtils.isStillGeneric(getter.getClass())) {
 	 	 						AsmUtils.invoke(mv, getterPublicType, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
 	 							mv.visitTypeInsn(CHECKCAST,  AsmUtils.toWrapperType(p.getType()));
 	 						} else {
 	 	 						AsmUtils.invoke(mv, getterPublicType, "get", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L"+ AsmUtils.toType(getter.getClass().getMethod("get", sourceClass).getReturnType()) + ";");
 	 						}
 	 						//mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
 	 						
 	 						String valueMethodPrefix = methodSuffix.toLowerCase();
 	 						if ("character".equals(valueMethodPrefix)) {
 	 							valueMethodPrefix = "char";
 	 						}
 	 						String valueMethod = valueMethodPrefix + "Value";
 	 						//mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
 	 						AsmUtils.invoke(mv, AsmUtils.toWrapperClass(p.getType()), valueMethod, "()" + AsmUtils.toType(p.getType()));

 						}
 					} else {
 						if (AsmUtils.isStillGeneric(getter.getClass())) {
 	 						AsmUtils.invoke(mv, getterPublicType, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
 							mv.visitTypeInsn(CHECKCAST, propertyType);
 						} else {
 	 						AsmUtils.invoke(mv, getterPublicType, "get", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L"+ AsmUtils.toType(getter.getClass().getMethod("get", sourceClass).getReturnType()) + ";");
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
			mv.visitMethodInsn(INVOKEVIRTUAL, classType, "newInstance", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L"
					+ targetType + ";", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}
}
