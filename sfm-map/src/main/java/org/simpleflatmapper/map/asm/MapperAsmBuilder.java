package org.simpleflatmapper.map.asm;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.fieldmapper.ConstantSourceFieldMapper;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.ow2asm.ClassWriter;
import org.simpleflatmapper.ow2asm.FieldVisitor;
import org.simpleflatmapper.ow2asm.MethodVisitor;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.AbstractMapper;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Setter;
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
import static org.simpleflatmapper.ow2asm.Opcodes.INVOKEINTERFACE;
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
    public static final String FIELD_MAPPER_PREFIX = "fieldMapper";
    public static final String CONSTRUCTOR_MAPPER_PREFIX = "constructorMapper";

    public static <S, T> byte[] dump(
            final String className,
            final FieldMapper<? super S, ? super T>[] _mappers,
            final FieldMapper<? super S, ? super T>[] _constructorMappers,
            final Class<? super S> sourceClass,
            final Class<T> target
    ) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        final String targetType = AsmUtils.toAsmType(target);
        final String mapperClassType = AsmUtils.toAsmType(className);
        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, mapperClassType, "L" + ABSTRACT_MAPPER_TYPE + "<" + toTargetTypeDeclaration(targetType) + ">;", ABSTRACT_MAPPER_TYPE, null);

        
        MapperBuilder[] mappers = new MapperBuilder[_mappers.length];
        for(int i = 0; i < mappers.length; i++) {
            mappers[i] = newMapperBuilder(_mappers[i], FIELD_MAPPER_PREFIX, i);
        }
        
        MapperBuilder[] constructorMappers = new MapperBuilder[_constructorMappers.length];
        for(int i = 0; i < _constructorMappers.length; i++) {
            constructorMappers[i] = newMapperBuilder(_constructorMappers[i], CONSTRUCTOR_MAPPER_PREFIX, i);
        }
        
        
        
        for (int i = 0; i < mappers.length; i++) {
            mappers[i].addDeclaration(cw);
        }

        for (int i = 0; i < constructorMappers.length; i++) {
            constructorMappers[i].addDeclaration(cw);
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                    "([L" + FIELD_MAPPER_TYPE + ";"
                            + "[L" + FIELD_MAPPER_TYPE + ";L"
                            + INSTANTIATOR_TYPE + ";)V",
                    "(" +
                            "[L" + FIELD_MAPPER_TYPE + "<" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + ">;" +
                            "[L" + FIELD_MAPPER_TYPE + "<" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + ">;" +
                            "L" + INSTANTIATOR_TYPE + "<" + toTargetTypeDeclaration(targetType) + ">;)V", null);

            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, ABSTRACT_MAPPER_TYPE, "<init>",
                    "(L" + INSTANTIATOR_TYPE + ";)V", false);


            for (int i = 0; i < mappers.length; i++) {
                mappers[i].addInit(mv, mapperClassType);
            }


            for (int i = 0; i < constructorMappers.length; i++) {
                constructorMappers[i].addInit(mv, mapperClassType);
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL, "mapFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType) + ")V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();

            for (int i = 0; i < mappers.length; i++) {
                mappers[i].addMappingCall(mv, mapperClassType);
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "mapFields", "(Ljava/lang/Object;Ljava/lang/Object;" + toTargetTypeDeclaration(mappingContextType) + ")V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(sourceClass));
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, targetType);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, mapperClassType, "mapFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType) + ")V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PROTECTED + ACC_FINAL, "mapToFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType) + ")V", null, new String[]{"java/lang/Exception"});
            mv.visitCode();

            for (int i = 0; i < constructorMappers.length; i++) {
                constructorMappers[i].addMappingCall(mv, mapperClassType);
            }

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, mapperClassType, "mapFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType) + ")V", false);
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
            mv.visitMethodInsn(INVOKEVIRTUAL, mapperClassType, "mapToFields", "(" + toTargetTypeDeclaration(sourceClass) + toTargetTypeDeclaration(targetType) + toTargetTypeDeclaration(mappingContextType) + ")V", false);
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

            for (int i = 0; i < mappers.length; i++) {
                String mapperName = ", " + FIELD_MAPPER_PREFIX + i + "=";
                String mapper = String.valueOf(mappers[i]);

                mv.visitLdcInsn(mapperName);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                mv.visitLdcInsn(mapper);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);


            }

            for (int i = 0; i < constructorMappers.length; i++) {
                String mapperName = ", " + CONSTRUCTOR_MAPPER_PREFIX + i + "=";
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

    private static <S, T> MapperBuilder newMapperBuilder(FieldMapper<? super S, ? super T> mapper, String fieldMapperPrefix, int i) {
        if (mapper == null) return EmptyMapperBuilder.INSTANCE;
        
        if (mapper instanceof ConstantSourceFieldMapper) {
            return new ConstantSourceFieldMapperBuilder<S, T>(mapper, i, fieldMapperPrefix);
        }
        return new DefaultMapperBuilder<S, T>(mapper, i, fieldMapperPrefix);
    }


    private static Method getMapToMethod(Class<?> aClass) {
        Method m = null;
        for (Method p : aClass.getDeclaredMethods()) {
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


    
    private interface MapperBuilder {
        void addInit(MethodVisitor mv, String mapperClassType);
        void addMappingCall(MethodVisitor mv, String mapperClassType) throws NoSuchMethodException;
        void addDeclaration(ClassWriter cw);
    }
    
    private static final class EmptyMapperBuilder implements MapperBuilder {
        private static final EmptyMapperBuilder INSTANCE = new EmptyMapperBuilder();
        @Override
        public void addInit(MethodVisitor mv, String mapperClassType) {
            
        }

        @Override
        public void addMappingCall(MethodVisitor mv, String mapperClassType) {

        }

        @Override
        public void addDeclaration(ClassWriter cw) {

        }
    }
    
    private static final class DefaultMapperBuilder<S, T> implements MapperBuilder {
        private final FieldMapper<? super S, ? super T> mapper;
        private final int index;
        private final String prefix;

        private DefaultMapperBuilder(FieldMapper<? super S, ? super T> mapper, int index, String prefix) {
            this.mapper = mapper;
            this.index = index;
            this.prefix = prefix;
        }

        @Override
        public void addInit(MethodVisitor mv, String mapperClassType) {
            Type mapperClass = AsmUtils.findClosestPublicTypeExposing(mapper.getClass(), FieldMapper.class);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, prefix == CONSTRUCTOR_MAPPER_PREFIX ? 2 : 1);
            AsmUtils.addIndex(mv, index);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, AsmUtils.toAsmType(mapperClass));
            mv.visitFieldInsn(PUTFIELD, mapperClassType, prefix + index, toTargetTypeDeclaration(AsmUtils.toAsmType(mapperClass)));
            
        }

        @Override
        public void addMappingCall(MethodVisitor mv, String mapperClassType) {
            Type mapperClass = AsmUtils.findClosestPublicTypeExposing(mapper.getClass(), FieldMapper.class);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, mapperClassType, prefix + index, "L" + AsmUtils.toAsmType(mapperClass) + ";");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);

            Method m = getMapToMethod(TypeHelper.toClass(mapperClass));
            AsmUtils.invoke(mv, mapperClass, m);
        }

        @Override
        public void addDeclaration(ClassWriter cw) {
            Type mapperClass = AsmUtils.findClosestPublicTypeExposing(mapper.getClass(), FieldMapper.class);
            FieldVisitor fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, prefix + index, toTargetTypeDeclaration(AsmUtils.toAsmType(mapperClass)), toTargetTypeDeclaration(AsmUtils.toGenericAsmType(mapperClass)), null);
            fv.visitEnd();
        }
    }


    private static final class ConstantSourceFieldMapperBuilder<S, T> implements MapperBuilder {
        private final FieldMapper<? super S, ? super T> mapper;
        
        private final int index;
        private final String prefix;

        private ConstantSourceFieldMapperBuilder(FieldMapper<? super S, ? super T> mapper, int index, String prefix) {
            this.mapper = mapper;
            this.index = index;
            this.prefix = prefix;
        }

        @Override
        public void addInit(MethodVisitor mv, String mapperClassType) {
            String fieldMapperType = AsmUtils.toAsmType(ConstantSourceFieldMapper.class);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, prefix == CONSTRUCTOR_MAPPER_PREFIX ? 2 : 1);
            AsmUtils.addIndex(mv, index);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, fieldMapperType);
            mv.visitFieldInsn(GETFIELD, fieldMapperType, "getter", toTargetTypeDeclaration(AsmUtils.toAsmType(ContextualGetter.class)));
            mv.visitFieldInsn(PUTFIELD, mapperClassType, prefix + index + "Getter", toTargetTypeDeclaration(AsmUtils.toAsmType(ContextualGetter.class)));
            
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, prefix == CONSTRUCTOR_MAPPER_PREFIX ? 2 : 1);
            AsmUtils.addIndex(mv, index);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, fieldMapperType);
            mv.visitFieldInsn(GETFIELD, fieldMapperType, "setter", toTargetTypeDeclaration(AsmUtils.toAsmType(Setter.class)));
            mv.visitFieldInsn(PUTFIELD, mapperClassType, prefix + index + "Setter", toTargetTypeDeclaration(AsmUtils.toAsmType(Setter.class)));
        }

        @Override
        public void addMappingCall(MethodVisitor mv, String mapperClassType) throws NoSuchMethodException {


            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, mapperClassType, prefix + index + "Setter", toTargetTypeDeclaration(AsmUtils.toAsmType(Setter.class)));
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, mapperClassType, prefix + index + "Getter", toTargetTypeDeclaration(AsmUtils.toAsmType(ContextualGetter.class)));
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 3);
            
            
            AsmUtils.invoke(mv, ContextualGetter.class, ContextualGetter.class.getMethod("get", Object.class, Context.class));
            AsmUtils.invoke(mv, Setter.class, Setter.class.getMethod("set", Object.class, Object.class));
        }

        @Override
        public void addDeclaration(ClassWriter cw) {
            {
                FieldVisitor fvGetter = cw.visitField(ACC_PRIVATE + ACC_FINAL, prefix + index + "Getter", toTargetTypeDeclaration(AsmUtils.toAsmType(ContextualGetter.class)), toTargetTypeDeclaration(AsmUtils.toGenericAsmType(ContextualGetter.class)), null);
                fvGetter.visitEnd();
            }
            {
                FieldVisitor fvSetter = cw.visitField(ACC_PRIVATE + ACC_FINAL, prefix + index+ "Setter", toTargetTypeDeclaration(AsmUtils.toAsmType(Setter.class)), toTargetTypeDeclaration(AsmUtils.toGenericAsmType(Setter.class)), null);
                fvSetter.visitEnd();
            }
        }
    }
}

