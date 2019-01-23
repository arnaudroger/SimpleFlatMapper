package org.simpleflatmapper.reflect.asm;

import org.simpleflatmapper.ow2asm.ClassWriter;
import org.simpleflatmapper.ow2asm.FieldVisitor;
import org.simpleflatmapper.ow2asm.Label;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.ow2asm.Opcodes;
import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import static org.simpleflatmapper.ow2asm.Opcodes.ACC_BRIDGE;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_FINAL;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_PUBLIC;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_SUPER;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_SYNTHETIC;
import static org.simpleflatmapper.ow2asm.Opcodes.ACONST_NULL;
import static org.simpleflatmapper.ow2asm.Opcodes.ALOAD;
import static org.simpleflatmapper.ow2asm.Opcodes.ARETURN;
import static org.simpleflatmapper.ow2asm.Opcodes.ASTORE;
import static org.simpleflatmapper.ow2asm.Opcodes.CHECKCAST;
import static org.simpleflatmapper.ow2asm.Opcodes.DUP;
import static org.simpleflatmapper.ow2asm.Opcodes.GETFIELD;
import static org.simpleflatmapper.ow2asm.Opcodes.IFNULL;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKEINTERFACE;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKESPECIAL;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKESTATIC;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKEVIRTUAL;
import static org.simpleflatmapper.ow2asm.Opcodes.NEW;
import static org.simpleflatmapper.ow2asm.Opcodes.PUTFIELD;
import static org.simpleflatmapper.ow2asm.Opcodes.RETURN;
import static org.simpleflatmapper.ow2asm.Opcodes.V1_6;


public class InstantiatorBuilder {

	public static <S>  byte[] createInstantiator(final String className, final Class<?> sourceClass,
                                                 final ExecutableInstantiatorDefinition instantiatorDefinition, final Map<Parameter, Getter<? super S, ?>> injections) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		Class<?> targetClass= TypeHelper.toClass(BiInstantiatorBuilder.getTargetType(instantiatorDefinition));
		
		String targetType = AsmUtils.toAsmType(targetClass);
		String sourceType = AsmUtils.toWrapperType(sourceClass);
        String classType = AsmUtils.toAsmType(className);
        String instantiatorType = AsmUtils.toAsmType(Instantiator.class);

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "Ljava/lang/Object;L" + instantiatorType + "<L"	+ targetType + ";>;", "java/lang/Object",
				new String[] { instantiatorType });

        Parameter[] parameters = instantiatorDefinition.getParameters();

        appendGetters(injections, cw);
        appendInit(injections, cw, sourceType, classType);
        appendNewInstanceBuilderOnMethod(sourceClass, instantiatorDefinition, injections, cw, targetType, sourceType, classType, parameters);
        appendBridgeMethod(cw, targetType, sourceType, classType);
        appendToString(injections, cw, parameters);
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}

    public static <S>  byte[] createInstantiator(final String className, final Class<?> sourceClass,
                                                 final BuilderInstantiatorDefinition instantiatorDefinition,
                                                 final Map<Parameter, Getter<? super S, ?>> injections, boolean ignoreNullValues) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        Class<?> targetClass= TypeHelper.toClass(BiInstantiatorBuilder.getTargetType(instantiatorDefinition));

        String targetType = AsmUtils.toAsmType(targetClass);
        String sourceType = AsmUtils.toWrapperType(sourceClass);
        String classType = AsmUtils.toAsmType(className);
        String instantiatorType = AsmUtils.toAsmType(Instantiator.class);

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "Ljava/lang/Object;L" + instantiatorType + "<L"	+ targetType + ";>;", "java/lang/Object",
                new String[] { instantiatorType });

        {
            FieldVisitor fv =
                    cw.visitField(
                            ACC_FINAL,
                            "builderInstantiator",
                            "L" + AsmUtils.toAsmType(Instantiator.class) + ";",
                            "L" + AsmUtils.toAsmType(Instantiator.class) + "<Ljava/lang/Void;L"
                                    + AsmUtils.toAsmType(BiInstantiatorBuilder.getTargetType(instantiatorDefinition.getBuilderInstantiator())) +  ";>;", null);
            fv.visitEnd();
        }

        appendGetters(injections, cw);
        appendInitBuilder(injections, cw, sourceType, classType, instantiatorDefinition);
        appendNewInstanceBuilderOnBuilder(sourceClass, instantiatorDefinition, injections, cw, targetType, sourceType, classType, instantiatorDefinition.getSetters(), ignoreNullValues);
        appendBridgeMethod(cw, targetType, sourceType, classType);
        appendToString(injections, cw, instantiatorDefinition.getParameters());
        cw.visitEnd();

        return AsmUtils.writeClassToFile(className, cw.toByteArray());
    }

    private static <S> void appendGetters(Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw) {
        FieldVisitor fv;
        for(Entry<Parameter, Getter<? super S, ?>> entry : injections.entrySet()) {
            GetterCall getterCall = getGetterCall(entry.getKey().getType(), entry.getValue().getClass());

            fv = cw.visitField(ACC_FINAL, "getter_" + entry.getKey().getName(), AsmUtils.toTargetTypeDeclaration(getterCall.getterType), null, null);
            fv.visitEnd();
        }
    }

    private static <S> void appendInitBuilder(Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, String sourceType, String classType, BuilderInstantiatorDefinition instantiatorDefinition) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(Ljava/util/Map;L" + AsmUtils.toAsmType(Instantiator.class) + ";)V",
                "(Ljava/util/Map<Ljava.lang.String;L" + AsmUtils.toAsmType(Getter.class)  +"<" + AsmUtils.toTargetTypeDeclaration(sourceType) + "*>;>;" +
                        "L" + AsmUtils.toAsmType(Instantiator.class) + "<Ljava/lang/Void;L" + AsmUtils.toAsmType(BiInstantiatorBuilder.getTargetType(instantiatorDefinition.getBuilderInstantiator())) +";>;)V", null);

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitFieldInsn(PUTFIELD, classType, "builderInstantiator", "L" + AsmUtils.toAsmType(Instantiator.class) + ";");

        appendInitGetters(injections, classType, mv);

        mv.visitInsn(RETURN);

        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private static <S> void appendInitGetters(Map<Parameter, Getter<? super S, ?>> injections, String classType, MethodVisitor mv) {
        for(Entry<Parameter, Getter<? super S, ?>> entry : injections.entrySet()) {
            String name = entry.getKey().getName();
            GetterCall getterCall = getGetterCall(entry.getKey().getType(), entry.getValue().getClass());

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(getterCall.getterType));
            mv.visitFieldInsn(PUTFIELD, classType, "getter_" + name, AsmUtils.toTargetTypeDeclaration(getterCall.getterType));
        }
    }

    private static <S> void appendInit(Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, String sourceType, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(Ljava/util/Map;)V",
                "(Ljava/util/Map<Ljava.lang.String;L" + AsmUtils.toAsmType(Getter.class)  +"<" + AsmUtils.toTargetTypeDeclaration(sourceType) + "*>;>;)V", null);

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        appendInitGetters(injections, classType, mv);

        mv.visitInsn(RETURN);

        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private static <S> void appendNewInstanceBuilderOnMethod(Class<?> sourceClass, ExecutableInstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, String targetType, String sourceType, String classType, Parameter[] parameters) throws NoSuchMethodException {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" + AsmUtils.toTargetTypeDeclaration(sourceType) + ")" + AsmUtils.toTargetTypeDeclaration(targetType), null, new String[] { "java/lang/Exception" });
        mv.visitCode();
        
        if (!Modifier.isStatic(instantiatorDefinition.getExecutable().getModifiers())) {
            mv.visitTypeInsn(NEW, targetType);
            mv.visitInsn(DUP);
        }


        StringBuilder sb = new StringBuilder();

        for (Parameter p : parameters) {
            Getter<? super S, ?> getter = injections.get(p);

            sb.append(AsmUtils.toTargetTypeDeclaration(p.getType()));

            if (getter == null) {
                if (TypeHelper.isPrimitive(p.getType())) {
                    mv.visitInsn(AsmUtils.defaultValue.get(p.getType()));
                } else {
                    mv.visitInsn(ACONST_NULL);
                }
            } else {
                invokeGetter(p, getter, classType, sourceClass, mv, null, false);
            }
        }

        Member exec = instantiatorDefinition.getExecutable();
        if (exec instanceof Constructor) {
            mv.visitMethodInsn(INVOKESPECIAL, targetType, "<init>", "(" + sb.toString() + ")V", false);
        } else {
            mv.visitMethodInsn(INVOKESTATIC, AsmUtils.toAsmType(((Method)exec).getDeclaringClass()), exec.getName(),
                    AsmUtils.toSignature((Method)exec)
                    , false);

        }

        mv.visitInsn(ARETURN);
        mv.visitMaxs(3 , 2);
        mv.visitEnd();
    }

    private static <S> void invokeGetter(Parameter p, Getter<? super S, ?> getter, String classType, Class<?> sourceClass, MethodVisitor mv, Consumer<MethodVisitor> consumer, boolean ignoreNullValues) throws NoSuchMethodException {
        GetterCall getterCall = getGetterCall(p.getType(), getter.getClass());

        String getterType = AsmUtils.toAsmType(getterCall.getterType);
        String sourceType = AsmUtils.toAsmType(sourceClass.equals(Void.class) || sourceClass.equals(void.class) ? Object.class : sourceClass);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classType, "getter_" + p.getName(), AsmUtils.toTargetTypeDeclaration(getterType));
        mv.visitVarInsn(ALOAD, 1);

        Type paramType = TypeHelper.getGenericParameterForClass(getterCall.getterType, Getter.class)[0];
        if (getterCall.isPrimitive) {
            AsmUtils.invoke(mv, getterCall.getterType, getterCall.methodName, "(" + AsmUtils.toTargetTypeDeclaration(paramType) + ")" + AsmUtils.toAsmType(p.getType()));
            if (consumer != null) {
                consumer.accept(mv);
            }
        } else {
            Class<?> wrapperClass = AsmUtils.toWrapperClass(p.getType());
            Method getterMethod = BiInstantiatorBuilder.getMethod(TypeHelper.toClass(getterCall.getterType), "get", 1);

            
            AsmUtils.invoke(mv, getterMethod);
            if (!wrapperClass.isAssignableFrom(getterMethod.getReturnType())) {
                mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(wrapperClass));
            }


            Label label = new Label();
            if (ignoreNullValues) {
                mv.visitVarInsn(ASTORE, 3);

                mv.visitVarInsn(ALOAD, 3);
                mv.visitJumpInsn(IFNULL, label);
            }
            
            changeToPrimitiveIfNeeded(p, mv, wrapperClass, ignoreNullValues);
            if (consumer != null) {
                consumer.accept(mv);
            }

            if (ignoreNullValues) {
                mv.visitLabel(label);
            }
        }
    }

    private static void changeToPrimitiveIfNeeded(Parameter p, MethodVisitor mv, Class<?> wrapperClass, boolean ignoreNullValues) {

        if (TypeHelper.isPrimitive(p.getType())) {
            if (ignoreNullValues) {
                mv.visitVarInsn(ALOAD, 3);
            }
            String methodSuffix = getPrimitiveMethodSuffix(p);
            String valueMethodPrefix = methodSuffix.toLowerCase();
            if ("character".equals(valueMethodPrefix)) {
                valueMethodPrefix = "char";
            }
            String valueMethod = valueMethodPrefix + "Value";
            AsmUtils.invoke(mv, wrapperClass, valueMethod, "()" + AsmUtils.toAsmType(p.getType()));
            if (ignoreNullValues) {
                mv.visitVarInsn(AsmUtils.getStoreOps(p.getType()), 3);
            }
        }
    }

    private static String getPrimitiveMethodSuffix(Parameter p) {
        return getPrimitiveMethodSuffix(p.getType());
    }

    private static String getPrimitiveMethodSuffix(Class<?> type) {
        String methodSuffix = AsmUtils.wrappers.get(type).getSimpleName();
        if ("Integer".equals(methodSuffix)) {
            methodSuffix = "Int";
        }
        return methodSuffix;
    }

    private static <S> void appendNewInstanceBuilderOnBuilder(Class<?> sourceClass, BuilderInstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<? super S, ?>> injections, ClassWriter cw, String targetType, String sourceType, String classType, Map<Parameter, Method> setters, boolean ignoreNullValues) throws NoSuchMethodException {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" + AsmUtils.toTargetTypeDeclaration(sourceType) + ")" + AsmUtils.toTargetTypeDeclaration(targetType), null, new String[] { "java/lang/Exception" });
        mv.visitCode();


        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD,classType, "builderInstantiator", AsmUtils.toTargetTypeDeclaration(Instantiator.class));
        mv.visitInsn(ACONST_NULL);
        mv.visitMethodInsn(INVOKEINTERFACE, AsmUtils.toAsmType(Instantiator.class), "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        final Type builderClass = BiInstantiatorBuilder.getTargetType(instantiatorDefinition.getBuilderInstantiator());
        final String builderType = AsmUtils.toAsmType(builderClass);
        mv.visitTypeInsn(CHECKCAST, builderType);

        mv.visitVarInsn(ASTORE, 2);

        boolean first = true;
        for (final Entry<Parameter, Method> e : setters.entrySet()) {
            Parameter p = e.getKey();
            Getter<? super S, ?> getter = injections.get(p);

            if (getter != null) {

                final Class<?> parameterType = p.getType();
                final boolean checkIfNull = ignoreNullValues && !getGetterCall(parameterType, getter.getClass()).isPrimitive;
                
                mv.visitVarInsn(ALOAD, 2);
                
                invokeGetter(p, getter, classType, sourceClass, mv, new Consumer<MethodVisitor>() {
                    @Override
                    public void accept(MethodVisitor mv) {
                        if (checkIfNull) {
                            mv.visitVarInsn(ALOAD, 2);
                            mv.visitVarInsn(AsmUtils.getLoadOps(parameterType), 3);
                        }
                        AsmUtils.invoke(mv, TypeHelper.toClass(builderClass), e.getValue().getName(),
                                AsmUtils.toSignature(e.getValue()));
                        if (!Void.TYPE.equals(e.getValue().getReturnType())) {
                            mv.visitVarInsn(ASTORE, 2);
                        }
                    }
                }, ignoreNullValues);


                    
                if (checkIfNull) {
                    if (first && !Void.TYPE.equals(e.getValue().getReturnType())) {
                        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{builderType}, 0, null);
                    } else {
                        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    }
                    first = false;
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

    private static GetterCall getGetterCall(Class<?> propertyType, Class<? extends Getter> getterClass) {
        if (TypeHelper.isPrimitive(propertyType)) {
            Class<?> primitiveGetter = BiInstantiatorBuilder.getPrimitiveGetter(propertyType);
            if (primitiveGetter == null) {
                throw new IllegalStateException("No primitive getter for primitive " + propertyType);
            }
            Type publicGetterClass = AsmUtils.findClosestPublicTypeExposing(getterClass, primitiveGetter);
            if (publicGetterClass != null) {
                return new GetterCall("get" + getPrimitiveMethodSuffix(propertyType), publicGetterClass, true);
            }
        }

        Type publicGetterClass = AsmUtils.findClosestPublicTypeExposing(getterClass, Getter.class);
        return new GetterCall("get", publicGetterClass, false);
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
        mv.visitMethodInsn(INVOKEVIRTUAL, classType, "newInstance", "(" + AsmUtils.toTargetTypeDeclaration(sourceType) + ")"
                + AsmUtils.toTargetTypeDeclaration(targetType), false);
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

    static class GetterCall {
        final String methodName;
        final Type getterType;
        final boolean isPrimitive;

        GetterCall(String methodName, Type getterType, boolean isPrimitive) {
            this.methodName = methodName;
            this.getterType = getterType;
            this.isPrimitive = isPrimitive;
        }
    }
}
