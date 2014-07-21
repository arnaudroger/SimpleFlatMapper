package org.sfm.reflect.asm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.sfm.reflect.Setter;

public class AsmSetterFactory implements Opcodes {
	
	@SuppressWarnings("serial")
	static final Map<Class<?>, Class<?>> wrappers = new HashMap<Class<?>, Class<?>>() {
		{
			  put(boolean.class, Boolean.class);
		      put(byte.class, Byte.class);
		      put(char.class, Character.class);
		      put(double.class, Double.class);
		      put(float.class, Float.class);
		      put(int.class, Integer.class);
		      put(long.class, Long.class);
		      put(short.class, Short.class);
		}
	};
	
	@SuppressWarnings("serial")
	static final Map<Class<?>, String> primitivesType = new HashMap<Class<?>, String>() {
		{
			  put(boolean.class, "Z");
		      put(byte.class, "B");
		      put(char.class, "C");
		      put(double.class, "D");
		      put(float.class, "F");
		      put(int.class, "I");
		      put(long.class, "J");
		      put(short.class, "S");
		}
	};	
	
	@SuppressWarnings("serial")
	static final Set<Class<?>> primitivesClassAndWrapper = new HashSet<Class<?>>() {
		{
			addAll(wrappers.keySet());
			addAll(wrappers.values());
		}
	};
	
	private static class FactoryClassLoader extends ClassLoader {

		public FactoryClassLoader(ClassLoader parent) {
			super(parent);
		}

		private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			Class<?> type = classes.get(name);
			
			if (type != null) {
				return type; 
			} else {
				return super.findClass(name);
			}
		}
		
		public Class<?> registerGetter(String name, byte[] bytes) {
			Class<?> type = classes.get(name);
			if (type == null) {
				type = defineClass(name, bytes, 0, bytes.length);
				return type;
			} else {
				throw new RuntimeException("Setter " + name + " already defined");
			}
		}
	}
	
	private FactoryClassLoader factoryClassLoader;
	
	private Map<Method, Setter<?, ?>> setters = new HashMap<Method, Setter<?, ?>>();
	
	public AsmSetterFactory() {
		factoryClassLoader = new FactoryClassLoader(Thread.currentThread().getContextClassLoader());
	}
	
	public AsmSetterFactory(ClassLoader cl) {
		factoryClassLoader = new FactoryClassLoader(cl);
	}
	
	@SuppressWarnings("unchecked")
	public <T, P> Setter<T,P> createSetter(Method m) throws Exception {
		Setter<T,P> setter = (Setter<T, P>) setters.get(m);
		if (setter == null) {
			String className = generateClassName(m);
			byte[] bytes = generateClass(m, className);
			Class<?> type = factoryClassLoader.registerGetter(className, bytes);
			setter = (Setter<T, P>) type.newInstance();
			setters.put(m, setter);
		}
		return setter;
	}

	private byte[] generateClass(Method m, String className) throws Exception {
		Class<?> propertyType = m.getParameterTypes()[0];
		if (primitivesClassAndWrapper.contains(propertyType)) {
			return createPrimitiveSetter(className, m);
		} else {
			return createObjectSetter(className, m);
		}
	}
	
	private String generateClassName(Method m) {
		return "org.sfm.reflect.asm." + m.getDeclaringClass().getPackage().getName() + 
					".Setter" + m.getName()
					 + m.getDeclaringClass().getSimpleName()
					 + m.getParameterTypes()[0].getSimpleName()
					;
	}

	private byte[] createObjectSetter(String className, Method method) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		Class<?> target = method.getDeclaringClass();
		Class<?> property = method.getParameterTypes()[0];
		
		String targetType = toType(target);
		String propertyType = toType(property);
		String classType = toType(className);
		
		cw.visit(
				V1_7,
				ACC_PUBLIC + ACC_SUPER,
				classType,
				"Ljava/lang/Object;Lorg/sfm/reflect/Setter<L" + targetType + ";L" + propertyType + ";>;",
				"java/lang/Object", new String[] { "org/sfm/reflect/Setter" });

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "set",
					"(L" + targetType + ";L" + propertyType + ";)V", null,
					new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL,  targetType , method.getName(), "(L" + propertyType + ";)V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 3);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getPropertyType", "()Ljava/lang/Class;", "()Ljava/lang/Class<+L" + propertyType + ";>;", null);
			mv.visitCode();
			mv.visitLdcInsn(Type.getType("L" + propertyType + ";"));
			mv.visitInsn(ARETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, new String[] { "java/lang/Exception" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, targetType);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitTypeInsn(CHECKCAST,  propertyType );
			mv.visitMethodInsn(INVOKEVIRTUAL, classType , "set", "(L" + targetType + ";L" + propertyType + ";)V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}

	private byte[] createPrimitiveSetter(String className, Method method) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		Class<?> target = method.getDeclaringClass();
		Class<?> primitive = method.getParameterTypes()[0];
		Class<?> property = wrappers.get(primitive);
		
		String targetType = toType(target);
		String primitiveType = primitivesType.get(primitive);
		String propertyType = toType(property);
		String classType = toType(className);
		
		String methodSuffix =  property.getSimpleName();
		if ("Integer".equals(methodSuffix)) {
			methodSuffix = "Int";
		}
		
		String setMethod = "set" + methodSuffix;
		String valueMethod = methodSuffix.toLowerCase() + "Value";

		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER,  classType, 
				"Ljava/lang/Object;"
				+ "Lorg/sfm/reflect/Setter<L" + targetType + ";L" + propertyType + ";>;"
				+ "Lorg/sfm/reflect/primitive/" + methodSuffix + "Setter<L" + targetType + ";>;", 
				"java/lang/Object", 
				new String[] { "org/sfm/reflect/Setter", 
								"org/sfm/reflect/primitive/" + methodSuffix + "Setter" });


		{
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, setMethod, "(L" + targetType + ";" + primitiveType + ")V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ILOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL,  targetType , method.getName(), "(" + primitiveType + ")V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 3);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "set", "(L" + targetType + ";L" + propertyType + ";)V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL,  propertyType , valueMethod, "()" + primitiveType + "", false);
		mv.visitMethodInsn(INVOKEVIRTUAL,  targetType , method.getName(), "(" + primitiveType + ")V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 3);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "getPropertyType", "()Ljava/lang/Class;", "()Ljava/lang/Class<+L" + propertyType + ";>;", null);
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC,  propertyType , "TYPE", "Ljava/lang/Class;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, setMethod, "(Ljava/lang/Object;" + primitiveType + ")V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST,  targetType );
		mv.visitVarInsn(ILOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL,  classType , setMethod, "(L" + targetType + ";" + primitiveType + ")V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 3);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, new String[] { "java/lang/Exception" });
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST,  targetType );
		mv.visitVarInsn(ALOAD, 2);
		mv.visitTypeInsn(CHECKCAST,  propertyType );
		mv.visitMethodInsn(INVOKEVIRTUAL,  classType , "set", "(L" + targetType + ";L" + propertyType + ";)V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 3);
		mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
		}
	private static String toType(Class<?> target) {
		String name = target.getName();
		return toType(name);
	}

	private static String toType(String name) {
		return name.replace('.', '/');
	}
}
