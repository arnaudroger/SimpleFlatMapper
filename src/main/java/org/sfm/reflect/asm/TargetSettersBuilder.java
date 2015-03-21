package org.sfm.reflect.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static org.objectweb.asm.Opcodes.*;

public class TargetSettersBuilder {

    public static byte[] createTargetSetterClass(String className, int nbDelayedSetter, int nbSetter, Type type) throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;



        cw.visitEnd();

        return AsmUtils.writeClassToFile(className, cw.toByteArray());

    }
}
