package org.sfm.reflect.asm;

import com.sun.org.apache.xpath.internal.compiler.OpCodes;
import org.objectweb.asm.*;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.ErrorHelper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsmInstantiatorDefinitionFactory {

    public static List<InstantiatorDefinition> extractDefinitions(final Type target) throws IOException {
        final List<InstantiatorDefinition> constructors = new ArrayList<InstantiatorDefinition>();

        final Class<?> targetClass = TypeHelper.toClass(target);

        ClassLoader cl = targetClass.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }

        final String fileName = targetClass.getName().replace('.', '/') + ".class";
        final InputStream is = cl.getResourceAsStream(fileName);
        try {
            if (is == null) {
                throw new IOException("Cannot find file " + fileName + " in " + cl);
            }
            ClassReader classReader = new ClassReader(is);
            classReader.accept(new ClassVisitor(Opcodes.ASM5) {
                List<String> genericTypeNames;

                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    if (signature != null) {
                        genericTypeNames =  AsmUtils.extractGenericTypeNames(signature);
                    } else {
                        genericTypeNames = Collections.emptyList();
                    }
                    super.visit(version, access, name, signature, superName, interfaces);
                }

                @Override
                public MethodVisitor visitMethod(int access,
                                                 final String methodName,
                                                 String desc,
                                                 String signature,
                                                 String[] exceptions) {
                    final boolean isConstructor = "<init>".equals(methodName);
                    if ((Opcodes.ACC_PUBLIC & access) == Opcodes.ACC_PUBLIC
                            && (isConstructor || (Opcodes.ACC_STATIC & access) == Opcodes.ACC_STATIC)) {
                        final List<String> descTypes = AsmUtils.extractConstructorTypeNames(desc);
                        final List<String> genericTypes;
                        final List<String> names = new ArrayList<String>();
                        if (signature != null) {
                            genericTypes = AsmUtils.extractConstructorTypeNames(signature);
                        } else {
                            genericTypes = descTypes;
                        }

                        if (!isConstructor) {
                            if (descTypes.size() > 1) {
                                try {
                                    final Type genericType = AsmUtils.toGenericType(descTypes.get(descTypes.size() - 1), genericTypeNames, target);
                                    if (!targetClass.isAssignableFrom(TypeHelper.toClass(genericType))) {
                                        return null;
                                    }
                                } catch (ClassNotFoundException e) {
                                    return null;
                                }
                            } else return null;
                        }

                        return new MethodVisitor(Opcodes.ASM5) {


                            Label firstLabel;
                            Label lastLabel;
                            @Override
                            public void visitLabel(Label label) {
                                if (firstLabel == null) {
                                    firstLabel = label;
                                }
                                lastLabel = label;
                            }

                            @Override
                            public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                                if (start.equals(firstLabel) && end.equals(lastLabel) && ! "this".equals(name)) {
                                    names.add(name);
                                }
                            }


                            @Override
                            public void visitEnd() {
                                try {
                                    final List<Parameter> parameters = new ArrayList<Parameter>();

                                    int l = descTypes.size() - (isConstructor ? 0 : 1);
                                    for(int i = 0; i < l; i ++) {
                                        String name = "arg" + i;
                                        if (i < names.size()) {
                                            name =names.get(i);
                                        }
                                        parameters.add(createParameter(name, descTypes.get(i), genericTypes.get(i)));
                                    }

                                    final Member executable;

                                    if (isConstructor) {
                                        executable = targetClass.getDeclaredConstructor(toTypeArray(parameters));
                                    } else {
                                        executable = targetClass.getDeclaredMethod(methodName, toTypeArray(parameters));
                                    }
                                    constructors.add(new InstantiatorDefinition(executable, parameters.toArray(new Parameter[parameters.size()])));
                                } catch(Exception e) {
                                    ErrorHelper.rethrow(e);
                                }
                            }

                            private Class<?>[] toTypeArray(List<Parameter> parameters) {
                                Class<?>[] types = new Class<?>[parameters.size()];
                                for(int i = 0; i < types.length; i++) {
                                    types[i] = parameters.get(i).getType();
                                }
                                return types;
                            }

                            private Parameter createParameter(String name,
                                                                         String desc, String signature) {
                                try {

                                    Type basicType = AsmUtils.toGenericType(desc, genericTypeNames, target);
                                    Type genericType = basicType;
                                    if (signature != null) {
                                        genericType = AsmUtils.toGenericType(signature, genericTypeNames, target);
                                    }
                                    return new Parameter(name, TypeHelper.toClass(basicType), genericType);
                                } catch (ClassNotFoundException e) {
                                    throw new Error("Unexpected error " + e, e);
                                }
                            }
                        };
                    } else {
                        return null;
                    }
                }



            }, 0);
        } finally {
            try { is.close(); } catch(Exception e) {}
        }

        return constructors;
    }
}
