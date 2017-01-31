package org.simpleflatmapper.reflect.asm;

import org.simpleflatmapper.ow2asm.ClassWriter;
import org.simpleflatmapper.ow2asm.FieldVisitor;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.util.BiFactory;
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
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKEINTERFACE;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKESPECIAL;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKESTATIC;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKEVIRTUAL;
import static org.simpleflatmapper.ow2asm.Opcodes.NEW;
import static org.simpleflatmapper.ow2asm.Opcodes.PUTFIELD;
import static org.simpleflatmapper.ow2asm.Opcodes.RETURN;
import static org.simpleflatmapper.ow2asm.Opcodes.V1_6;


public class BiInstantiatorBuilder {

    public static final Class<BiInstantiator> BI_INSTANTIATOR_CLASS = BiInstantiator.class;
    public static final Class<Instantiator> INSTANTIATOR_CLASS = Instantiator.class;

    public static <S1, S2>  byte[] createInstantiator(final String className, final Class<?> s1, final Class<?> s2,
                                                      final ExecutableInstantiatorDefinition instantiatorDefinition, final Map<Parameter, BiFactory<? super S1, ? super S2, ?>> injections) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		Class<?> targetClass= TypeHelper.toClass(getTargetType(instantiatorDefinition));
		
		String targetType = AsmUtils.toAsmType(targetClass);
		String s1Type = AsmUtils.toWrapperType(s1);
        String s2Type = AsmUtils.toWrapperType(s2);
        String classType = AsmUtils.toAsmType(className);
        String instantiatorType = AsmUtils.toAsmType(BI_INSTANTIATOR_CLASS);

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "Ljava/lang/Object;L" + instantiatorType + "<L"	+ targetType + ";>;", "java/lang/Object",
				new String[] { instantiatorType });

        Parameter[] parameters = instantiatorDefinition.getParameters();

        appendFactory(injections, cw);
        appendInit(injections, cw, s1Type, s2Type, classType);
        appendNewInstance(s1, s2, instantiatorDefinition, injections, cw, targetType, s1Type, s2Type, classType, parameters);
        appendBridgeMethod(cw, targetType, s1Type, s2Type, classType);
        appendToString(injections, cw, parameters);
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}

    public static <S1, S2>  byte[] createInstantiator(final String className, final Class<?> s1, final Class<?> s2,
                                                 final Instantiator<Void, ?> builderInstantiator,
                                                 final BuilderInstantiatorDefinition instantiatorDefinition,
                                                 final Map<Parameter, BiFactory<? super S1, ? super S2, ?>> injections) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        Class<?> targetClass= TypeHelper.toClass(getTargetType(instantiatorDefinition));

        String targetType = AsmUtils.toAsmType(targetClass);
        String s1Type = AsmUtils.toWrapperType(s1);
        String s2Type = AsmUtils.toWrapperType(s2);
        String classType = AsmUtils.toAsmType(className);
        String instantiatorType = AsmUtils.toAsmType(BI_INSTANTIATOR_CLASS);

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "Ljava/lang/Object;L" + instantiatorType + "<L"	+ targetType + ";>;", "java/lang/Object",
                new String[] { instantiatorType });



        {
            FieldVisitor fv =
                    cw.visitField(
                            ACC_FINAL,
                            "builderInstantiator",
                            "L" + AsmUtils.toAsmType(INSTANTIATOR_CLASS) + ";",
                            "L" + AsmUtils.toAsmType(INSTANTIATOR_CLASS) + "<Ljava/lang/Void;L"
                                    + AsmUtils.toAsmType(getTargetType(instantiatorDefinition.getBuilderInstantiator())) +  ";>;", null);
            fv.visitEnd();
        }

        appendFactory(injections, cw);
        appendInitBuilder(injections, cw, s1Type, s2Type, classType, instantiatorDefinition);
        appendNewInstance2(s1, s2, instantiatorDefinition, injections, cw, targetType, s1Type, s2Type, classType, instantiatorDefinition.getSetters());
        appendBridgeMethod(cw, targetType, s1Type, s2Type, classType);
        appendToString(injections, cw, instantiatorDefinition.getParameters());
        cw.visitEnd();

        return AsmUtils.writeClassToFile(className, cw.toByteArray());
    }

    private static <S1, S2> void appendFactory(Map<Parameter, BiFactory<? super S1, ? super S2, ?>> injections, ClassWriter cw) {
        FieldVisitor fv;
        for(Entry<Parameter, BiFactory<? super S1, ? super S2, ?>> entry : injections.entrySet()) {
            fv = cw.visitField(ACC_FINAL, "factory_" + entry.getKey().getName(), AsmUtils.toTargetTypeDeclaration(entry.getValue().getClass()), null, null);
            fv.visitEnd();
        }
    }

    private static <S1, S2> void appendInitBuilder(Map<Parameter, BiFactory<? super S1, ? super S2, ?>> injections, ClassWriter cw, String s1, String s2, String classType, BuilderInstantiatorDefinition instantiatorDefinition) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(Ljava/util/Map;L" + AsmUtils.toAsmType(INSTANTIATOR_CLASS) + ";)V",
                "(Ljava/util/Map<Ljava.lang.String;L" + AsmUtils.toAsmType(BiFactory.class)  +"<" + AsmUtils.toTargetTypeDeclaration(s1) + "*" + AsmUtils.toTargetTypeDeclaration(s2) + "*>;>;" +
                        "L" + AsmUtils.toAsmType(INSTANTIATOR_CLASS) + "<Ljava/lang/Void;L" + AsmUtils.toAsmType(getTargetType(instantiatorDefinition.getBuilderInstantiator())) +";>;)V", null);

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitFieldInsn(PUTFIELD, classType, "builderInstantiator", "L" + AsmUtils.toAsmType(INSTANTIATOR_CLASS) + ";");

        for(Entry<Parameter, BiFactory<? super S1, ? super S2, ?>> entry : injections.entrySet()) {
            String name = entry.getKey().getName();

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(entry.getValue().getClass()));
            mv.visitFieldInsn(PUTFIELD, classType, "factory_" + name, AsmUtils.toTargetTypeDeclaration(entry.getValue().getClass()));
        }

        mv.visitInsn(RETURN);

        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private static <S1, S2> void appendInit(Map<Parameter, BiFactory<? super S1, ? super S2, ?>> injections, ClassWriter cw, String s1, String s2, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(Ljava/util/Map;)V",
                "(Ljava/util/Map<Ljava.lang.String;L" + AsmUtils.toAsmType(BiFactory.class)  +"<" + AsmUtils.toTargetTypeDeclaration(s1) + "*" + AsmUtils.toTargetTypeDeclaration(s2) + "*>;>;)V", null);

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);


        for(Entry<Parameter, BiFactory<? super S1, ? super S2, ?>> entry : injections.entrySet()) {
            String name = entry.getKey().getName();


            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(name);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(entry.getValue().getClass()));
            mv.visitFieldInsn(PUTFIELD, classType, "factory_" + name, AsmUtils.toTargetTypeDeclaration(entry.getValue().getClass()));

        }

        mv.visitInsn(RETURN);

        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private static <S1, S2> void appendNewInstance(Class<?> s1, Class<?> s2, ExecutableInstantiatorDefinition instantiatorDefinition, Map<Parameter, BiFactory<? super S1, ? super S2, ?>> injections, ClassWriter cw, String targetType, String s1Type, String s2Type, String classType, Parameter[] parameters) throws NoSuchMethodException {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" + AsmUtils.toTargetTypeDeclaration(s1Type) + AsmUtils.toTargetTypeDeclaration(s2Type) +")" + AsmUtils.toTargetTypeDeclaration(targetType), null, new String[] { "java/lang/Exception" });
        mv.visitCode();
        mv.visitTypeInsn(NEW, targetType);
        mv.visitInsn(DUP);


        StringBuilder sb = new StringBuilder();

        for (Parameter p : parameters) {
            BiFactory<? super S1, ? super S2, ?> factory = injections.get(p);

            sb.append(AsmUtils.toTargetTypeDeclaration(p.getType()));

            if (factory == null) {
                if (TypeHelper.isPrimitive(p.getType())) {
                    mv.visitInsn(AsmUtils.defaultValue.get(p.getType()));
                } else {
                    mv.visitInsn(ACONST_NULL);
                }
            } else {
                invovkeFactory(p, factory, classType, s1, s2, mv);
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

    private static <S1, S2> void invovkeFactory(Parameter p, BiFactory<S1, S2, ?> factory, String classType, Class<?> s1, Class<?> s2, MethodVisitor mv) throws NoSuchMethodException {

        String getterType = AsmUtils.toAsmType(factory.getClass());
        String s1Type = AsmUtils.toAsmType(s1.equals(Void.class) || s1.equals(void.class) ? Object.class : s1);
        String s2Type = AsmUtils.toAsmType(s2.equals(Void.class) || s2.equals(void.class) ? Object.class : s2);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classType, "factory_" + p.getName(), AsmUtils.toTargetTypeDeclaration(getterType));
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);

        Class<?> wrapperClass = AsmUtils.toWrapperClass(p.getType());
        Method getterMethod = getNewInstanceMethod(factory.getClass());

        AsmUtils.invoke(mv, factory.getClass(), getterMethod);
        if (!wrapperClass.isAssignableFrom(getterMethod.getReturnType())) {
            mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(wrapperClass));
        }

        if (TypeHelper.isPrimitive(p.getType())) {
            String methodSuffix = getPrimitiveMethodSuffix(p);
            String valueMethodPrefix = methodSuffix.toLowerCase();
            if ("character".equals(valueMethodPrefix)) {
                valueMethodPrefix = "char";
            }
            String valueMethod = valueMethodPrefix + "Value";
            AsmUtils.invoke(mv, wrapperClass, valueMethod, "()" + AsmUtils.toAsmType(p.getType()));
        }
    }

    private static Method getNewInstanceMethod(Class<? extends BiFactory> aClass) {
        Method m = null;
        for(Method p : aClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(p.getModifiers())
                    && p.getName().equals("newInstance")
                    && (p.getParameterTypes() != null && p.getParameterTypes().length == 2)) {
                // crude way of selecting non bridge method
                if (m == null || p.getModifiers() < m.getModifiers()) {
                    m = p;
                }
            }
        }
        return m;
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

    private static <S1, S2> void appendNewInstance2(Class<?> s1, Class<?> s2, BuilderInstantiatorDefinition instantiatorDefinition, Map<Parameter, BiFactory<? super S1, ? super S2, ?>> injections, ClassWriter cw, String targetType, String s1Type, String s2Type, String classType, Map<Parameter, Method> setters) throws NoSuchMethodException {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" + AsmUtils.toTargetTypeDeclaration(s1Type) + AsmUtils.toTargetTypeDeclaration(s2Type)+ ")" + AsmUtils.toTargetTypeDeclaration(targetType), null, new String[] { "java/lang/Exception" });
        mv.visitCode();


        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD,classType, "builderInstantiator", AsmUtils.toTargetTypeDeclaration(INSTANTIATOR_CLASS));
        mv.visitInsn(ACONST_NULL);
        mv.visitMethodInsn(INVOKEINTERFACE, AsmUtils.toAsmType(INSTANTIATOR_CLASS), "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        final Type builderClass = getTargetType(instantiatorDefinition.getBuilderInstantiator());
        final String builderType = AsmUtils.toAsmType(builderClass);
        mv.visitTypeInsn(CHECKCAST, builderType);

        mv.visitVarInsn(ASTORE, 2);

        for (Entry<Parameter, Method> e : setters.entrySet()) {
            mv.visitVarInsn(ALOAD, 2);
            Parameter p = e.getKey();
            BiFactory<? super S1, ? super S2, ?> factory = injections.get(p);


            if (factory == null) {
                if (TypeHelper.isPrimitive(p.getType())) {
                    mv.visitInsn(AsmUtils.defaultValue.get(p.getType()));
                } else {
                    mv.visitInsn(ACONST_NULL);
                }
            } else {

                invovkeFactory(p, factory, classType, s1, s2, mv);

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


    private static void appendBridgeMethod(ClassWriter cw, String targetType, String s1Type, String s2Type,String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC,
                "newInstance", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null,
                new String[] { "java/lang/Exception" });

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, s1Type);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, s2Type);
        mv.visitMethodInsn(INVOKEVIRTUAL, classType, "newInstance", "(" + AsmUtils.toTargetTypeDeclaration(s1Type) + AsmUtils.toTargetTypeDeclaration(s2Type) +")"
                + AsmUtils.toTargetTypeDeclaration(targetType), false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private static <S1, S2> void appendToString(Map<Parameter, BiFactory<? super S1, ? super S2, ?>> injections, ClassWriter cw, Parameter[] parameters) {
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

            BiFactory<? super S1, ? super S2, ?> biFactory = injections.get(parameters[i]);

            String getterName =  ", parameter" + i + "=";
            String getterString = String.valueOf(biFactory);

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
