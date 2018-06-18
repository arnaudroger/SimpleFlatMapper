package org.simpleflatmapper.csv.impl.asm;

import org.simpleflatmapper.ow2asm.ClassWriter;
import org.simpleflatmapper.ow2asm.FieldVisitor;
import org.simpleflatmapper.ow2asm.Label;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.ow2asm.Opcodes;
import org.simpleflatmapper.reflect.asm.AsmUtils;
import org.simpleflatmapper.reflect.asm.ShardingHelper;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.ParsingContextFactory;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandlerFactory;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;

import java.lang.reflect.Type;

@SuppressWarnings("SpellCheckingInspection")
public class CsvMapperCellHandlerBuilder {

    public static final String DELAYED_CELL_SETTER_TYPE = AsmUtils.toAsmType(DelayedCellSetter.class);
    public static final String CELL_SETTER_TYPE = AsmUtils.toAsmType(CellSetter.class);
    public static final String CSV_CELL_MAPPER_TYPE = AsmUtils.toAsmType(CsvMapperCellHandler.class);
    public static final String CELL_HANDLER_FACTORY_TYPE = AsmUtils.toAsmType(CsvMapperCellHandlerFactory.class);

    public static <T> byte[] createTargetSetterClass(String className,
                                                 DelayedCellSetterFactory<T, ?>[] delayedCellSetters,
            CellSetter<T>[] setters, Type type, boolean ignoreException, int maxMethodSize) throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        FieldVisitor fv;
        MethodVisitor mv;

        final String targetType = AsmUtils.toAsmType(type);
        final String classType = AsmUtils.toAsmType(className);

        cw.visit(Opcodes.V1_6, Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, classType,
                "L" + CSV_CELL_MAPPER_TYPE + "<L" + targetType + ";>;", CSV_CELL_MAPPER_TYPE, null);


        // declare fields
        for(int i = 0; i < delayedCellSetters.length; i++) {
            if (delayedCellSetters[i] != null) {
                fv = cw.visitField(Opcodes.ACC_PROTECTED + Opcodes.ACC_FINAL, "delayedCellSetter" + i,
                        AsmUtils.toTargetTypeDeclaration(DELAYED_CELL_SETTER_TYPE),
                        "L" + DELAYED_CELL_SETTER_TYPE + "<L" + targetType + ";*>;", null);
                fv.visitEnd();
            }
        }

        for(int i = 0; i < setters.length; i++) {
            if (setters[i] != null) {
                fv = cw.visitField(Opcodes.ACC_PROTECTED + Opcodes.ACC_FINAL, "setter" + i,
                        AsmUtils.toTargetTypeDeclaration(CELL_SETTER_TYPE),
                        "L" + CELL_SETTER_TYPE + "<L" + targetType + ";>;", null);
                fv.visitEnd();
            }
        }
        appendInit(delayedCellSetters, setters, cw, targetType, classType, maxMethodSize);


        appendDelayedCellValue(delayedCellSetters, ignoreException, cw, classType);
        append_delayedCellValue(delayedCellSetters, cw, classType, maxMethodSize);

        appendCellValue(setters, ignoreException, cw, classType);
        append_cellValue(delayedCellSetters, setters, cw, classType, maxMethodSize);

        appendApplyDelayedSetter(delayedCellSetters, ignoreException, cw, classType, maxMethodSize);
        appendApplyDelayedCellSetterN(delayedCellSetters, cw, classType);

        appendGetDelayedCellSetter(delayedCellSetters, cw, targetType, classType, maxMethodSize);
        appendPeekDelayedCellSetterValue(delayedCellSetters, cw, classType, maxMethodSize);

        cw.visitEnd();


        return AsmUtils.writeClassToFile(className, cw.toByteArray());

    }

    private static <T> void append_cellValue(final DelayedCellSetterFactory<T, ?>[] delayedCellSetters, final CellSetter<T>[] setters, ClassWriter cw, final String classType, final int maxMethodSize) {

        ShardingHelper.shard(setters.length, maxMethodSize, new AbstractMethodDispatchShardCallBack<T>(cw, classType, maxMethodSize) {
            @Override
            protected void appendLeafSwitch(MethodVisitor mv, int start, int end) {
                Label defaultLabel = new Label();
                Label[] labels = newLabels(end - start);
                mv.visitTableSwitchInsn(delayedCellSetters.length + start, delayedCellSetters.length + end - 1, defaultLabel, labels);

                for (int i = start; i < end; i++) {

                    mv.visitLabel(labels[i - start]);
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    if (setters[i] != null) {
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, classType, "setter" + i, "L" + CELL_SETTER_TYPE + ";");
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, classType, "currentInstance", "Ljava/lang/Object;");
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitVarInsn(Opcodes.ILOAD, 2);
                        mv.visitVarInsn(Opcodes.ILOAD, 3);
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, classType, "parsingContext", AsmUtils.toTargetTypeDeclaration(ParsingContext.class));
                        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, CELL_SETTER_TYPE, "set", "(Ljava/lang/Object;[CIIL" + AsmUtils.toAsmType(ParsingContext.class) + ";)V", true);
                    }
                    if (i < (end - 1)) {
                        mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    }
                }
                mv.visitLabel(defaultLabel);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }

            @Override
            protected int maxArgIndex() {
                return 4;
            }

            @Override
            protected void loadArguments(MethodVisitor mv) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitVarInsn(Opcodes.ILOAD, 2);
                mv.visitVarInsn(Opcodes.ILOAD, 3);
                mv.visitVarInsn(Opcodes.ILOAD, 4);
            }

            @Override
            protected int argIndex() {
                return 4;
            }

            @Override
            protected String name() {
                return "_cellValue";
            }

            @Override
            protected String signature() {
                return "([CIII)V";
            }

            @Override
            protected int leafStart() {
                return delayedCellSetters.length;
            }

        });

    }

    private static <T> void appendPeekDelayedCellSetterValue(final DelayedCellSetterFactory<T, ?>[] delayedCellSetters, final ClassWriter cw, final String classType, final int maxMethodSize) {

        ShardingHelper.shard(delayedCellSetters.length, maxMethodSize, new AbstractMethodDispatchShardCallBack<T>(cw, classType, maxMethodSize ) {
            @Override
            protected void appendLeafSwitch(MethodVisitor mv, int start, int end) {
                Label defaultLabel = new Label();
                Label[] labels = newLabels(end - start);
                mv.visitTableSwitchInsn(start, end - 1, defaultLabel, labels);

                for (int i = start; i < end; i++) {
                    mv.visitLabel(labels[i -start]);
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    if (delayedCellSetters[i] != null) {
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, classType, "delayedCellSetter" + i, "L" + DELAYED_CELL_SETTER_TYPE + ";");
                        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, DELAYED_CELL_SETTER_TYPE, "peekValue", "()Ljava/lang/Object;", true);
                        mv.visitInsn(Opcodes.ARETURN);
                    } else if (i < (delayedCellSetters.length - 1)) {
                        mv.visitInsn(Opcodes.ACONST_NULL);
                        mv.visitInsn(Opcodes.ARETURN);
                    }
                }
                mv.visitLabel(defaultLabel);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }

            @Override
            protected int maxArgIndex() {
                return 1;
            }

            @Override
            protected void loadArguments(MethodVisitor mv) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ILOAD, 1);
            }

            @Override
            protected int argIndex() {
                return 1;
            }

            @Override
            protected String name() {
                return "_peekDelayedCellSetterValue";
            }

            @Override
            protected String signature() {
                return "(I)Ljava/lang/Object;";
            }
        });

        MethodVisitor mv;
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "peekDelayedCellSetterValue" , "(L" + AsmUtils.toAsmType(CsvColumnKey.class) + ";)Ljava/lang/Object;", null, null);
        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, AsmUtils.toAsmType(CsvColumnKey.class), "getIndex", "()I", false);
        mv.visitVarInsn(Opcodes.ISTORE, 2);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classType, "_peekDelayedCellSetterValue", "(I)Ljava/lang/Object;", false);


        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 2);
        mv.visitEnd();

    }
    private static <T> void append_delayedCellValue(final DelayedCellSetterFactory<T, ?>[] delayedCellSetters, final ClassWriter cw, final String classType, final int maxMethodSize) {


        ShardingHelper.shard(delayedCellSetters.length, maxMethodSize, new AbstractMethodDispatchShardCallBack<T>(cw, classType, maxMethodSize ) {
            @Override
            protected void appendLeafSwitch(MethodVisitor mv, int start, int end) {
                Label defaultLabel = new Label();
                Label[] labels = newLabels(end - start);
                mv.visitTableSwitchInsn(start, end - 1, defaultLabel, labels);

                for (int i = start; i < end; i++) {
                    mv.visitLabel(labels[i - start]);
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    if (delayedCellSetters[i] != null) {
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, classType, "delayedCellSetter" + i, "L" + DELAYED_CELL_SETTER_TYPE + ";");
                        mv.visitVarInsn(Opcodes.ALOAD, 1);
                        mv.visitVarInsn(Opcodes.ILOAD, 2);
                        mv.visitVarInsn(Opcodes.ILOAD, 3);
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, classType, "parsingContext", AsmUtils.toTargetTypeDeclaration(ParsingContext.class));
                        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, DELAYED_CELL_SETTER_TYPE, "set", "([CIIL" + AsmUtils.toAsmType(ParsingContext.class) + ";)V", true);
                    }
                    if (i < (end - 1)) {
                        mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    }
                }
                mv.visitLabel(defaultLabel);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);            }

            @Override
            protected void loadArguments(MethodVisitor mv) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitVarInsn(Opcodes.ILOAD, 2);
                mv.visitVarInsn(Opcodes.ILOAD, 3);
                mv.visitVarInsn(Opcodes.ILOAD, 4);
            }

            @Override
            protected int maxArgIndex() {
                return 4;
            }

            @Override
            protected int argIndex() {
                return 4;
            }

            @Override
            protected String name() {
                return "_delayedCellValue";
            }

            @Override
            protected String signature() {
                return "([CIII)V";
            }
        });
    }

    private static <T> void appendApplyDelayedSetter(final DelayedCellSetterFactory<T, ?>[] delayedCellSetters, final boolean ignoreException, final ClassWriter cw, final String classType, final int maxMethodSize) {
        ShardingHelper.shard(delayedCellSetters.length, maxMethodSize, new ShardingHelper.ShardCallBack() {
            @Override
            public void leafDispatch(String suffix, int start, int end) {
                MethodVisitor mv;
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "applyDelayedSetters" + suffix, "()V", null, null);
                mv.visitCode();

                if (end - start > 0) {

                    if (!ignoreException) {
                        Label[] labels = newLabels((3 * getNbNonNullSettersWithSetters(delayedCellSetters, start, end)) + 1);
                        for (int i = start, j = 0; i < end; i++) {
                            if (delayedCellSetters[i] != null && delayedCellSetters[i].hasSetter()) {
                                mv.visitTryCatchBlock(labels[j], labels[j + 1], labels[j + 2], "java/lang/Exception");
                                j += 3;
                            }
                        }

                        for (int i = start, j = 0; i < end; i++) {
                            if (delayedCellSetters[i] != null && delayedCellSetters[i].hasSetter()) {
                                mv.visitLabel(labels[j]);
                                if (j > 0) {
                                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                                }
                                mv.visitVarInsn(Opcodes.ALOAD, 0);
                                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classType, "applyDelayedCellSetter" + i, "()V", false);
                                mv.visitLabel(labels[j + 1]);
                                mv.visitJumpInsn(Opcodes.GOTO, labels[j + 3]);
                                mv.visitLabel(labels[j + 2]);
                                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
                                mv.visitVarInsn(Opcodes.ASTORE, 1);
                                mv.visitVarInsn(Opcodes.ALOAD, 0);
                                AsmUtils.addIndex(mv, i);
                                mv.visitVarInsn(Opcodes.ALOAD, 1);
                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, classType, "fieldError", "(ILjava/lang/Exception;)V", false);

                                j += 3;
                            }
                        }
                        mv.visitLabel(labels[labels.length - 1]);
                        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    } else {
                        for (int i = start; i < end; i++) {
                            if (delayedCellSetters[i] != null && delayedCellSetters[i].hasSetter()) {
                                mv.visitVarInsn(Opcodes.ALOAD, 0);
                                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classType, "applyDelayedCellSetter" + i, "()V", false);
                            }
                        }
                    }
                }
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(3, 2);
                mv.visitEnd();
            }

            @Override
            public void nodeDispatch(String suffix, int divide, int start, int end) {
                MethodVisitor mv;
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "applyDelayedSetters" + suffix, "()V", null, new String[]{"java/lang/Exception"});
                mv.visitCode();

                final int powerOfTwo = Integer.numberOfTrailingZeros(divide);

                int sStart = start >> powerOfTwo;
                int sEnd = end >> powerOfTwo;

                int left = end - (sEnd << powerOfTwo);
                if (left > 0) {
                    sEnd ++;
                }

                for (int i = sStart; i < sEnd; i++) {

                    int estart = i * divide;
                    int eend = Math.min(end, (i+1) * divide);

                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classType, "applyDelayedSetters"+ (divide/maxMethodSize) + "n" + estart + "t" + eend, "()V" , false);


                }

                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(6, 5);
                mv.visitEnd();
            }
        });
    }

    private static Label[] newLabels(int ll) {
        Label[] labels = new Label[ll];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label();
        }
        return labels;
    }

    private static <T> void appendApplyDelayedCellSetterN(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, ClassWriter cw, String classType) {
        MethodVisitor mv;
        for(int i = 0; i < delayedCellSetters.length; i++) {
            if (delayedCellSetters[i] != null) {
                mv = cw.visitMethod(Opcodes.ACC_PRIVATE, "applyDelayedCellSetter" + i, "()V", null, new String[]{"java/lang/Exception"});
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, classType, "delayedCellSetter" + i, "L" + DELAYED_CELL_SETTER_TYPE + ";");
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, classType, "currentInstance", "Ljava/lang/Object;");
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, DELAYED_CELL_SETTER_TYPE, "set", "(Ljava/lang/Object;)V", true);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(2, 1);
                mv.visitEnd();
            }
        }
    }

    private static <T> void appendCellValue(CellSetter<T>[] setters, boolean ignoreException, ClassWriter cw, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "cellValue", "([CIII)V", null, null);
        mv.visitCode();
        if (setters.length != 0) {
            if (ignoreException) {
                callCellValue(mv, classType);
            } else {
                Label l0 = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
                mv.visitLabel(l0);
                callCellValue(mv, classType);
                mv.visitLabel(l1);
                Label l3 = new Label();
                mv.visitJumpInsn(Opcodes.GOTO, l3);
                mv.visitLabel(l2);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
                mv.visitVarInsn(Opcodes.ASTORE, 5);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ILOAD, 4);
                mv.visitVarInsn(Opcodes.ALOAD, 5);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, classType, "fieldError", "(ILjava/lang/Exception;)V", false);
                mv.visitLabel(l3);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(5, 6);
        mv.visitEnd();
    }

    private static <T> void appendDelayedCellValue(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, boolean ignoreException, ClassWriter cw, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "delayedCellValue", "([CIII)V", null, null);
        mv.visitCode();
        if (delayedCellSetters.length != 0) {
            if (ignoreException) {
                callDelayedCellValue(mv, classType);
            } else {
                Label l0 = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
                mv.visitLabel(l0);
                callDelayedCellValue(mv, classType);
                mv.visitLabel(l1);
                Label l3 = new Label();
                mv.visitJumpInsn(Opcodes.GOTO, l3);
                mv.visitLabel(l2);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
                mv.visitVarInsn(Opcodes.ASTORE, 5);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ILOAD, 4);
                mv.visitVarInsn(Opcodes.ALOAD, 5);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, classType, "fieldError", "(ILjava/lang/Exception;)V", false);
                mv.visitLabel(l3);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }
        }
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(5, 6);
        mv.visitEnd();
    }

    private static <T> void appendInit(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, CellSetter<T>[] setters, ClassWriter cw, String targetType, String classType, int maxSize) {
        MethodVisitor mv;// constructor
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(" +
                    AsmUtils.toTargetTypeDeclaration(Instantiator.class) +
                    AsmUtils.toTargetTypeDeclaration(DelayedCellSetter[].class) +
                    AsmUtils.toTargetTypeDeclaration(CellSetter[].class) +
                    AsmUtils.toTargetTypeDeclaration(CsvColumnKey[].class) +
                    AsmUtils.toTargetTypeDeclaration(ParsingContext.class) +
                    AsmUtils.toTargetTypeDeclaration(FieldMapperErrorHandler.class) +
                    ")V",
                    "(" +
                    "L" + AsmUtils.toAsmType(Instantiator.class) +"<L" + AsmUtils.toAsmType(CsvMapperCellHandler.class)+ "<L" + targetType + ";>;L"+ targetType + ";>;" +
                    "[L" + DELAYED_CELL_SETTER_TYPE +  "<L" + targetType + ";*>;" +
                    "[L" + CELL_SETTER_TYPE + "<L" + targetType + ";>;" +
                    AsmUtils.toTargetTypeDeclaration(CsvColumnKey[].class) +
                    AsmUtils.toTargetTypeDeclaration(ParsingContext.class) +
                    "L" + AsmUtils.toAsmType(FieldMapperErrorHandler.class) + "<L" + AsmUtils.toAsmType(CsvColumnKey.class) + ";>;" +
                    ")V", null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitInsn(Opcodes.ARRAYLENGTH);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitInsn(Opcodes.ARRAYLENGTH);
            mv.visitVarInsn(Opcodes.ALOAD, 5);
            mv.visitVarInsn(Opcodes.ALOAD, 6);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, CSV_CELL_MAPPER_TYPE, "<init>",
                    "(" +
                            AsmUtils.toTargetTypeDeclaration(Instantiator.class) +
                            AsmUtils.toTargetTypeDeclaration(CsvColumnKey[].class) +
                            "I" +
                            "I" +
                            AsmUtils.toTargetTypeDeclaration(ParsingContext.class) +
                            AsmUtils.toTargetTypeDeclaration(FieldMapperErrorHandler.class) +
                            ")V", false);


            ShardingHelper.shard(delayedCellSetters.length, maxSize, new ShardingHelper.ShardCallBack() {
                @Override
                public void leafDispatch(String suffix, int start, int end) {

                }

                @Override
                public void nodeDispatch(String suffix, int divide, int start, int end) {

                }
            });
            for(int i = 0; i < delayedCellSetters.length; i++) {
                if (delayedCellSetters[i] != null) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 2);
                    AsmUtils.addIndex(mv, i);
                    mv.visitInsn(Opcodes.AALOAD);
                    mv.visitFieldInsn(Opcodes.PUTFIELD,
                            classType,
                            "delayedCellSetter" + i,
                            AsmUtils.toTargetTypeDeclaration(DELAYED_CELL_SETTER_TYPE));
                }
            }

            for(int i = 0; i < setters.length; i++) {
                if (setters[i] != null) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 3);
                    AsmUtils.addIndex(mv, i);
                    mv.visitInsn(Opcodes.AALOAD);
                    mv.visitFieldInsn(Opcodes.PUTFIELD,
                            classType,
                            "setter" + i ,
                            AsmUtils.toTargetTypeDeclaration(CELL_SETTER_TYPE));
                }
            }

            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(7, 7);
            mv.visitEnd();
        }
    }


    private static <T> void appendGetDelayedCellSetter(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, ClassWriter cw, String targetType, String classType, int maxMethodSize) {
        MethodVisitor mv;
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getDelayedCellSetter", "(I)L" +  DELAYED_CELL_SETTER_TYPE + ";",
                "(I)L" + DELAYED_CELL_SETTER_TYPE + "<L" + targetType + ";*>;", null);
        mv.visitCode();

        if (delayedCellSetters.length != 0) {
            final int switchStart = 0;
            final int switchEnd = delayedCellSetters.length;
            appendGetDelayedCellSetterSwitch(delayedCellSetters, classType, mv, switchStart, switchEnd);
        }
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(1, 2);
        mv.visitEnd();
    }

    private static <T> void appendGetDelayedCellSetterSwitch(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, String classType, MethodVisitor mv, int switchStart, int switchEnd) {
        mv.visitVarInsn(Opcodes.ILOAD, 1);
        Label defaultLabel = new Label();
        Label[] labels = newLabels(switchEnd - switchStart);
        mv.visitTableSwitchInsn(switchStart, switchEnd - 1, defaultLabel, labels);

        for (int i = switchStart; i < switchEnd; i++) {
            mv.visitLabel(labels[i - switchStart]);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            if (delayedCellSetters != null) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, classType, "delayedCellSetter" + i, "L" + DELAYED_CELL_SETTER_TYPE + ";");
            } else {
                mv.visitInsn(Opcodes.ACONST_NULL);
            }
            mv.visitInsn(Opcodes.ARETURN);
        }

        mv.visitLabel(defaultLabel);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }

    private static void callCellValue(MethodVisitor mv, String classType) {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitVarInsn(Opcodes.ILOAD, 3);
        mv.visitVarInsn(Opcodes.ILOAD, 4);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classType, "_cellValue", "([CIII)V", false);
    }

    private static void callDelayedCellValue(MethodVisitor mv, String classType) {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ILOAD, 2);
        mv.visitVarInsn(Opcodes.ILOAD, 3);
        mv.visitVarInsn(Opcodes.ILOAD, 4);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classType, "_delayedCellValue", "([CIII)V", false);
    }

    private static <T> int getNbNonNullSettersWithSetters(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, int start, int end) {
        int n = 0;
        for(int i = start; i < end; i++) {
            if (delayedCellSetters[i] != null && delayedCellSetters[i].hasSetter()) n++;
        }
        return n;
    }

    public static byte[] createTargetSetterFactory(String factoryName, String className, Type target) throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        String factoryType = AsmUtils.toAsmType(factoryName);
        String classType = AsmUtils.toAsmType(className);
        String targetType = AsmUtils.toAsmType(target);


        cw.visit(Opcodes.V1_6, Opcodes.ACC_FINAL + Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                factoryType,
                "L" + CELL_HANDLER_FACTORY_TYPE + "<L" +  targetType + ";>;",
                CELL_HANDLER_FACTORY_TYPE, null);

        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(" +
                    AsmUtils.toTargetTypeDeclaration(Instantiator.class) +
                    AsmUtils.toTargetTypeDeclaration(CsvColumnKey[].class) +
                    AsmUtils.toTargetTypeDeclaration(ParsingContextFactory.class) +
                    AsmUtils.toTargetTypeDeclaration(FieldMapperErrorHandler.class) +
                    ")V",
                    "(" +
                    "L"+ AsmUtils.toAsmType(Instantiator.class) +
                            "<L" + AsmUtils.toAsmType(CsvMapperCellHandler.class) + "<L"+ targetType + ";>;L" + targetType + ";>;" +
                    AsmUtils.toTargetTypeDeclaration(CsvColumnKey[].class) +
                    AsmUtils.toTargetTypeDeclaration(ParsingContextFactory.class) +
                    "L" + AsmUtils.toAsmType(FieldMapperErrorHandler.class) + "<L" + AsmUtils.toAsmType(CsvColumnKey.class) + ";>;" +
                    ")V", null);

            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, CELL_HANDLER_FACTORY_TYPE, "<init>",
                    "(" +
                            AsmUtils.toTargetTypeDeclaration(Instantiator.class) +
                            AsmUtils.toTargetTypeDeclaration(CsvColumnKey[].class) +
                            AsmUtils.toTargetTypeDeclaration(ParsingContextFactory.class) +
                            AsmUtils.toTargetTypeDeclaration(FieldMapperErrorHandler.class) +
                    ")V", false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "newInstance", "(" +
                    AsmUtils.toTargetTypeDeclaration(DelayedCellSetter[].class) +
                    AsmUtils.toTargetTypeDeclaration(CellSetter[].class) +
                    ")" + AsmUtils.toTargetTypeDeclaration(CsvMapperCellHandler.class),
                    "(" +
                    "[L" + DELAYED_CELL_SETTER_TYPE + "<L" +  targetType + ";*>;" +
                    "[L" + CELL_SETTER_TYPE + "<L" +  targetType + ";>;" +
                    ")" +
                    "L" + AsmUtils.toAsmType(CsvMapperCellHandler.class) + "<L" + targetType + ";>;", null);
            mv.visitCode();
            mv.visitTypeInsn(Opcodes.NEW, classType);
            mv.visitInsn(Opcodes.DUP);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, factoryType, "instantiator", AsmUtils.toTargetTypeDeclaration(Instantiator.class));
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, factoryType, "keys", AsmUtils.toTargetTypeDeclaration(CsvColumnKey[].class));
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, factoryType, "parsingContextFactory", AsmUtils.toTargetTypeDeclaration(ParsingContextFactory.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, AsmUtils.toAsmType(ParsingContextFactory.class), "newContext", "()" + AsmUtils.toTargetTypeDeclaration(ParsingContext.class), false);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, factoryType, "fieldErrorHandler", AsmUtils.toTargetTypeDeclaration(FieldMapperErrorHandler.class));
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classType, "<init>",
                    "(" +
                            AsmUtils.toTargetTypeDeclaration(Instantiator.class) +
                            AsmUtils.toTargetTypeDeclaration(DelayedCellSetter[].class) +
                            AsmUtils.toTargetTypeDeclaration(CellSetter[].class) +
                            AsmUtils.toTargetTypeDeclaration(CsvColumnKey[].class) +
                            AsmUtils.toTargetTypeDeclaration(ParsingContext.class) +
                            AsmUtils.toTargetTypeDeclaration(FieldMapperErrorHandler.class) +
                    ")V", false);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(8, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }


    private static abstract class AbstractMethodDispatchShardCallBack<T> implements ShardingHelper.ShardCallBack {
        private final ClassWriter cw;
        private final String classType;
        private final int maxMethodSize;

        public AbstractMethodDispatchShardCallBack(ClassWriter cw, String classType, int maxMethodSize) {
            this.cw = cw;
            this.classType = classType;
            this.maxMethodSize = maxMethodSize;
        }

        @Override
        public void leafDispatch(String suffix, int start, int end) {
            MethodVisitor mv;
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, name() + suffix, signature(), null, null);
            mv.visitCode();

            if (end - start > 0) {
                mv.visitVarInsn(Opcodes.ILOAD, argIndex());
                appendLeafSwitch(mv, start, end);

            }
            if (isVoid()) {
                mv.visitInsn(Opcodes.RETURN);
            } else {
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
            }
            mv.visitMaxs(1, 2);
            mv.visitEnd();
        }

        protected abstract void appendLeafSwitch(MethodVisitor mv, int start, int end);


        @Override
        public void nodeDispatch(String suffix, int divide, int start, int end) {
            MethodVisitor mv;
            mv = cw.visitMethod(Opcodes.ACC_PRIVATE, name() + suffix, signature(), null, null);
            mv.visitCode();
            Label startLabel = new Label();
            mv.visitLabel(startLabel);
            final int powerOfTwo = Integer.numberOfTrailingZeros(divide);

            int sStart = start >> powerOfTwo;
            int sEnd = end >> powerOfTwo;

            int left = end - (sEnd << powerOfTwo);
            if (left > 0) {
                sEnd ++;
            }


            Label[] labels = newLabels(sEnd - sStart);
            Label defaultLabel = new Label();

            mv.visitVarInsn(Opcodes.ILOAD, argIndex());

            int sub = leafStart();
            if (sub != 0) {
                AsmUtils.addIndex(mv, sub);
                mv.visitInsn(Opcodes.ISUB);
            }

            AsmUtils.addIndex(mv, powerOfTwo);
            mv.visitInsn(Opcodes.ISHR);

            mv.visitVarInsn(Opcodes.ISTORE, maxArgIndex() + 1);
            mv.visitVarInsn(Opcodes.ILOAD, maxArgIndex() + 1);
            mv.visitTableSwitchInsn(sStart, sEnd - 1, defaultLabel, labels);

            for (int i = sStart; i < sEnd; i++) {

                int estart = i << powerOfTwo;
                int eend = Math.min(end, (i+1) << powerOfTwo);

                mv.visitLabel(labels[i - sStart]);
                if ( i == start) {
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER}, 0, null);
                } else {
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }

                loadArguments(mv);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classType, name() + (divide / maxMethodSize) + "n" + estart + "t" + eend, signature(), false);

                if (isVoid()) {
                    if (i < (sEnd - 1)) {
                        mv.visitJumpInsn(Opcodes.GOTO, defaultLabel);
                    }
                } else {
                    mv.visitInsn(Opcodes.ARETURN);
                }

            }

            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            if (isVoid()) {
                mv.visitInsn(Opcodes.RETURN);
            } else {
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
            }

            Label endLabel = new Label();
            mv.visitLabel(endLabel);
            appendDebugInfo(mv, startLabel, endLabel);
            mv.visitMaxs(6, 5);
            mv.visitEnd();
        }

        protected int leafStart() {
            return 0;
        }

        protected abstract int maxArgIndex();

        protected void appendDebugInfo(MethodVisitor mv, Label startLabel, Label endLabel) {
        }

        protected abstract void loadArguments(MethodVisitor mv);

        protected abstract int argIndex();

        private boolean isVoid() {
            return signature().endsWith("V");
        }

        protected abstract String name();

        protected abstract String signature();
    }
}
