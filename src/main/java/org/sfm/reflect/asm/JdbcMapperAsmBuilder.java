package org.sfm.reflect.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.sfm.jdbc.impl.AbstractJdbcMapperImpl;
import org.sfm.map.MappingContext;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.FieldMapper;
import org.sfm.reflect.Instantiator;

import java.sql.ResultSet;

import static org.objectweb.asm.Opcodes.*;

public class JdbcMapperAsmBuilder {

	private static final String ABSTRACT_JDBC_MAPPER_IMPL_TYPE = AsmUtils.toType(AbstractJdbcMapperImpl.class);
	private static final String FIELD_MAPPER_TYPE = AsmUtils.toType(FieldMapper.class);
	private static final String INSTANTIATOR_TYPE = AsmUtils.toType(Instantiator.class);
	private static final String ROW_HANDLER_ERROR_HANDLER_TYPE = AsmUtils.toType(RowHandlerErrorHandler.class);

    private static final String mappingContextType = AsmUtils.toType(MappingContext.class);

    public static <S,T> byte[] dump (final String className, final FieldMapper<S, T>[] mappers, final FieldMapper<S, T>[] constructorMappers, final Class<T> target) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        Class<ResultSet> sourceClass = ResultSet.class;
        final String targetType = AsmUtils.toType(target);
        final String sourceType = AsmUtils.toType(sourceClass);
        final String classType = AsmUtils.toType(className);
		cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, classType, "L" + ABSTRACT_JDBC_MAPPER_IMPL_TYPE + "<" + toTargetTypeDeclaration(targetType) + ">;", ABSTRACT_JDBC_MAPPER_IMPL_TYPE, null);

		for(int i = 0; i < mappers.length; i++) {
			declareMapperFields(cw, mappers[i], i);
		}

        for(int i = 0; i < constructorMappers.length; i++) {
            declareConstructorMapperFields(cw, constructorMappers[i], i);
        }

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([L" + FIELD_MAPPER_TYPE + ";" + "[L" + FIELD_MAPPER_TYPE + ";L" + INSTANTIATOR_TYPE + ";L" + ROW_HANDLER_ERROR_HANDLER_TYPE + ";)V",
                    "([L" + FIELD_MAPPER_TYPE +"<L" + sourceType + ";" + toTargetTypeDeclaration(targetType) + ">;[L" + FIELD_MAPPER_TYPE +"<L" + sourceType + ";" + toTargetTypeDeclaration(targetType) + ">;L" + AsmUtils.toType(Instantiator.class) + "<" + toTargetTypeDeclaration(targetType) + ">;L" + AsmUtils.toType(RowHandlerErrorHandler.class) + ";)V", null);

			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 4);
			mv.visitMethodInsn(INVOKESPECIAL, ABSTRACT_JDBC_MAPPER_IMPL_TYPE, "<init>", "(L" + INSTANTIATOR_TYPE + ";L" + ROW_HANDLER_ERROR_HANDLER_TYPE +  ";)V", false);
			
			
			for(int i = 0; i < mappers.length; i++) {
				addGetterSetterInit(mv,  mappers[i], i, classType); 
			}


            for(int i = 0; i < constructorMappers.length; i++) {
                addGConstructorGetterSetterInit(mv, constructorMappers[i], i, classType);
            }
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL, "mapFields", "(L" + sourceType + ";" + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType) +")V", null, new String[] { "java/lang/Exception" });
			mv.visitCode();

			for(int i = 0; i < mappers.length; i++) {
				generateMappingCall(mv, mappers[i], i, classType, sourceType, targetType);
			}
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "mapFields", "(Ljava/lang/Object;Ljava/lang/Object;" + toTargetTypeDeclaration(mappingContextType) + ")V", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, sourceType);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(CHECKCAST, targetType);
            mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, classType, "mapFields", "(L" + sourceType + ";" + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType)+ ")V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}

        {
            mv = cw.visitMethod(ACC_PROTECTED + ACC_FINAL, "mapToFields", "(L" + sourceType + ";" + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType)+  ")V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();

            for(int i = 0; i < constructorMappers.length; i++) {
                generateConstructorMappingCall(mv, constructorMappers[i], i, classType, sourceType, targetType);
            }

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, classType, "mapFields", "(L" + sourceType + ";" + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType)+  ")V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PROTECTED + ACC_BRIDGE + ACC_SYNTHETIC, "mapToFields", "(Ljava/lang/Object;Ljava/lang/Object;" + toTargetTypeDeclaration(mappingContextType) + ")V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, sourceType);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, targetType);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, classType, "mapToFields", "(L" + sourceType + ";" + toTargetTypeDeclaration(targetType)+ toTargetTypeDeclaration(mappingContextType) + ")V", false);
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
            mv.visitMethodInsn(INVOKESPECIAL, "org/sfm/map/impl/AbstractMapperImpl", "appendToStringBuilder", "(Ljava/lang/StringBuilder;)V", false);

            mv.visitVarInsn(ALOAD, 1);

            for(int i = 0; i < mappers.length; i++) {
                String mapperName =  ", mapper" + i + "=";
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

    private static String toTargetTypeDeclaration(String targetType) {
        if (targetType.startsWith("[")) {
            return targetType;
        } else {
            return "L" + targetType+ ";";
        }
    }

    private static <S, T> void generateMappingCall(MethodVisitor mv,
			FieldMapper<S, T> mapper, int index, String classType, String sourceType, String targetType) {
        generateMappingCall(mv, mapper, index, classType, sourceType, targetType, "mapper");
	}
    private static <S, T> void generateConstructorMappingCall(MethodVisitor mv,
                                                              FieldMapper<S, T> mapper, int index, String classType, String sourceType, String targetType) {
        generateMappingCall(mv, mapper, index, classType, sourceType, targetType, "constructorMapper");
    }

    private static <S, T> void generateMappingCall(MethodVisitor mv, FieldMapper<S, T> mapper, int index, String classType, String sourceType, String targetType, String variablePrefix) {
        if (mapper ==null) return;
        Class<?> mapperClass = AsmUtils.getPublicOrInterfaceClass(mapper.getClass());
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classType, variablePrefix + index, "L" + AsmUtils.toType(mapperClass) + ";");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 3);
        if (AsmUtils.isStillGeneric(mapperClass)) {
            AsmUtils.invoke(mv, mapperClass, "mapTo", "(Ljava/lang/Object;Ljava/lang/Object;" + toTargetTypeDeclaration(mappingContextType) + ")V");
        } else {
            AsmUtils.invoke(mv, mapperClass, "mapTo", "(L" + sourceType + ";" + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType) + ")V");
        }
    }


	private static <S, T> void addGetterSetterInit(MethodVisitor mv,
			FieldMapper<S, T> mapper, int index, String classType) {
		if (mapper == null) return;
		Class<?> mapperClass = mapper.getClass();
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		addIndex(mv, index);
		mv.visitInsn(AALOAD);
		mv.visitTypeInsn(CHECKCAST, AsmUtils.toType(mapperClass));
		mv.visitFieldInsn(PUTFIELD, classType, "mapper" + index, toTargetTypeDeclaration(AsmUtils.toType(mapperClass)));

	}

    private static <S, T> void addGConstructorGetterSetterInit(MethodVisitor mv,
                                                   FieldMapper<S, T> mapper, int index, String classType) {
        if (mapper == null) return;
        Class<?> mapperClass = mapper.getClass();

        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 2);
        addIndex(mv, index);
        mv.visitInsn(AALOAD);
        mv.visitTypeInsn(CHECKCAST, AsmUtils.toType(mapperClass));
        mv.visitFieldInsn(PUTFIELD, classType, "constructorMapper" + index, toTargetTypeDeclaration(AsmUtils.toType(mapperClass)));

    }

	private static <S, T> void declareMapperFields(ClassWriter cw,
			FieldMapper<S, T> mapper, int index) {
		if (mapper == null) 
			return;
		
		FieldVisitor fv;
		Class<?> mapperClass = AsmUtils.getPublicOrInterfaceClass(mapper.getClass());
		
		fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "mapper" + index, toTargetTypeDeclaration(AsmUtils.toType(mapperClass)), toTargetTypeDeclaration(AsmUtils.toTypeWithParam(mapperClass)), null);
		fv.visitEnd();

	}

    private static <S, T> void declareConstructorMapperFields(ClassWriter cw,
                                                   FieldMapper<S, T> mapper, int index) {
        if (mapper == null)
            return;

        FieldVisitor fv;
        Class<?> mapperClass = AsmUtils.getPublicOrInterfaceClass(mapper.getClass());

        fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "constructorMapper" + index, toTargetTypeDeclaration(AsmUtils.toType(mapperClass)), toTargetTypeDeclaration(AsmUtils.toTypeWithParam(mapperClass)), null);
        fv.visitEnd();

    }


    private static void addIndex(MethodVisitor mv, int i) {
		switch(i) {
		case 0:
			mv.visitInsn(ICONST_0);
			return;
		case 1:
			mv.visitInsn(ICONST_1);
			return;
		case 2:
			mv.visitInsn(ICONST_2);
			return;
		case 3:
			mv.visitInsn(ICONST_3);
			return;
		case 4:
			mv.visitInsn(ICONST_4);
			return;
		case 5:
			mv.visitInsn(ICONST_5);
			return;
		default:
			mv.visitIntInsn(BIPUSH, i);
		}
	}
}
