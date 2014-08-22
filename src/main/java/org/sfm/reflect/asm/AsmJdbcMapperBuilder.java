package org.sfm.reflect.asm;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.TypeVariable;
import java.sql.ResultSet;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.sfm.map.FieldMapper;
import org.sfm.reflect.Instantiator;

public class AsmJdbcMapperBuilder {
	public static <S,T> byte[] dump (final String className, final FieldMapper<S, T>[] mappers, final Instantiator<T> instantiator, final Class<T> target) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;
		
		final String targetType = AsmUtils.toType(target);
		final String sourceType = AsmUtils.toType(ResultSet.class);
		final String classType = AsmUtils.toType(className);
		final String instantiatorType = AsmUtils.toType(instantiator.getClass());

		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER + ACC_FINAL, classType, "Ljava/lang/Object;Lorg/sfm/jdbc/JdbcMapper<L" + targetType + ";>;", "java/lang/Object", new String[] { "org/sfm/jdbc/JdbcMapper" });


		for(int i = 0; i < mappers.length; i++) {
			declareMapperFields(cw,  mappers[i], targetType, sourceType, i);  
		}
		FieldVisitor fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "instantiator", "L" + instantiatorType + ";", "L" + AsmUtils.toTypeWithParam(instantiator.getClass()) + ";", null);
		fv.visitEnd();

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Lorg/sfm/map/FieldMapper;Lorg/sfm/reflect/Instantiator;)V", "([Lorg/sfm/map/FieldMapper<L" + sourceType + ";L" + targetType + ";>;Lorg/sfm/reflect/Instantiator<L" + targetType + ";>;)V", null);

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
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		{
			
			mv = cw.visitMethod(ACC_PUBLIC, "map", "(L" + sourceType + ";)L" + targetType + ";", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, classType, "instantiator", "L" + instantiatorType + ";");
			if (isStillGeneric(instantiator.getClass())) {
				mv.visitMethodInsn(INVOKEVIRTUAL, instantiatorType, "newInstance", "()Ljava/lang/Object;", false);
			} else {
				mv.visitMethodInsn(INVOKEVIRTUAL, instantiatorType, "newInstance", "()L" + targetType + ";", false);
			}
			mv.visitTypeInsn(CHECKCAST, targetType);
			mv.visitVarInsn(ASTORE, 2);

			for(int i = 0; i < mappers.length; i++) {
				generateMappingCall(mv, mappers[i], i, classType, sourceType, targetType);
			}
			
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ARETURN);
			
			mv.visitMaxs(3, 3);
			mv.visitEnd();
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
			mv = cw.visitMethod(ACC_PUBLIC, "forEach", "(Ljava/sql/ResultSet;Lorg/sfm/utils/Handler;)Lorg/sfm/utils/Handler;", "<H::Lorg/sfm/utils/Handler<L" + targetType + ";>;>(L" + sourceType + ";TH;)TH;", new String[] { "java/lang/Exception" });
			mv.visitCode();
			Label l1 = new Label();
			mv.visitJumpInsn(GOTO, l1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitFrame(F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, classType, "map", "(L" + sourceType + ";)L" + targetType + ";", false);
			mv.visitVarInsn(ASTORE, 3);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/sfm/utils/Handler", "handle", "(Ljava/lang/Object;)V", true);
			mv.visitLabel(l1);
			mv.visitFrame(F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "next", "()Z", true);
			mv.visitJumpInsn(IFNE, l2);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 4);
			mv.visitEnd();
			}

		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
	}

	private static boolean isStillGeneric(Class<? > clazz) {
		final TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
		return typeParameters != null && typeParameters.length > 0;
	}

	private static <S, T> void generateMappingCall(MethodVisitor mv,
			FieldMapper<S, T> mapper, int index, String classType, String sourceType, String targetType) {
		Class<?> mapperClass = mapper.getClass();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, classType, "mapper" + index, "L" +  AsmUtils.toType(mapperClass) + ";");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		
		if (isStillGeneric(mapperClass)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, AsmUtils.toType(mapperClass), "map", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
		} else {
			mv.visitMethodInsn(INVOKEVIRTUAL, AsmUtils.toType(mapperClass), "map", "(L" + sourceType + ";L" + targetType +";)V", false);
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
		Class<?> mapperClass = mapper.getClass();
		
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
