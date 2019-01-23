package org.simpleflatmapper.reflect.asm;

import org.simpleflatmapper.ow2asm.ClassWriter;
import org.simpleflatmapper.ow2asm.FieldVisitor;
import org.simpleflatmapper.ow2asm.Label;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.ow2asm.Opcodes;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.getter.BiFunctionGetter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


public class BiInstantiatorBuilder {

    public static final Class<BiInstantiator> BI_INSTANTIATOR_CLASS = BiInstantiator.class;
    public static final Class<Instantiator> INSTANTIATOR_CLASS = Instantiator.class;

    public static final String BI_FUNCTION_PREFIX = "factory_";

    public static <S1, S2>  byte[] createInstantiator(final String className, final Class<?> s1, final Class<?> s2,
                                                      final ExecutableInstantiatorDefinition instantiatorDefinition, final Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injectionsMap) throws Exception {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		Class<?> targetClass= TypeHelper.toClass(getTargetType(instantiatorDefinition));
		
		String targetType = AsmUtils.toAsmType(targetClass);
		String s1Type = AsmUtils.toWrapperType(s1);
        String s2Type = AsmUtils.toWrapperType(s2);
        String classType = AsmUtils.toAsmType(className);
        String instantiatorType = AsmUtils.toAsmType(BI_INSTANTIATOR_CLASS);
        List<InjectionPoint> injections = toInjections(injectionsMap);

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "Ljava/lang/Object;L" + instantiatorType + "<L"	+ targetType + ";>;", "java/lang/Object",
				new String[] { instantiatorType });

        Parameter[] parameters = instantiatorDefinition.getParameters();

        appendFunctionsField(injections, cw);
        appendInit(injections, cw, s1Type, s2Type, classType);
        appendNewInstance(s1, s2, instantiatorDefinition, injections, cw, targetType, s1Type, s2Type, classType, parameters);
        appendBridgeMethod(cw, targetType, s1Type, s2Type, classType);
        appendToString(injections, cw, parameters);
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}

    private static <S1, S2>  List<InjectionPoint> toInjections(Map<Parameter, BiFunction<? super S1, ? super S2 , ?>> injectionsMap) {
        List<InjectionPoint> injections = new ArrayList<InjectionPoint>(injectionsMap.size());
        for(Entry<Parameter, BiFunction<? super S1, ? super S2 , ?>> e : injectionsMap.entrySet()) {
            injections.add(getFunctionCall(e.getKey(), e.getValue()));
        }
        return injections;
    }

    public static <S1, S2>  byte[] createInstantiator(final String className, final Class<?> s1, final Class<?> s2,
                                                      final Instantiator<Void, ?> builderInstantiator,
                                                      final BuilderInstantiatorDefinition instantiatorDefinition,
                                                      final Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injectionsMap, boolean ignoreNullValues) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES );

        Class<?> targetClass= TypeHelper.toClass(getTargetType(instantiatorDefinition));

        String targetType = AsmUtils.toAsmType(targetClass);
        String s1Type = AsmUtils.toWrapperType(s1);
        String s2Type = AsmUtils.toWrapperType(s2);
        String classType = AsmUtils.toAsmType(className);
        String instantiatorType = AsmUtils.toAsmType(BI_INSTANTIATOR_CLASS);

        List<InjectionPoint> injections = toInjections(injectionsMap);
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

        appendFunctionsField(injections, cw);
        appendInitBuilder(injections, cw, s1Type, s2Type, classType, instantiatorDefinition);
        appendNewInstanceBuilder(s1, s2, instantiatorDefinition, injections, cw, targetType, s1Type, s2Type, classType, instantiatorDefinition.getSetters(), ignoreNullValues);
        appendBridgeMethod(cw, targetType, s1Type, s2Type, classType);
        appendToString(injections, cw, instantiatorDefinition.getParameters());
        cw.visitEnd();

        return AsmUtils.writeClassToFile(className, cw.toByteArray());
    }

    private static <S1, S2> void appendFunctionsField(List<InjectionPoint> injections, ClassWriter cw) {
        for(InjectionPoint injectionPoint : injections) {
            FieldVisitor fv = cw.visitField(ACC_FINAL, getBiFunctionFieldName(injectionPoint.parameter), AsmUtils.toTargetTypeDeclaration(injectionPoint.functionType), null, null);
            fv.visitEnd();
        }
    }

    private static <S1, S2> void appendInit(List<InjectionPoint> injections, ClassWriter cw, String s1, String s2, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(Ljava/util/Map;)V",
                "(Ljava/util/Map<Ljava.lang.String;L" + AsmUtils.toAsmType(BiFunction.class)  +"<" + AsmUtils.toTargetTypeDeclaration(s1) + "*" + AsmUtils.toTargetTypeDeclaration(s2) + "*>;>;)V", null);

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        appendInitField(injections, classType, mv);

        mv.visitInsn(RETURN);

        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }


    private static <S1, S2> void appendInitBuilder(List<InjectionPoint> injections, ClassWriter cw, String s1, String s2, String classType, BuilderInstantiatorDefinition instantiatorDefinition) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(Ljava/util/Map;L" + AsmUtils.toAsmType(INSTANTIATOR_CLASS) + ";)V",
                "(Ljava/util/Map<Ljava.lang.String;L" + AsmUtils.toAsmType(BiFunction.class)  +"<" + AsmUtils.toTargetTypeDeclaration(s1) + "*" + AsmUtils.toTargetTypeDeclaration(s2) + "*>;>;" +
                        "L" + AsmUtils.toAsmType(INSTANTIATOR_CLASS) + "<Ljava/lang/Void;L" + AsmUtils.toAsmType(getTargetType(instantiatorDefinition.getBuilderInstantiator())) +";>;)V", null);

        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitFieldInsn(PUTFIELD, classType, "builderInstantiator", "L" + AsmUtils.toAsmType(INSTANTIATOR_CLASS) + ";");

        appendInitField(injections, classType, mv);

        mv.visitInsn(RETURN);

        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private static <S1, S2> void appendInitField(List<InjectionPoint> injections, String classType, MethodVisitor mv) {
        for(InjectionPoint injectionPoint : injections) {

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(injectionPoint.parameter.getName());
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            if (injectionPoint.isGetter) {
                mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(BiFunctionGetter.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmUtils.toAsmType(BiFunctionGetter.class), "getGetter", "()" + AsmUtils.toTargetTypeDeclaration(Getter.class), false);
            }
            mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(injectionPoint.functionType));
            mv.visitFieldInsn(PUTFIELD, classType, getBiFunctionFieldName(injectionPoint.parameter), AsmUtils.toTargetTypeDeclaration(injectionPoint.functionType));
        }
    }


    private static <S1, S2> void appendNewInstance(Class<?> s1, Class<?> s2, ExecutableInstantiatorDefinition instantiatorDefinition, List<InjectionPoint> injectionPoints, ClassWriter cw, String targetType, String s1Type, String s2Type, String classType, Parameter[] parameters) throws NoSuchMethodException {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" + AsmUtils.toTargetTypeDeclaration(s1Type) + AsmUtils.toTargetTypeDeclaration(s2Type) +")" + AsmUtils.toTargetTypeDeclaration(targetType), null, new String[] { "java/lang/Exception" });
        mv.visitCode();
        
        if (!Modifier.isStatic(instantiatorDefinition.getExecutable().getModifiers())) {
            mv.visitTypeInsn(NEW, targetType);
            mv.visitInsn(DUP);
        }

        StringBuilder sb = new StringBuilder();

        for (Parameter p : parameters) {
            final InjectionPoint function = findFunctionCalls(p, injectionPoints);

            sb.append(AsmUtils.toTargetTypeDeclaration(p.getType()));

            if (function == null) {
                newInstanceNullFunction(mv, p);
            } else {
                invokeBiFunction(targetType, function, classType, s1, s2, mv,  new Consumer<MethodVisitor>() {
                    @Override
                    public void accept(MethodVisitor mv) {
                        mv.visitVarInsn(AsmUtils.getLoadOps(function.parameter.getType()), 4);
                    }
                }, false);
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

    private static <S1, S2> void appendNewInstanceBuilder(Class<?> s1, Class<?> s2, BuilderInstantiatorDefinition instantiatorDefinition, List<InjectionPoint> injectionPoints, ClassWriter cw, String targetType, String s1Type, String s2Type, String classType, Map<Parameter, Method> setters, boolean ignoreNullValues) throws NoSuchMethodException {
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

        mv.visitVarInsn(ASTORE, 3);

        for (final Entry<Parameter, Method> e : setters.entrySet()) {

            Parameter p = e.getKey();

            final InjectionPoint injectionPoint = findFunctionCalls(p, injectionPoints);

            if (injectionPoint != null) {

                final boolean checkIfNull = ignoreNullValues && !injectionPoint.isPrimitive;
                
       
                invokeBiFunction(targetType, injectionPoint, classType, s1, s2, mv,

                        new Consumer<MethodVisitor>() {
                            @Override
                            public void accept(MethodVisitor mv) {
                                mv.visitVarInsn(ALOAD, 3);
                                mv.visitVarInsn(AsmUtils.getLoadOps(injectionPoint.parameter.getType()), 4);
                                AsmUtils.invoke(mv, TypeHelper.toClass(builderClass), e.getValue().getName(),
                                        AsmUtils.toSignature(e.getValue()));
                                if (!Void.TYPE.equals(e.getValue().getReturnType())) {
                                    if(!e.getValue().getReturnType().equals(builderClass)) {
                                        mv.visitTypeInsn(CHECKCAST, builderType);
                                    }
                                    mv.visitVarInsn(ASTORE, 3);
                                }
                            }
                        }, ignoreNullValues);
            }
        }
        mv.visitVarInsn(ALOAD, 3);
        
        Method buildMethod = instantiatorDefinition.getBuildMethod();
        if (Modifier.isStatic(buildMethod.getModifiers())) {
            mv.visitMethodInsn(INVOKESTATIC, 
                    AsmUtils.toAsmType(buildMethod.getDeclaringClass()), 
                    buildMethod.getName(), AsmUtils.toSignature(buildMethod), false);
        } else {
            AsmUtils.invoke(mv, TypeHelper.toClass(builderClass),
                    buildMethod.getName(),
                    AsmUtils.toSignature(buildMethod));
        }

        mv.visitInsn(ARETURN);
        mv.visitMaxs(3 , 2);
        mv.visitEnd();
    }

    private static InjectionPoint findFunctionCalls(Parameter p, List<InjectionPoint> injectionPoints) {
        for(InjectionPoint fc : injectionPoints) {
            if (fc.parameter.equals(p)) {
                return fc;
            }
        }
        return null;
    }

    private static void newInstanceNullFunction(MethodVisitor mv, Parameter p) {
        if (TypeHelper.isPrimitive(p.getType())) {
            mv.visitInsn(AsmUtils.defaultValue.get(p.getType()));
        } else {
            mv.visitInsn(ACONST_NULL);
        }
    }


    private static <S1, S2> void invokeBiFunction(String targetType, InjectionPoint injectionPoint, String classType, Class<?> s1, Class<?> s2, MethodVisitor mv, Consumer<MethodVisitor> consumer, boolean ignoreNullValues) throws NoSuchMethodException {

        String s1Type = AsmUtils.toAsmType(s1.equals(Void.class) || s1.equals(void.class) ? Object.class : s1);
        String s2Type = AsmUtils.toAsmType(s2.equals(Void.class) || s2.equals(void.class) ? Object.class : s2);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classType, getBiFunctionFieldName(injectionPoint.parameter), AsmUtils.toTargetTypeDeclaration(injectionPoint.functionType));
        mv.visitVarInsn(ALOAD, 1);
        if (!injectionPoint.isGetter){
            mv.visitVarInsn(ALOAD, 2);
        }

        Method getterMethod = injectionPoint.getMethod();

        AsmUtils.invoke(mv, getterMethod);

        if (!injectionPoint.isPrimitive) {
            
            // IF NULL
            Class<?> wrapperClass = AsmUtils.toWrapperClass(injectionPoint.parameter.getType());
            if (!wrapperClass.isAssignableFrom(getterMethod.getReturnType())) {
                mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(wrapperClass));
            }

            mv.visitVarInsn(ASTORE, 4);

            Label label = new Label();
            if (ignoreNullValues) {

                mv.visitVarInsn(ALOAD, 4);
                mv.visitJumpInsn(IFNULL, label);
            }

            changeToPrimitiveIfNeeded(injectionPoint, mv, wrapperClass, ignoreNullValues);

            if (consumer != null) {
                consumer.accept(mv);
            }

            if (ignoreNullValues) {
                mv.visitLabel(label);
                if (injectionPoint.isGetter) {
                    mv.visitFrame(Opcodes.F_APPEND, 2, new Object[]{AsmUtils.toAsmType(targetType), AsmUtils.toAsmType(wrapperClass) }, 0, null);
                } else {
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{AsmUtils.toAsmType(wrapperClass)}, 0, null);
                }
            }
            // iframe?
        } else {
            mv.visitVarInsn(AsmUtils.getStoreOps(injectionPoint.parameter.getType()), 4);
            if (consumer != null) {
                consumer.accept(mv);
            }
        }
    }

    private static void changeToPrimitiveIfNeeded(InjectionPoint injectionPoint, MethodVisitor mv, Class<?> wrapperClass, boolean ignoreNullValues) {
        if (TypeHelper.isPrimitive(injectionPoint.parameter.getType())) {
            mv.visitVarInsn(ALOAD, 4);
            String methodSuffix = getPrimitiveMethodSuffix(injectionPoint.parameter);
            String valueMethodPrefix = methodSuffix.toLowerCase();
            if ("character".equals(valueMethodPrefix)) {
                valueMethodPrefix = "char";
            }
            String valueMethod = valueMethodPrefix + "Value";
            AsmUtils.invoke(mv, wrapperClass, valueMethod, "()" + AsmUtils.toAsmType(injectionPoint.parameter.getType()));
            mv.visitVarInsn(AsmUtils.getStoreOps(injectionPoint.parameter.getType()), 4);
        }
    }

    private static String getBiFunctionFieldName(Parameter p) {
        return BI_FUNCTION_PREFIX + p.getName();
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

    private static <S1, S2> void appendToString(List<InjectionPoint> injections, ClassWriter cw, Parameter[] parameters) {
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

            InjectionPoint injectionPoint = findFunctionCalls(parameters[i], injections);

            String getterName =  ", parameter" + i + "=";
            String getterString = injectionPoint != null ? injectionPoint.functionType.toString() : "<null>";

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

    private static InjectionPoint getFunctionCall(Parameter parameter, BiFunction biFunction) {
        if (biFunction.getClass().equals(BiFunctionGetter.class)) {
            Getter getter = ((BiFunctionGetter)biFunction).getGetter();
            Class<?> getterClass = getter.getClass();
            if (TypeHelper.isPrimitive(parameter.getType())) {
                Class<?> primitiveGetter = getPrimitiveGetter(parameter.getType());
                if (primitiveGetter == null) {
                    throw new IllegalStateException("No primitive getter for primitive " + parameter.getType());
                }
                Type publicGetterClass = AsmUtils.findClosestPublicTypeExposing(getterClass, primitiveGetter);
                if (publicGetterClass != null) {
                    return new InjectionPoint(parameter, biFunction, true, "get" + getPrimitiveMethodSuffix(parameter.getType()), publicGetterClass, true);
                }
            }

            Type publicGetterClass = AsmUtils.findClosestPublicTypeExposing(getterClass, Getter.class);
            return new InjectionPoint(parameter, biFunction, true, "get", publicGetterClass, false);
        } else {
            return new InjectionPoint(parameter, biFunction, false, "apply", biFunction.getClass(), false);
        }
    }

    public static Type getTargetType(InstantiatorDefinition instantiatorDefinition) {

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


    static final Map<Class<?>, Class<?>> primitivesGetter = new HashMap<Class<?>, Class<?>>();
    static {
        primitivesGetter.put(boolean.class, BooleanGetter.class);
        primitivesGetter.put(byte.class,    ByteGetter.class);
        primitivesGetter.put(char.class,    CharacterGetter.class);
        primitivesGetter.put(short.class,   ShortGetter.class);
        primitivesGetter.put(int.class,     IntGetter.class);
        primitivesGetter.put(long.class,    LongGetter.class);
        primitivesGetter.put(float.class,   FloatGetter.class);
        primitivesGetter.put(double.class,  DoubleGetter.class);
    }
    public static Class<?> getPrimitiveGetter(Class<?> propertyType) {
        return primitivesGetter.get(propertyType);
    }

    static class InjectionPoint {
        final Parameter parameter;
        final BiFunction<?, ?, ?> function;
        final boolean isGetter;
        final String methodName;
        final Type functionType;
        final boolean isPrimitive;

        InjectionPoint(Parameter parameter, BiFunction<?, ?, ?> function, boolean isGetter, String methodName, Type functionType, boolean isPrimitive) {
            this.parameter = parameter;
            this.function = function;
            this.isGetter = isGetter;
            this.methodName = methodName;
            this.functionType = functionType;
            this.isPrimitive = isPrimitive;
        }

        public Method getMethod() {
            return BiInstantiatorBuilder.getMethod(TypeHelper.toClass(functionType), methodName, isGetter ? 1 : 2);
        }
    }


    public static Method getMethod(Class<?> aClass, String name, int nbParam) {
        Method m = null;
        for(Method p : aClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(p.getModifiers())
                    && p.getName().equals(name)
                    && (p.getParameterTypes() != null && p.getParameterTypes().length == nbParam)) {
                // crude way of selecting non bridge method
                if (m == null || p.getModifiers() < m.getModifiers()) {
                    m = p;
                }
            }
        }
        return m;
    }

}
