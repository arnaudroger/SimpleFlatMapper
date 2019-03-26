package org.simpleflatmapper.lightningcsv.impl;

import org.simpleflatmapper.lightningcsv.parser.AbstractCharConsumer;
import org.simpleflatmapper.lightningcsv.parser.CellPreProcessor;
import org.simpleflatmapper.lightningcsv.parser.CharBuffer;
import org.simpleflatmapper.lightningcsv.parser.CharConsumerFactory;
import org.simpleflatmapper.lightningcsv.parser.ConfigurableCharConsumer;
import org.simpleflatmapper.lightningcsv.parser.TextFormat;
import org.simpleflatmapper.lightningcsv.parser.UnescapeCellPreProcessor;
import org.simpleflatmapper.ow2asm.ClassReader;
import org.simpleflatmapper.ow2asm.ClassVisitor;
import org.simpleflatmapper.ow2asm.ClassWriter;
import org.simpleflatmapper.ow2asm.FieldVisitor;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.ow2asm.Opcodes;
import org.simpleflatmapper.util.FactoryClassLoader;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.simpleflatmapper.lightningcsv.impl.AsmUtils.toTargetTypeDeclaration;
import static org.simpleflatmapper.ow2asm.Opcodes.BIPUSH;
import static org.simpleflatmapper.ow2asm.Opcodes.ICONST_0;
import static org.simpleflatmapper.ow2asm.Opcodes.ICONST_1;
import static org.simpleflatmapper.ow2asm.Opcodes.IRETURN;

public class AsmCharConsumerFactory extends CharConsumerFactory {
    private static final AtomicInteger id = new AtomicInteger();
    
    public AbstractCharConsumer newCharConsumer(TextFormat textFormat, CharBuffer charBuffer, CellPreProcessor cellTransformer, boolean specialisedCharConsumer) {
        if (specialisedCharConsumer) {
            SpecialisationKey key = new SpecialisationKey(cellTransformer.ignoreLeadingSpace(), textFormat, cellTransformer.getClass());

            Constructor<? extends AbstractCharConsumer> constructor;

            if (key.equals(RFC4180)) {
                constructor = RFC4180_CC;
            } else {
                constructor = specialisedCharConsumers.get(key);
                if (constructor == null && specialisedCharConsumers.size() < 64) {
                    constructor = createNewSpecialisedCharConsumer(key);
                }
            }

            if (constructor != null) {
                try {
                    return constructor.newInstance(charBuffer, key.textFormat, cellTransformer);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }

        return new ConfigurableCharConsumer(charBuffer, textFormat, cellTransformer);

    }


    private static Constructor<? extends AbstractCharConsumer> createNewSpecialisedCharConsumer(SpecialisationKey key) {
        synchronized (lock) {
            Constructor<? extends AbstractCharConsumer> constructor;
            constructor = specialisedCharConsumers.get(key);
            if (constructor == null) {
                if (specialisedCharConsumers.size() < 64) { // artificial limit to avoid DOS
                    constructor = generateSpecialisedCharConsumer(key);
                    if (constructor != null) {
                        specialisedCharConsumers.put(key, constructor);
                    }
                }
            }
            return constructor;
        }
    }

    @SuppressWarnings("unchecked")
    private static Constructor<? extends AbstractCharConsumer> generateSpecialisedCharConsumer(final SpecialisationKey key) {
        try {
            final String newName = "org/simpleflatmapper/lightningcsv/parser/Asm_"
                    + (key.ignoreLeadingSpace ? "Ils_" : "")
                    + (key.textFormat.yamlComment ? "Yaml_" : "")
                    + "S_" + Integer.toHexString(key.textFormat.separatorChar)
                    + "_Q_" + Integer.toHexString(key.textFormat.quoteChar)
                    + "_E_" + Integer.toHexString(key.textFormat.escapeChar)
                    + key.cellTransformer.getClass().getName().replace('.', '_')
                    + "_CharConsumer_" + id.incrementAndGet();


            final Set<String> oldNames = new HashSet<String>(Arrays.asList("org/simpleflatmapper/lightningcsv/parser/ConfigurableCharConsumer"));
            ClassReader reader = new ClassReader(
                    ConfigurableCharConsumer.class.getResourceAsStream("ConfigurableCharConsumer.class")
            );
            ClassWriter writer = new ClassWriter(reader,      ClassWriter.COMPUTE_MAXS |
                    ClassWriter.COMPUTE_FRAMES);
            ClassVisitor visitor = new ClassVisitor(AsmUtils.API, writer) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    super.visit(version, access, newName, signature, superName, interfaces);
                }

                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    if (name.equals("cellPreProcessor")) {
                        super.visitField(access, name, toTargetTypeDeclaration(AsmUtils.toAsmType(key.cellTransformer)), toTargetTypeDeclaration(AsmUtils.toGenericAsmType(key.cellTransformer)), value);
                    }
                    return super.visitField(access, name, descriptor, signature, value);
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                    if (name.equals("ignoreLeadingSpace")) {
                        mv.visitCode();
                        mv.visitInsn(key.ignoreLeadingSpace ? ICONST_1 : ICONST_0);
                        mv.visitInsn(IRETURN);
                        mv.visitMaxs(1, 1);
                        mv.visitEnd();

                        return null;
                    } else if (name.equals("yamlComment")) {
                        mv.visitCode();
                        mv.visitInsn(key.textFormat.yamlComment ? ICONST_1 : ICONST_0);
                        mv.visitInsn(IRETURN);
                        mv.visitMaxs(1, 1);
                        mv.visitEnd();
                        return null;

                    } else if (name.equals("escapeChar")) {
                        mv.visitCode();
                        mv.visitIntInsn(BIPUSH, key.textFormat.escapeChar);
                        mv.visitInsn(IRETURN);
                        mv.visitMaxs(1, 1);
                        mv.visitEnd();
                        return null;

                    } else if (name.equals("separatorChar")) {
                        mv.visitCode();
                        mv.visitIntInsn(BIPUSH, key.textFormat.separatorChar);
                        mv.visitInsn(IRETURN);
                        mv.visitMaxs(1, 1);
                        mv.visitEnd();
                        return null;

                    } else if (name.equals("quoteChar")) {
                        mv.visitCode();
                        mv.visitIntInsn(BIPUSH, key.textFormat.quoteChar);
                        mv.visitInsn(IRETURN);
                        mv.visitMaxs(1, 1);
                        mv.visitEnd();
                        return null;

                    } else {
                        return new MethodVisitor(AsmUtils.API, mv) {
                            
                            
                            public void visitTypeInsn(int i, String s) {
                                if (oldNames.contains(s)) {
                                    s = newName;
                                }
                                mv.visitTypeInsn(i, s);
                            }

                            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                                if ("cellPreProcessor".equals(name)) {
                                    if (opcode == Opcodes.PUTFIELD) {
                                        mv.visitTypeInsn(Opcodes.CHECKCAST, AsmUtils.toAsmType(key.cellTransformer));
                                        mv.visitFieldInsn(opcode, newName, name, toTargetTypeDeclaration(AsmUtils.toAsmType(key.cellTransformer)));
                                    } else {
                                        mv.visitFieldInsn(opcode, newName, name, toTargetTypeDeclaration(AsmUtils.toAsmType(key.cellTransformer)));
                                    }
                                } else if (oldNames.contains(owner)) {
                                    mv.visitFieldInsn(opcode, newName, name, fix(desc));
                                } else {
                                    mv.visitFieldInsn(opcode, owner, name, fix(desc));
                                }
                            }

                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if (owner.equals(AsmUtils.toAsmType(CellPreProcessor.class))) {
                                    mv.visitMethodInsn(opcode, AsmUtils.toAsmType(key.cellTransformer), name, desc, itf);
                                } else if (oldNames.contains(owner)) {
                                    mv.visitMethodInsn(opcode, newName, name, fix(desc), itf);
                                } else {
                                    mv.visitMethodInsn(opcode, owner, name, fix(desc), itf);
                                }
                            }

                            private String fix(String s) {
                                if (s != null) {
                                    Iterator it = oldNames.iterator();
                                    String name;
                                    while (it.hasNext()) {
                                        name = (String) it.next();
                                        if (s.indexOf(name) != -1) {
                                            s = s.replaceAll(name, newName);
                                        }
                                    }
                                }
                                return s;
                            }
                        };
                    }
                }
            };
            reader.accept(visitor, 0);

            byte[] bytes = writer.toByteArray();

            String className = newName.replace('/', '.');
            AsmUtils.writeClassToFile(className, bytes);

            Class<?> clazz = classLoader.registerClass(className, bytes);

            return (Constructor<? extends AbstractCharConsumer>) clazz.getConstructor(CharBuffer.class, TextFormat.class, CellPreProcessor.class);

        } catch (Exception e) {
            // ignore
            System.err.println("Error creating specialised parser " + e.getMessage());
        }
        return null;
    }

    private static final FactoryClassLoader classLoader = new FactoryClassLoader(AbstractCharConsumer.class.getClassLoader());
    private static final Object lock = new Object();
    private static final SpecialisationKey RFC4180 = new SpecialisationKey(false, new TextFormat(',', '"', '"', false), UnescapeCellPreProcessor.class);
    private static final Constructor<? extends AbstractCharConsumer> RFC4180_CC;

    static {
        RFC4180_CC = generateSpecialisedCharConsumer(RFC4180);
    }

    private static final ConcurrentHashMap<SpecialisationKey, Constructor<? extends AbstractCharConsumer>> specialisedCharConsumers =
            new ConcurrentHashMap<SpecialisationKey, Constructor<? extends AbstractCharConsumer>>();

    private static class SpecialisationKey {
        final boolean ignoreLeadingSpace;
        final TextFormat textFormat;
        final Class<?> cellTransformer;
        private SpecialisationKey(boolean ignoreLeadingSpace, TextFormat textFormat, Class<?> cellTransformer) {
            this.ignoreLeadingSpace = ignoreLeadingSpace;
            this.textFormat = textFormat;
            this.cellTransformer = cellTransformer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SpecialisationKey that = (SpecialisationKey) o;

            if (ignoreLeadingSpace != that.ignoreLeadingSpace) return false;
            if (!textFormat.equals(that.textFormat)) return false;
            return cellTransformer.equals(that.cellTransformer);
        }

        @Override
        public int hashCode() {
            int result = (ignoreLeadingSpace ? 1 : 0);
            result = 31 * result + textFormat.hashCode();
            result = 31 * result + cellTransformer.hashCode();
            return result;
        }
    }
}