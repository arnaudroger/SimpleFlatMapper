package org.sfm.reflect.asm;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.sfm.map.Mapper;

public class AsmMapperBuilder {
	public static <S,T> byte[] dump (final String className, final Mapper<S, T>[] mappers, final Class<S> source, final Class<T> target) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;
		
		String targetType = AsmUtils.toType(target);
		String sourceType = AsmUtils.toType(source);
		String classType = AsmUtils.toType(className);


		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER + ACC_FINAL, classType, "Ljava/lang/Object;Lorg/sfm/map/Mapper<L" + sourceType + ";L" + targetType + ";>;", "java/lang/Object", new String[] { "org/sfm/map/Mapper" });

		for(int i = 0; i < mappers.length; i++) {
			declareMapperFields(cw,  mappers[i], targetType, sourceType, i); 
		}

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Lorg/sfm/map/Mapper;)V", "([Lorg/sfm/map/Mapper<L" + sourceType + ";L" + targetType + ";>;)V", null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			
			
			for(int i = 0; i < mappers.length; i++) {
				addGetterSetterInit(mv,  mappers[i], i, classType); 
			}
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "map", "(L" + sourceType + ";L" + targetType + ";)V", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			
			for(int i = 0; i < mappers.length; i++) {
				generateMappingCall(mv, mappers[i], i, classType);
			}
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(4, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "map", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, "" + sourceType + "");
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(CHECKCAST, "" + targetType + "");
			mv.visitMethodInsn(INVOKEVIRTUAL, "" + classType + "", "map", "(L" + sourceType + ";L" + targetType + ";)V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		cw.visitEnd();

		return AsmUtils.writeClassToFile(className, cw.toByteArray());
		}

	private static <S, T> void generateMappingCall(MethodVisitor mv,
			Mapper<S, T> mapper, int index, String classType) {
		Class<?> mapperClass = mapper.getClass();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, classType, "mapper" + index, "L" +  AsmUtils.toType(mapperClass) + ";");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, AsmUtils.toType(mapperClass), "map", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);

	}

	private static <S, T> void addGetterSetterInit(MethodVisitor mv,
			Mapper<S, T> mapper, int index, String classType) {
		Class<?> mapperClass = mapper.getClass();
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		addIndex(mv, index);
		mv.visitInsn(AALOAD);
		mv.visitTypeInsn(CHECKCAST, AsmUtils.toType(mapperClass));
		mv.visitFieldInsn(PUTFIELD, classType, "mapper" + index, "L" +  AsmUtils.toType(mapperClass) +";");

	}

	private static <S, T> void declareMapperFields(ClassWriter cw,
			Mapper<S, T> mapper, String targetType, String sourceType, int index) {
		FieldVisitor fv;
		Class<?> mapperClass = mapper.getClass();
		
		fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "mapper" + index, "L" + AsmUtils.toType(mapperClass) + ";", "L" + AsmUtils.toType(mapperClass) + "<L" + sourceType + ";L" + targetType + ";>;", null);
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
