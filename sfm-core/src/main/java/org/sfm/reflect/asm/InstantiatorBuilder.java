package org.sfm.reflect.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.sfm.reflect.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import static org.objectweb.asm.Opcodes.*;


public class InstantiatorBuilder {

	public static <S>  byte[] createInstantiator(final String className, final Class<?> sourceClass,
                                                 final ExecutableInstantiatorDefinition instantiatorDefinition, final Map<Parameter, Getter<? super S, ?>> injections) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		Class<?> targetClass= TypeHelper.toClass(getTargetType(instantiatorDefinition));
		
		String targetType = AsmUtils.toType(targetClass);
		String sourceType = AsmUtils.toWrapperType(sourceClass);
        String classType = AsmUtils.toType(className);
        String instantiatorType = AsmUtils.toType(Instantiator.class);

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "Ljava/lang/Object;L" + instantiatorType + "<L"	+ targetType + ";>;", "java/lang/Object",
				new String[] { instantiatorType });

        Parameter[] parameters = instantiatorDefinition.getParameters();

        appendGetters(injections, cw);
        appendInit(injections, cw, sourceType, classType);
        appendNewInstance(sourceClass, instantiatorDefinition, injections, cw, targetType, sourceType, classType, parameters);
        appendBridgeMethod(cw, targetType, sourceType, classType);
        appendToString(injections, cw, parameters);
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}

    public static <S>  byte[] createInstantiator(final String className, final Class<?> sourceClass,
                                                 final Instantiator<Void, ?> builderInstantiator,
                                                 final BuilderInstantiatorDefinition instantiatorDefinition,
                                                 final Map<Parameter, Getter<? super S, ?>> injections) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        Class<?> targetClass= TypeHelper.toClass(getTargetType(instantiatorDefinition));

        String targetType = AsmUtils.toType(targetClass);
        String sourceType = AsmUtils.toWrapperType(sourceClass);
        String classType = AsmUtils.toType(className);
        String instantiatorType = AsmUtils.toType(Instantiator.class);

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "Ljava/lang/Object;L" + instantiatorType + "<L"	+ targetType + ";>;", "java/lang/Object",
                new String[] { instantiatorType });



        {
            FieldVisitor fv =
                    cw.visitField(
                            ACC_FINAL,
                            "builderInstantiator",
                            "Lorg/sfm/reflect/Instantiator;",
                            "Lorg/sfm/reflect/Instantiator<Ljava/lang/Void;L"
                                    + AsmUtils.toType(getTargetType(instantiatorDefinition.getBuilderInstantiator())) +  ";>;", null);
            fv.visitEnd();
        }

        appendGetters(injections, cw);
        appendInitBuilder(injections, cw, sourceType, classType, instantiatorDefinition);
        appendNewInstance(sourceClass, instantiatorDefinition, injections, cw, targetType, sourceType, classType, instantiatorDefinition.getSetters());
        appendBridgeMethod(cw, targetType, sourceType, classType);
        appendToString(injections, cw, instantiatorDefinition.getParameters());
        cw.visitEnd();

        return AsmUtils.writeClassToFile(className, cw.toByteArray());
    }

    private static <S> void appendGetters(Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw) {
        FieldVisitor fv;
        for(Entry<Parameter, Getter<? super S, ?>> entry : injections.entrySet()) {
            fv = cw.visitField(ACC_FINAL, "getter_" + entry.getKey().getName(), "L" + AsmUtils.toType(entry.getValue().getClass()) +";", null, null);
            fv.visitEnd();
        }
    }

    private static <S> void appendInitBuilder(Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, String sourceType, String classType, BuilderInstantiatorDefinition instantiatorDefinition) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(Ljava/util/Map;Lorg/sfm/reflect/Instantiator;)V",
                "(Ljava/util/Map<Ljava.lang.String;L" + AsmUtils.toType(Getter.class)  +"<" + AsmUtils.toDeclaredLType(sourceType) + "*>;>;" +
                        "Lorg/sfm/reflect/Instantiator<Ljava/lang/Void;L" + AsmUtils.toType(getTargetType(instantiatorDefinition.getBuilderInstantiator())) +";>;)V", null);

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitFieldInsn(PUTFIELD, classType, "builderInstantiator", "Lorg/sfm/reflect/Instantiator;");

        for(Entry<Parameter, Getter<? super S, ?>> entry : injections.entrySet()) {
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

    private static <S> void appendInit(Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, String sourceType, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(Ljava/util/Map;)V",
                "(Ljava/util/Map<Ljava.lang.String;L" + AsmUtils.toType(Getter.class)  +"<" + AsmUtils.toDeclaredLType(sourceType) + "*>;>;)V", null);

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);


        for(Entry<Parameter, Getter<? super S, ?>> entry : injections.entrySet()) {
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

    private static <S> void appendNewInstance(Class<?> sourceClass, ExecutableInstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, String targetType, String sourceType, String classType, Parameter[] parameters) throws NoSuchMethodException {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L" + targetType + ";", null, new String[] { "java/lang/Exception" });
        mv.visitCode();
        mv.visitTypeInsn(NEW, targetType);
        mv.visitInsn(DUP);


        StringBuilder sb = new StringBuilder();

        for (Parameter p : parameters) {
            Getter<? super S, ?> getter = injections.get(p);


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
                    String methodSuffix = AsmUtils.wrappers.get(p.getType()).getSimpleName();
                    if ("Integer".equals(methodSuffix)) {
                        methodSuffix = "Int";
                    }
                    String methodName = "get" + methodSuffix;
                    try {
                        getterPublicType.getMethod(methodName);
                        AsmUtils.invoke(mv, getterPublicType, "get" + methodSuffix, "(" + AsmUtils.toDeclaredLType(sourceType) + ")" + propertyType);
                    } catch (NoSuchMethodException e) {
                        if (AsmUtils.isStillGeneric(getter.getClass())) {
                            AsmUtils.invoke(mv, getterPublicType, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
                            mv.visitTypeInsn(CHECKCAST, AsmUtils.toWrapperType(p.getType()));
                        } else {
                            AsmUtils.invoke(mv, getterPublicType, "get", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L" + AsmUtils.toType(getter.getClass().getMethod("get", sourceClass).getReturnType()) + ";");
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
                        AsmUtils.invoke(mv, getterPublicType, "get", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L" + AsmUtils.toType(getter.getClass().getMethod("get", sourceClass).getReturnType()) + ";");
                    }
                }

            }
        }

        Member exec = instantiatorDefinition.getExecutable();
        if (exec instanceof Constructor) {
            mv.visitMethodInsn(INVOKESPECIAL, targetType, "<init>", "(" + sb.toString() + ")V", false);
        } else {
            mv.visitMethodInsn(INVOKESTATIC, AsmUtils.toType(((Method)exec).getDeclaringClass()), exec.getName(),
                    AsmUtils.toSignature((Method)exec)
                    , false);

        }

        mv.visitInsn(ARETURN);
        mv.visitMaxs(3 , 2);
        mv.visitEnd();
    }

    private static <S> void appendNewInstance(Class<?> sourceClass, BuilderInstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, String targetType, String sourceType, String classType, Map<Parameter, Method> setters) throws NoSuchMethodException {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L" + targetType + ";", null, new String[] { "java/lang/Exception" });
        mv.visitCode();


        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD,classType, "builderInstantiator", "Lorg/sfm/reflect/Instantiator;");
        mv.visitInsn(ACONST_NULL);
        mv.visitMethodInsn(INVOKEINTERFACE, "org/sfm/reflect/Instantiator", "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        final Type builderClass = getTargetType(instantiatorDefinition.getBuilderInstantiator());
        final String builderType = AsmUtils.toType(builderClass);
        mv.visitTypeInsn(CHECKCAST, builderType);

        mv.visitVarInsn(ASTORE, 2);

        for (Entry<Parameter, Method> e : setters.entrySet()) {
            mv.visitVarInsn(ALOAD, 2);
            Parameter p = e.getKey();
            Getter<? super S, ?> getter = injections.get(p);

            String propertyType = AsmUtils.toType(p.getType());

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
                    String methodSuffix = AsmUtils.wrappers.get(p.getType()).getSimpleName();
                    if ("Integer".equals(methodSuffix)) {
                        methodSuffix = "Int";
                    }
                    String methodName = "get" + methodSuffix;
                    try {
                        getterPublicType.getMethod(methodName);
                        AsmUtils.invoke(mv, getterPublicType, "get" + methodSuffix, "(" + AsmUtils.toDeclaredLType(sourceType) + ")" + propertyType);
                    } catch (NoSuchMethodException ex) {
                        if (AsmUtils.isStillGeneric(getter.getClass())) {
                            AsmUtils.invoke(mv, getterPublicType, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
                            mv.visitTypeInsn(CHECKCAST, AsmUtils.toWrapperType(p.getType()));
                        } else {
                            AsmUtils.invoke(mv, getterPublicType, "get", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L" + AsmUtils.toType(getter.getClass().getMethod("get", sourceClass).getReturnType()) + ";");
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
                        AsmUtils.invoke(mv, getterPublicType, "get", "(" + AsmUtils.toDeclaredLType(sourceType) + ")L" + AsmUtils.toType(getter.getClass().getMethod("get", sourceClass).getReturnType()) + ";");
                    }
                }

                AsmUtils.invoke(mv, TypeHelper.toClass(builderClass), e.getValue().getName(),
                        AsmUtils.toSignature(e.getValue()));

                if (!Void.TYPE.equals(e.getValue().getReturnType())) {
                    mv.visitVarInsn(ASTORE, 2);
                }
            }
        }
        mv.visitVarInsn(ALOAD, 2);
        AsmUtils.invoke(mv, TypeHelper.toClass(builderClass),
                instantiatorDefinition.getBuildMethod().getName(),
                AsmUtils.toSignature(instantiatorDefinition.getBuildMethod()));

        mv.visitInsn(ARETURN);
        mv.visitMaxs(3 , 2);
        mv.visitEnd();
    }

    private static void appendBridgeMethod(ClassWriter cw, String targetType, String sourceType, String classType) {
        MethodVisitor mv;
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

    private static <S> void appendToString(Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, Parameter[] parameters) {
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

        for(int i = 0; i < parameters.length; i++) {
            String paramName =  (i > 0 ? ", " : "") + "parameter" + i + "=";
            String parameter = String.valueOf(parameters[i]);


            mv.visitLdcInsn(paramName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            mv.visitLdcInsn(parameter);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            Getter<? super S, ?> getter = injections.get(parameters[i]);

            String getterName =  ", parameter" + i + "=";
            String getterString = String.valueOf(getter);

            mv.visitLdcInsn(getterName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            mv.visitLdcInsn(getterString);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        }
        mv.visitLdcInsn("}");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);


        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private static Type getTargetType(InstantiatorDefinition instantiatorDefinition) {

        switch (instantiatorDefinition.getType()) {
            case METHOD:
                return ((Method)((ExecutableInstantiatorDefinition)instantiatorDefinition).getExecutable()).getGenericReturnType();
            case CONSTRUCTOR:
                return ((Constructor)((ExecutableInstantiatorDefinition)instantiatorDefinition).getExecutable()).getDeclaringClass();
            case BUILDER:
                return ((BuilderInstantiatorDefinition)instantiatorDefinition).getBuildMethod().getGenericReturnType();
            default:
                throw new IllegalArgumentException("Unsupported type " + instantiatorDefinition.getType());
        }
    }
}
