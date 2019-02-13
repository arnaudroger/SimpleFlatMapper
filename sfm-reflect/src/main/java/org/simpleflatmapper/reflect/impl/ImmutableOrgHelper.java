package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.ow2asm.*;
import org.simpleflatmapper.reflect.asm.AsmUtils;

import java.io.InputStream;
import java.lang.reflect.Type;

public class ImmutableOrgHelper {
    public static Type findImplementation(Class<?> targetClass) {

        ClassLoader cl = targetClass.getClassLoader();
        if (cl == null) {
            return null;
        }

        final String fileName = targetClass.getName().replace('.', '/') + ".class";
        final InputStream is = cl.getResourceAsStream(fileName);
        if (is == null) {
            return null;
        }
        try {
            ClassReader classReader = new ClassReader(is);
            ImmutableOrgClassVisitor classVisitor = new ImmutableOrgClassVisitor();
            classReader.accept(classVisitor, 0);

            if (classVisitor.isImmutable) {
                String name = targetClass.getName();

                int indexOfLastDot = name.lastIndexOf('.');

                String immutablePrefix = classVisitor.getImmutablePrefix();

                String immutableName;
                if (indexOfLastDot != -1) {
                    immutableName = name.substring(0, indexOfLastDot + 1) + immutablePrefix + name.substring(indexOfLastDot + 1);
                } else {
                    immutableName = immutablePrefix + name;
                }

                ClassLoader classLoader = targetClass.getClassLoader();
                return classLoader.loadClass(immutableName);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }

        return null;
    }



    private static class ImmutableOrgClassVisitor extends ClassVisitor {
        private boolean isImmutable;
        private String immutablePrefix = "Immutable*";

        public ImmutableOrgClassVisitor() {
            super(AsmUtils.API);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if ("Lorg/immutables/value/Value$Immutable;".equals(descriptor)) {
                isImmutable = true;
            } else if ("Lorg/immutables/value/Value$Style;".equals(descriptor)) {
                return new AnnotationVisitor(AsmUtils.API) {
                    @Override
                    public void visit(String name, Object value) {
                        if ("typeImmutable".equals(name)) {
                            immutablePrefix = (String) value;
                        }
                        super.visit(name, value);
                    }

                    @Override
                    public void visitEnum(String name, String descriptor, String value) {
                        super.visitEnum(name, descriptor, value);
                    }

                    @Override
                    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                        return super.visitAnnotation(name, descriptor);
                    }

                    @Override
                    public AnnotationVisitor visitArray(String name) {
                        return super.visitArray(name);
                    }
                };
            }
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }

        public String getImmutablePrefix() {
            if (immutablePrefix.endsWith("*")) {
                return immutablePrefix.substring(0, immutablePrefix.length() - 1);
            }
            return immutablePrefix;
        }
    }
}
