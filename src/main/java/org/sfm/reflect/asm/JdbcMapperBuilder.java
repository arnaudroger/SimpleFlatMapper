package org.sfm.reflect.asm;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import java.sql.ResultSet;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sfm.map.FieldMapper;
import org.sfm.reflect.Instantiator;

public class JdbcMapperBuilder {
	public static <S,T> byte[] dump (final String className, final FieldMapper<S, T>[] mappers, final Instantiator<ResultSet, T> instantiator, final Class<T> target) throws Exception {

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		MethodVisitor mv;
		
		Class<ResultSet> sourceClass = ResultSet.class;
		final String targetType = AsmUtils.toType(target);
		final String sourceType = AsmUtils.toType(sourceClass);
		final String classType = AsmUtils.toType(className);
		final Class<?> instantiatorClass = AsmUtils.getPublicOrInterfaceClass(instantiator.getClass());
		final String instantiatorType = AsmUtils.toType(instantiatorClass);
		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER + ACC_FINAL, classType, "Ljava/lang/Object;Lorg/sfm/jdbc/JdbcMapper<L" + targetType + ";>;", "java/lang/Object", new String[] { "org/sfm/jdbc/JdbcMapper" });


		for(int i = 0; i < mappers.length; i++) {
			declareMapperFields(cw,  mappers[i], targetType, sourceType, i);  
		}
		FieldVisitor fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "instantiator", "L" + instantiatorType + ";", "L" + AsmUtils.toTypeWithParam(instantiator.getClass()) + ";", null);
		fv.visitEnd();
		
		fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "errorHandler", "Lorg/sfm/jdbc/JdbcMapperErrorHandler;", null, null);
		fv.visitEnd();

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Lorg/sfm/map/FieldMapper;Lorg/sfm/reflect/Instantiator;Lorg/sfm/jdbc/JdbcMapperErrorHandler;)V", "([Lorg/sfm/map/FieldMapper<L" + sourceType + ";L" + targetType + ";>;Lorg/sfm/reflect/Instantiator<L" + targetType + ";>;Lorg/sfm/jdbc/JdbcMapperErrorHandler;)V", null);

			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			
			
			for(int i = 0; i < mappers.length; i++) {
				addGetterSetterInit(mv,  mappers[i], i, classType); 
			}
			
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(CHECKCAST, instantiatorType);
			mv.visitFieldInsn(PUTFIELD, classType, "instantiator", "L" + instantiatorType+ ";");
			
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitFieldInsn(PUTFIELD, classType, "errorHandler", "Lorg/sfm/jdbc/JdbcMapperErrorHandler;");
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "map", "(L" + sourceType + ";)L" + targetType + ";", null, new String[] { "org/sfm/map/MappingException" });
			mv.visitCode();
			Label l0 = new Label();
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, classType, "instantiator", "L" + instantiatorType + ";");
			mv.visitVarInsn(ALOAD, 1);
			if (AsmUtils.isStillGeneric(instantiatorClass)) {
				AsmUtils.invoke(mv, instantiatorClass, "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;");
				mv.visitTypeInsn(CHECKCAST, targetType);
			} else {
				AsmUtils.invoke(mv, instantiatorClass, "newInstance",  "(L" + sourceType + ";)L" + AsmUtils.toType(instantiatorClass.getMethod("newInstance", sourceClass).getReturnType()) + ";");
			}
			mv.visitVarInsn(ASTORE, 2);
			mv.visitLabel(l1);
			Label l3 = new Label();
			mv.visitJumpInsn(GOTO, l3);
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
			mv.visitVarInsn(ASTORE, 3);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitTypeInsn(NEW, "org/sfm/map/InstantiationMappingException");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "getMessage", "()Ljava/lang/String;", false);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKESPECIAL, "org/sfm/map/InstantiationMappingException", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
			mv.visitInsn(ATHROW);
			mv.visitLabel(l3);
			mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {targetType}, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKESPECIAL, classType, "mapFields", "(L" + sourceType + ";L" + targetType + ";)V", false);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(4, 4);
			mv.visitEnd();
		}
		
		{
			mv = cw.visitMethod(ACC_PRIVATE, "mapFields", "(L" + sourceType + ";L" + targetType + ";)V", null, null);
			mv.visitCode();

			for(int i = 0; i < mappers.length; i++) {
				generateMappingCall(mv, mappers[i], i, classType, sourceType, targetType);
			}
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
		}
		
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "map", "(Ljava/lang/Object;)Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, sourceType);
			mv.visitMethodInsn(INVOKEVIRTUAL, classType, "map", "(L" + sourceType + ";)L" + targetType + ";", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		
		
		{
			mv = cw.visitMethod(ACC_PUBLIC, "forEach", "(Ljava/sql/ResultSet;Lorg/sfm/utils/Handler;)Lorg/sfm/utils/Handler;", "<H::Lorg/sfm/utils/Handler<L"+ targetType +";>;>(Ljava/sql/ResultSet;TH;)TH;", new String[] { "org/sfm/map/MappingException", "java/sql/SQLException" });
			mv.visitCode();
			Label l0 = new Label();
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
			Label l3 = new Label();
			mv.visitLabel(l3);
			Label l4 = new Label();
			mv.visitJumpInsn(GOTO, l4);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, classType, "map", "(L" + sourceType + ";)L" + targetType + ";", false);
			mv.visitVarInsn(ASTORE, 3);
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/sfm/utils/Handler", "handle", "(Ljava/lang/Object;)V", true);
			mv.visitLabel(l1);
			mv.visitJumpInsn(GOTO, l4);
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_FULL, 4, new Object[] {classType, "java/sql/ResultSet", "org/sfm/utils/Handler", targetType}, 1, new Object[] {"java/lang/Throwable"});
			mv.visitVarInsn(ASTORE, 4);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, classType, "errorHandler", "Lorg/sfm/jdbc/JdbcMapperErrorHandler;");
			mv.visitVarInsn(ALOAD, 4);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/sfm/jdbc/JdbcMapperErrorHandler", "handlerError", "(Ljava/lang/Throwable;Ljava/lang/Object;)V", true);
			mv.visitLabel(l4);
			mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "next", "()Z", true);
			mv.visitJumpInsn(IFNE, l5);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 5);
			mv.visitEnd();
			}


		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}

	private static <S, T> void generateMappingCall(MethodVisitor mv,
			FieldMapper<S, T> mapper, int index, String classType, String sourceType, String targetType) {
		Class<?> mapperClass = AsmUtils.getPublicOrInterfaceClass(mapper.getClass());
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, classType, "mapper" + index, "L" +  AsmUtils.toType(mapperClass) + ";");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		if (AsmUtils.isStillGeneric(mapperClass)) {
			AsmUtils.invoke(mv, mapperClass, "map", "(Ljava/lang/Object;Ljava/lang/Object;)V");
		} else {
			AsmUtils.invoke(mv, mapperClass, "map", "(L" + sourceType + ";L" + targetType +";)V");
		}

	}

	private static <S, T> void addGetterSetterInit(MethodVisitor mv,
			FieldMapper<S, T> mapper, int index, String classType) {
		Class<?> mapperClass = mapper.getClass();
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		addIndex(mv, index);
		mv.visitInsn(AALOAD);
		mv.visitTypeInsn(CHECKCAST, AsmUtils.toType(mapperClass));
		mv.visitFieldInsn(PUTFIELD, classType, "mapper" + index, "L" +  AsmUtils.toType(mapperClass) +";");

	}

	private static <S, T> void declareMapperFields(ClassWriter cw,
			FieldMapper<S, T> mapper, String targetType, String sourceType, int index) {
		FieldVisitor fv;
		Class<?> mapperClass = AsmUtils.getPublicOrInterfaceClass(mapper.getClass());
		
		fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "mapper" + index, "L" + AsmUtils.toType(mapperClass) + ";", "L" + AsmUtils.toTypeWithParam(mapperClass) + ";", null);
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
