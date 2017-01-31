package org.simpleflatmapper.map.asm;

import org.simpleflatmapper.ow2asm.ClassWriter;
import org.simpleflatmapper.ow2asm.FieldVisitor;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.AbstractMapper;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.asm.AsmUtils;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.Method;

import static org.simpleflatmapper.ow2asm.Opcodes.AALOAD;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_BRIDGE;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_FINAL;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_PRIVATE;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_PROTECTED;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_PUBLIC;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_SUPER;
import static org.simpleflatmapper.ow2asm.Opcodes.ACC_SYNTHETIC;
import static org.simpleflatmapper.ow2asm.Opcodes.ALOAD;
import static org.simpleflatmapper.ow2asm.Opcodes.ARETURN;
import static org.simpleflatmapper.ow2asm.Opcodes.ASTORE;
import static org.simpleflatmapper.ow2asm.Opcodes.CHECKCAST;
import static org.simpleflatmapper.ow2asm.Opcodes.DUP;
import static org.simpleflatmapper.ow2asm.Opcodes.GETFIELD;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKESPECIAL;
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKEVIRTUAL;
import static org.simpleflatmapper.ow2asm.Opcodes.NEW;
import static org.simpleflatmapper.ow2asm.Opcodes.POP;
import static org.simpleflatmapper.ow2asm.Opcodes.PUTFIELD;
import static org.simpleflatmapper.ow2asm.Opcodes.RETURN;
import static org.simpleflatmapper.ow2asm.Opcodes.V1_6;
import static org.simpleflatmapper.reflect.asm.AsmUtils.toTargetTypeDeclaration;

public class MapperAsmBuilder {

	private static final String ABSTRACT_MAPPER_TYPE = AsmUtils.toAsmType(AbstractMapper.class);
	private static final String FIELD_MAPPER_TYPE = AsmUtils.toAsmType(FieldMapper.class);
	private static final String INSTANTIATOR_TYPE = AsmUtils.toAsmType(BiInstantiator.class);

    private static final String mappingContextType = AsmUtils.toAsmType(MappingContext.class);

    public static <S,T> byte[] dump (
            final String className,
            final FieldMapper<S, T>[] mappers,
            final FieldMapper<S, T>[] constructorMappers,
            final Class<? super S> sourceClass,
            final Class<T> target
    ) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        final String targetType = AsmUtils.toAsmType(target);
        final String classType = AsmUtils.toAsmType(className);
		cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "L" + ABSTRACT_MAPPER_TYPE + "<" + toTargetTypeDeclaration(targetType) + ">;", ABSTRACT_MAPPER_TYPE, null);

		for(int i = 0; i < mappers.length; i++) {
			declareMapperFields(cw, mappers[i], i);
		}

        for(int i = 0; i < constructorMappers.length; i++) {
            declareConstructorMapperFields(cw, constructorMappers[i], i);
        }

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                            "([L" + FIELD_MAPPER_TYPE + ";"
                            + "[L" + FIELD_MAPPER_TYPE + ";L"
                            + INSTANTIATOR_TYPE + ";)V",
                            "(" +
                                    "[L" + FIELD_MAPPER_TYPE + "<" + toTargetTypeDeclaration(sourceClass)  + toTargetTypeDeclaration(targetType) + ">;" +
                                    "[L" + FIELD_MAPPER_TYPE + "<" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + ">;" +
                                    "L" + INSTANTIATOR_TYPE + "<" + toTargetTypeDeclaration(targetType) + ">;)V", null);

			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESPECIAL, ABSTRACT_MAPPER_TYPE, "<init>",
                    "(L" + INSTANTIATOR_TYPE + ";)V", false);
			
			
			for(int i = 0; i < mappers.length; i++) {
				addFieldMapperInit(mv,  mappers[i], i, classType);
			}


            for(int i = 0; i < constructorMappers.length; i++) {
                addGConstructorFieldMapperInit(mv, constructorMappers[i], i, classType);
            }
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL, "mapFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType) +")V", null, new String[] { "java/lang/Exception" });
			mv.visitCode();

			for(int i = 0; i < mappers.length; i++) {
				generateMappingCall(mv, mappers[i], i, classType, AsmUtils.toAsmType(sourceClass), targetType);
			}
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "mapFields", "(Ljava/lang/Object;Ljava/lang/Object;" + toTargetTypeDeclaration(mappingContextType) + ")V", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(sourceClass));
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(CHECKCAST, targetType);
            mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, classType, "mapFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType)+ ")V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}

        {
            mv = cw.visitMethod(ACC_PROTECTED + ACC_FINAL, "mapToFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType)+  ")V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();

            for(int i = 0; i < constructorMappers.length; i++) {
                generateConstructorMappingCall(mv, constructorMappers[i], i, classType, AsmUtils.toAsmType(sourceClass), targetType);
            }

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, classType, "mapFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType)+  ")V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PROTECTED + ACC_BRIDGE + ACC_SYNTHETIC, "mapToFields", "(Ljava/lang/Object;Ljava/lang/Object;" + toTargetTypeDeclaration(mappingContextType) + ")V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(sourceClass));
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, targetType);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, classType, "mapToFields", "(" +toTargetTypeDeclaration(sourceClass) +  toTargetTypeDeclaration(targetType)+ toTargetTypeDeclaration(mappingContextType) + ")V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
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
            mv.visitInsn(POP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, ABSTRACT_MAPPER_TYPE, "appendToStringBuilder", "(Ljava/lang/StringBuilder;)V", false);

            mv.visitVarInsn(ALOAD, 1);

            for(int i = 0; i < mappers.length; i++) {
                String mapperName =  ", fieldMapper" + i + "=";
                String mapper = String.valueOf(mappers[i]);

                mv.visitLdcInsn(mapperName);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                mv.visitLdcInsn(mapper);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);


            }

            for(int i = 0; i < constructorMappers.length; i++) {
                String mapperName = ", constructorMapper" + i + "=";
                String mapper = String.valueOf(constructorMappers[i]);

                mv.visitLdcInsn(mapperName);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                mv.visitLdcInsn(mapper);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            }



            mv.visitLdcInsn("}");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);


            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }


        cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}

    private static <S, T> void generateMappingCall(MethodVisitor mv,
			FieldMapper<S, T> mapper, int index, String classType, String sourceType, String targetType) {
        generateMappingCall(mv, mapper, index, classType, sourceType, targetType, "fieldMapper");
	}
    private static <S, T> void generateConstructorMappingCall(MethodVisitor mv,
                                                              FieldMapper<S, T> mapper, int index, String classType, String sourceType, String targetType) {
        generateMappingCall(mv, mapper, index, classType, sourceType, targetType, "constructorMapper");
    }

    private static <S, T> void generateMappingCall(MethodVisitor mv, FieldMapper<S, T> mapper, int index, String classType, String sourceType, String targetType, String variablePrefix) {
        if (mapper ==null) return;
        Type mapperClass = AsmUtils.findClosestPublicTypeExposing(mapper.getClass(), FieldMapper.class);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classType, variablePrefix + index, "L" + AsmUtils.toAsmType(mapperClass) + ";");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 3);

        Method m = getMapToMethod(TypeHelper.toClass(mapperClass));
        AsmUtils.invoke(mv, mapperClass, m);
    }

    private static Method getMapToMethod(Class<?> aClass) {
        Method m = null;
        for(Method p : aClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(p.getModifiers())
                    && p.getName().equals("mapTo")
                    && (p.getParameterTypes() != null && p.getParameterTypes().length == 3)) {
                // crude way of selecting non bridge method
                if (m == null || p.getModifiers() < m.getModifiers()) {
                    m = p;
                }
            }
        }
        return m;
    }


    private static <S, T> void addFieldMapperInit(MethodVisitor mv,
                                                  FieldMapper<S, T> mapper, int index, String classType) {
		if (mapper == null) return;
		Type mapperClass = AsmUtils.findClosestPublicTypeExposing(mapper.getClass(), FieldMapper.class);
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		AsmUtils.addIndex(mv, index);
		mv.visitInsn(AALOAD);
		mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(mapperClass));
		mv.visitFieldInsn(PUTFIELD, classType, "fieldMapper" + index, toTargetTypeDeclaration(AsmUtils.toAsmType(mapperClass)));

	}

    private static <S, T> void addGConstructorFieldMapperInit(MethodVisitor mv,
                                                              FieldMapper<S, T> mapper, int index, String classType) {
        if (mapper == null) return;
        Type mapperClass = AsmUtils.findClosestPublicTypeExposing(mapper.getClass(), FieldMapper.class);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        AsmUtils.addIndex(mv, index);
        mv.visitInsn(AALOAD);
        mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(mapperClass));
        mv.visitFieldInsn(PUTFIELD, classType, "constructorMapper" + index, toTargetTypeDeclaration(AsmUtils.toAsmType(mapperClass)));

    }

	private static <S, T> void declareMapperFields(ClassWriter cw,
			FieldMapper<S, T> mapper, int index) {
		if (mapper == null) 
			return;
		
		FieldVisitor fv;

        Type mapperClass = AsmUtils.findClosestPublicTypeExposing(mapper.getClass(), FieldMapper.class);
        fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "fieldMapper" + index, toTargetTypeDeclaration(AsmUtils.toAsmType(mapperClass)), toTargetTypeDeclaration(AsmUtils.toGenericAsmType(mapperClass)), null);
		fv.visitEnd();

	}

    private static <S, T> void declareConstructorMapperFields(ClassWriter cw,
                                                   FieldMapper<S, T> mapper, int index) {
        if (mapper == null)
            return;

        FieldVisitor fv;
        Type mapperClass = AsmUtils.findClosestPublicTypeExposing(mapper.getClass(), FieldMapper.class);

        fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "constructorMapper" + index, toTargetTypeDeclaration(AsmUtils.toAsmType(mapperClass)), toTargetTypeDeclaration(AsmUtils.toGenericAsmType(mapperClass)), null);
        fv.visitEnd();

    }



}
