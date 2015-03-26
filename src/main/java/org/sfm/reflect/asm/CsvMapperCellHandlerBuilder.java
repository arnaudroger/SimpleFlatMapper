package org.sfm.reflect.asm;

import org.objectweb.asm.*;
import org.sfm.csv.CsvColumnKey;
import org.sfm.csv.impl.*;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

import static org.objectweb.asm.Opcodes.*;

public class CsvMapperCellHandlerBuilder {

    public static final String DELAYED_CELL_SETTER_TYPE = AsmUtils.toType(DelayedCellSetter.class);
    public static final String CELL_SETTER_TYPE = AsmUtils.toType(CellSetter.class);
    public static final String CSV_CELL_MAPPER_TYPE = AsmUtils.toType(CsvMapperCellHandler.class);
    public static final String CELL_HANDLER_FACTORY_TYPE = AsmUtils.toType(CsvMapperCellHandlerFactory.class);

    public static <T> byte[] createTargetSetterClass(String className,
                                                 DelayedCellSetterFactory<T, ?>[] delayedCellSetters,
            CellSetter<T>[] setters, Type type, boolean ignoreException, int maxMethodSize) throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        FieldVisitor fv;
        MethodVisitor mv;

        final String targetType = AsmUtils.toType(type);
        final String classType = AsmUtils.toType(className);

        cw.visit(V1_6, ACC_FINAL +ACC_PUBLIC + ACC_SUPER, classType,
                "L" + CSV_CELL_MAPPER_TYPE + "<L" + targetType + ";>;", CSV_CELL_MAPPER_TYPE, null);


        // declare fields
        for(int i = 0; i < delayedCellSetters.length; i++) {
            if (delayedCellSetters[i] != null) {
                fv = cw.visitField(ACC_PROTECTED + ACC_FINAL, "delayedCellSetter" + i,
                        AsmUtils.toDeclaredLType(DELAYED_CELL_SETTER_TYPE),
                        "L" + DELAYED_CELL_SETTER_TYPE + "<L" + targetType + ";*>;", null);
                fv.visitEnd();
            }
        }

        for(int i = 0; i < setters.length; i++) {
            if (setters[i] != null) {
                fv = cw.visitField(ACC_PROTECTED + ACC_FINAL, "setter" + i,
                        AsmUtils.toDeclaredLType(CELL_SETTER_TYPE),
                        "L" + CELL_SETTER_TYPE + "<L" + targetType + ";>;", null);
                fv.visitEnd();
            }
        }
        appendInit(delayedCellSetters, setters, cw, targetType, classType);


        appendDelayedCellValue(delayedCellSetters, ignoreException, cw, classType);
        append_delayedCellValue(delayedCellSetters, cw, classType, "", maxMethodSize);

        appendCellValue(setters, ignoreException, cw, classType);
        append_cellValue(delayedCellSetters, setters, cw, classType, "", maxMethodSize);

        appendApplyDelayedSetter(delayedCellSetters, ignoreException, cw, classType, "", maxMethodSize);
        appendApplyDelayedCellSetterN(delayedCellSetters, cw, classType);

        appendGetDelayedCellSetter(delayedCellSetters, cw, targetType, classType, "", maxMethodSize);
        appendPeekDelayedCellSetterValue(delayedCellSetters, cw, classType, "", maxMethodSize);

        cw.visitEnd();


        return AsmUtils.writeClassToFile(className, cw.toByteArray());

    }

    private static <T> void appendPeekDelayedCellSetterValue(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, ClassWriter cw, String classType, String suffix, int maxMethodSize) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "peekDelayedCellSetterValue", "(L" + AsmUtils.toType(CsvColumnKey.class) + ";)Ljava/lang/Object;", null, null);
        mv.visitCode();

        if (delayedCellSetters.length != 0) {
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, AsmUtils.toType(CsvColumnKey.class), "getIndex", "()I", false);

            final int switchStart = 0;
            final int switchEnd = delayedCellSetters.length;

            appendPeekDelayedCellSetterValueSwitch(delayedCellSetters, classType, mv, switchStart, switchEnd);
        }
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 2);
        mv.visitEnd();
    }

    private static <T> void appendPeekDelayedCellSetterValueSwitch(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, String classType, MethodVisitor mv, int switchStart, int switchEnd) {
        Label defaultLabel = new Label();
        Label[] labels = new Label[switchStart - switchEnd];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label();
        }
        mv.visitTableSwitchInsn(switchStart, switchEnd - 1, defaultLabel, labels);

        for (int i = 0; i < delayedCellSetters.length; i++) {
            mv.visitLabel(labels[i -switchStart]);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            if (delayedCellSetters[i] != null) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "delayedCellSetter" + i, "L" + DELAYED_CELL_SETTER_TYPE + ";");
                mv.visitMethodInsn(INVOKEINTERFACE, DELAYED_CELL_SETTER_TYPE, "peekValue", "()Ljava/lang/Object;", true);
                mv.visitInsn(ARETURN);
            } else if (i < (delayedCellSetters.length - 1)) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }
        mv.visitLabel(defaultLabel);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }

    private static <T> void append_cellValue(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, CellSetter<T>[] setters, ClassWriter cw, String classType, String suffix, int maxMethodSize) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE, "_cellValue", "([CIII)V", null, new String[]{"java/lang/Exception"});
        mv.visitCode();

        if (setters.length != 0) {

            int switchStart = 0;
            int switchEnd = setters.length;

            appendCellValueSwitch(delayedCellSetters, setters, classType, mv, switchStart, switchEnd);
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(6, 5);
        mv.visitEnd();
    }

    private static <T> void appendCellValueSwitch(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, CellSetter<T>[] setters, String classType, MethodVisitor mv, int switchStart, int switchEnd) {
        mv.visitVarInsn(ILOAD, 4);
        Label defaultLabel = new Label();
        Label[] labels = new Label[switchEnd - switchStart];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label();
        }
        mv.visitTableSwitchInsn(delayedCellSetters.length + switchStart, delayedCellSetters.length + switchEnd - 1, defaultLabel, labels);

        for (int i = switchStart; i < switchEnd; i++) {

            mv.visitLabel(labels[i - switchStart]);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            if (setters[i] != null) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "setter" + i, "L" + CELL_SETTER_TYPE + ";");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "currentInstance", "Ljava/lang/Object;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ILOAD, 2);
                mv.visitVarInsn(ILOAD, 3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "parsingContext", AsmUtils.toDeclaredLType(ParsingContext.class));
                mv.visitMethodInsn(INVOKEINTERFACE, CELL_SETTER_TYPE, "set", "(Ljava/lang/Object;[CIIL" + AsmUtils.toType(ParsingContext.class) + ";)V", true);
            }
            if (i < (switchEnd - 1)) {
                mv.visitJumpInsn(GOTO, defaultLabel);
            }
        }
        mv.visitLabel(defaultLabel);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }

    private static <T> void appendApplyDelayedCellSetterN(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, ClassWriter cw, String classType) {
        MethodVisitor mv;
        for(int i = 0; i < delayedCellSetters.length; i++) {
            if (delayedCellSetters[i] != null) {
                mv = cw.visitMethod(ACC_PRIVATE, "applyDelayedCellSetter" + i, "()V", null, new String[]{"java/lang/Exception"});
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "delayedCellSetter" + i, "L" + DELAYED_CELL_SETTER_TYPE + ";");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "currentInstance", "Ljava/lang/Object;");
                mv.visitMethodInsn(INVOKEINTERFACE, DELAYED_CELL_SETTER_TYPE, "set", "(Ljava/lang/Object;)V", true);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 1);
                mv.visitEnd();
            }
        }
    }

    private static <T> void appendApplyDelayedSetter(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, boolean ignoreException, ClassWriter cw, String classType, String suffix, int maxMethodSize) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "applyDelayedSetters", "()V", null, null);
        mv.visitCode();

        if (delayedCellSetters.length != 0) {

            Label[] labels = new Label[(3 * getNbNonNullSettersWithSetters(delayedCellSetters)) + 1];

            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }

            for (int i = 0, j = 0; i < delayedCellSetters.length; i++) {
                if (delayedCellSetters[i] != null && delayedCellSetters[i].hasSetter()) {
                    mv.visitTryCatchBlock(labels[j], labels[j + 1], labels[j + 2], "java/lang/Exception");
                    j+= 3;
                }
            }


            for (int i = 0, j = 0; i < delayedCellSetters.length; i++) {
                if (delayedCellSetters[i] != null && delayedCellSetters[i].hasSetter()) {
                    if (ignoreException) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKESPECIAL, classType, "applyDelayedCellSetter" + i, "()V", false);
                    } else {
                        mv.visitLabel(labels[j]);
                        if (j > 0) {
                            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                        }
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKESPECIAL, classType, "applyDelayedCellSetter" + i, "()V", false);
                        mv.visitLabel(labels[j + 1]);
                        mv.visitJumpInsn(GOTO, labels[j + 3]);
                        mv.visitLabel(labels[j + 2]);
                        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
                        mv.visitVarInsn(ASTORE, 1);
                        mv.visitVarInsn(ALOAD, 0);
                        AsmUtils.addIndex(mv, i);
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitMethodInsn(INVOKEVIRTUAL, classType, "fieldError", "(ILjava/lang/Exception;)V", false);

                    }

                    j+= 3;
                }
            }
            mv.visitLabel(labels[labels.length - 1]);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private static <T> void appendCellValue(CellSetter<T>[] setters, boolean ignoreException, ClassWriter cw, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "cellValue", "([CIII)V", null, null);
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
                mv.visitJumpInsn(GOTO, l3);
                mv.visitLabel(l2);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
                mv.visitVarInsn(ASTORE, 5);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 4);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKEVIRTUAL, classType, "fieldError", "(ILjava/lang/Exception;)V", false);
                mv.visitLabel(l3);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(5, 6);
        mv.visitEnd();
    }

    private static <T> void appendDelayedCellValue(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, boolean ignoreException, ClassWriter cw, String classType) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "delayedCellValue", "([CIII)V", null, null);
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
                mv.visitJumpInsn(GOTO, l3);
                mv.visitLabel(l2);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
                mv.visitVarInsn(ASTORE, 5);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 4);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitMethodInsn(INVOKEVIRTUAL, classType, "fieldError", "(ILjava/lang/Exception;)V", false);
                mv.visitLabel(l3);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(5, 6);
        mv.visitEnd();
    }

    private static <T> void appendInit(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, CellSetter<T>[] setters, ClassWriter cw, String targetType, String classType) {
        MethodVisitor mv;// constructor
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" +
                    AsmUtils.toDeclaredLType(Instantiator.class) +
                    AsmUtils.toDeclaredLType(DelayedCellSetter[].class) +
                    AsmUtils.toDeclaredLType(CellSetter[].class) +
                    AsmUtils.toDeclaredLType(CsvColumnKey[].class) +
                    AsmUtils.toDeclaredLType(ParsingContext.class) +
                    AsmUtils.toDeclaredLType(FieldMapperErrorHandler.class) +
                    ")V",
                    "(" +
                    "L" + AsmUtils.toType(Instantiator.class) +"<L" + AsmUtils.toType(CsvMapperCellHandler.class)+ "<L" + targetType + ";>;L"+ targetType + ";>;" +
                    "[L" + DELAYED_CELL_SETTER_TYPE +  "<L" + targetType + ";*>;" +
                    "[L" + CELL_SETTER_TYPE + "<L" + targetType + ";>;" +
                    AsmUtils.toDeclaredLType(CsvColumnKey[].class) +
                    AsmUtils.toDeclaredLType(ParsingContext.class) +
                    "L" + AsmUtils.toType(FieldMapperErrorHandler.class) + "<L" + AsmUtils.toType(CsvColumnKey.class) + ";>;" +
                    ")V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitMethodInsn(INVOKESPECIAL, CSV_CELL_MAPPER_TYPE, "<init>",
                    "(" +
                    AsmUtils.toDeclaredLType(Instantiator.class) +
                    AsmUtils.toDeclaredLType(CsvColumnKey[].class) +
                    "I" +
                    "I" +
                    AsmUtils.toDeclaredLType(ParsingContext.class) +
                    AsmUtils.toDeclaredLType(FieldMapperErrorHandler.class) +
                    ")V", false);

            for(int i = 0; i < delayedCellSetters.length; i++) {
                if (delayedCellSetters[i] != null) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 2);
                    AsmUtils.addIndex(mv, i);
                    mv.visitInsn(AALOAD);
                    mv.visitFieldInsn(PUTFIELD,
                            classType,
                            "delayedCellSetter" + i,
                            AsmUtils.toDeclaredLType(DELAYED_CELL_SETTER_TYPE));
                }
            }

            for(int i = 0; i < setters.length; i++) {
                if (setters[i] != null) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 3);
                    AsmUtils.addIndex(mv, i);
                    mv.visitInsn(AALOAD);
                    mv.visitFieldInsn(PUTFIELD,
                            classType,
                            "setter" + i ,
                            AsmUtils.toDeclaredLType(CELL_SETTER_TYPE));
                }
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(7, 7);
            mv.visitEnd();
        }
    }

    private static <T> void append_delayedCellValue(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, ClassWriter cw, String classType, String suffix, int limit) {



        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE, "_delayedCellValue" + suffix, "([CIII)V", null, new String[]{"java/lang/Exception"});
        mv.visitCode();

        if (delayedCellSetters.length != 0) {
            final int switchStart = 0;
            final int switchEnd = delayedCellSetters.length;

            appendDelayedCellValueSwitch(delayedCellSetters, classType, mv, switchStart, switchEnd);
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(5, 5);
        mv.visitEnd();
    }

    private static <T> void appendDelayedCellValueSwitch(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, String classType, MethodVisitor mv, int switchStart, int switchEnd) {
        mv.visitVarInsn(ILOAD, 4);

        Label defaultLabel = new Label();
        Label[] labels = new Label[switchEnd - switchStart];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label();
        }
        mv.visitTableSwitchInsn(switchStart, switchEnd - 1, defaultLabel, labels);

        for (int i = switchStart; i < switchEnd; i++) {
            mv.visitLabel(labels[i - switchStart]);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            if (delayedCellSetters[i] != null) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "delayedCellSetter" + i, "L" + DELAYED_CELL_SETTER_TYPE + ";");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ILOAD, 2);
                mv.visitVarInsn(ILOAD, 3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "parsingContext", AsmUtils.toDeclaredLType(ParsingContext.class));
                mv.visitMethodInsn(INVOKEINTERFACE, DELAYED_CELL_SETTER_TYPE, "set", "([CIIL" + AsmUtils.toType(ParsingContext.class) + ";)V", true);
            }
            if (i < (switchEnd - 1)) {
                mv.visitJumpInsn(GOTO, defaultLabel);
            }
        }
        mv.visitLabel(defaultLabel);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }

    private static <T> void appendGetDelayedCellSetter(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, ClassWriter cw, String targetType, String classType, String suffix, int maxMethodSize) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "getDelayedCellSetter", "(I)L" +  DELAYED_CELL_SETTER_TYPE + ";",
                "(I)L" + DELAYED_CELL_SETTER_TYPE + "<L" + targetType + ";*>;", null);
        mv.visitCode();

        if (delayedCellSetters.length != 0) {
            final int switchStart = 0;
            final int switchEnd = delayedCellSetters.length;
            appendGetDelayedCellSetterSwitch(delayedCellSetters, classType, mv, switchStart, switchEnd);
        }
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);

        mv.visitMaxs(1, 2);
        mv.visitEnd();
    }

    private static <T> void appendGetDelayedCellSetterSwitch(DelayedCellSetterFactory<T, ?>[] delayedCellSetters, String classType, MethodVisitor mv, int switchStart, int switchEnd) {
        mv.visitVarInsn(ILOAD, 1);
        Label defaultLabel = new Label();
        Label[] labels = new Label[switchEnd - switchStart];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label();
        }
        mv.visitTableSwitchInsn(switchStart, switchEnd - 1, defaultLabel, labels);

        for (int i = switchStart; i < switchEnd; i++) {
            mv.visitLabel(labels[i - switchStart]);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            if (delayedCellSetters != null) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, classType, "delayedCellSetter" + i, "L" + DELAYED_CELL_SETTER_TYPE + ";");
            } else {
                mv.visitVarInsn(ALOAD, ACONST_NULL);
            }
            mv.visitInsn(ARETURN);
        }

        mv.visitLabel(defaultLabel);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }

    private static void callCellValue(MethodVisitor mv, String classType) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitMethodInsn(INVOKESPECIAL, classType, "_cellValue", "([CIII)V", false);
    }

    private static void callDelayedCellValue(MethodVisitor mv, String classType) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitMethodInsn(INVOKESPECIAL, classType, "_delayedCellValue", "([CIII)V", false);
    }

    private static <T> int getNbNonNullSettersWithSetters(DelayedCellSetterFactory<T, ?>[] delayedCellSetters) {
        int n = 0;
        for(int i = 0; i < delayedCellSetters.length; i++) {
            if (delayedCellSetters[i] != null && delayedCellSetters[i].hasSetter()) n++;
        }
        return n;
    }

    private static <T> int getNbNonNullSetters(DelayedCellSetterFactory<T, ?>[] delayedCellSetters) {
        int n = 0;
        for(int i = 0; i < delayedCellSetters.length; i++) {
            if (delayedCellSetters[i] != null) n++;
        }
        return n;
    }


    public static byte[] createTargetSetterFactory(String factoryName, String className, Type target) throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        String factoryType = AsmUtils.toType(factoryName);
        String classType = AsmUtils.toType(className);
        String targetType = AsmUtils.toType(target);


        cw.visit(V1_6, ACC_FINAL + ACC_PUBLIC + ACC_SUPER,
                factoryType,
                "L" + CELL_HANDLER_FACTORY_TYPE + "<L" +  targetType + ";>;",
                CELL_HANDLER_FACTORY_TYPE, null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" +
                    AsmUtils.toDeclaredLType(Instantiator.class) +
                    AsmUtils.toDeclaredLType(CsvColumnKey[].class) +
                    AsmUtils.toDeclaredLType(ParsingContextFactory.class) +
                    AsmUtils.toDeclaredLType(FieldMapperErrorHandler.class) +
                    ")V",
                    "(" +
                    "L"+ AsmUtils.toType(Instantiator.class) +
                            "<L" + AsmUtils.toType(CsvMapperCellHandler.class) + "<L"+ targetType + ";>;L" + targetType + ";>;" +
                    AsmUtils.toDeclaredLType(CsvColumnKey[].class) +
                    AsmUtils.toDeclaredLType(ParsingContextFactory.class) +
                    "L" + AsmUtils.toType(FieldMapperErrorHandler.class) + "<L" + AsmUtils.toType(CsvColumnKey.class) + ";>;" +
                    ")V", null);

            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKESPECIAL, CELL_HANDLER_FACTORY_TYPE, "<init>",
                    "(" +
                            AsmUtils.toDeclaredLType(Instantiator.class) +
                            AsmUtils.toDeclaredLType(CsvColumnKey[].class) +
                            AsmUtils.toDeclaredLType(ParsingContextFactory.class) +
                            AsmUtils.toDeclaredLType(FieldMapperErrorHandler.class) +
                    ")V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(" +
                    AsmUtils.toDeclaredLType(DelayedCellSetter[].class) +
                    AsmUtils.toDeclaredLType(CellSetter[].class) +
                    ")" + AsmUtils.toDeclaredLType(CsvMapperCellHandler.class),
                    "(" +
                    "[L" + DELAYED_CELL_SETTER_TYPE + "<L" +  targetType + ";*>;" +
                    "[L" + CELL_SETTER_TYPE + "<L" +  targetType + ";>;" +
                    ")" +
                    "L" + AsmUtils.toType(CsvMapperCellHandler.class) + "<L" + targetType + ";>;", null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, classType);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, factoryType, "instantiator", AsmUtils.toDeclaredLType(Instantiator.class));
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, factoryType, "keys", AsmUtils.toDeclaredLType(CsvColumnKey[].class));
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, factoryType, "parsingContextFactory", AsmUtils.toDeclaredLType(ParsingContextFactory.class));
            mv.visitMethodInsn(INVOKEVIRTUAL, AsmUtils.toType(ParsingContextFactory.class), "newContext", "()" + AsmUtils.toDeclaredLType(ParsingContext.class), false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, factoryType, "fieldErrorHandler", AsmUtils.toDeclaredLType(FieldMapperErrorHandler.class));
            mv.visitMethodInsn(INVOKESPECIAL, classType, "<init>",
                    "(" +
                            AsmUtils.toDeclaredLType(Instantiator.class) +
                            AsmUtils.toDeclaredLType(DelayedCellSetter[].class) +
                            AsmUtils.toDeclaredLType(CellSetter[].class) +
                            AsmUtils.toDeclaredLType(CsvColumnKey[].class) +
                            AsmUtils.toDeclaredLType(ParsingContext.class) +
                            AsmUtils.toDeclaredLType(FieldMapperErrorHandler.class) +
                    ")V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(8, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
