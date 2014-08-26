package org.sfm.reflect.asm;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sfm.utils.PropertyNameMatcher;

public final class ConstructorDefinition<T> {
	private final Constructor<T> constructor;
	private final Parameter[] parameters;
	public ConstructorDefinition(Constructor<T> constructor,
			Parameter[] parameters) {
		super();
		this.constructor = constructor;
		this.parameters = parameters;
	}
	public Constructor<T> getConstructor() {
		return constructor;
	}
	public Parameter[] getParameters() {
		return parameters;
	}
	
	public static <T> List<ConstructorDefinition<T>> extractConstructors(final Class<T> target) throws IOException {
		final List<ConstructorDefinition<T>> constructors = new ArrayList<>();
		
		ClassLoader cl = target.getClassLoader();
		if (cl == null) {
			cl = ClassLoader.getSystemClassLoader();
		}
		
		final InputStream is = cl.getResourceAsStream(target.getName().replace('.', '/') + ".class");
		try {
			ClassReader classReader = new ClassReader(is);
			classReader.accept(new ClassVisitor(Opcodes.ASM5) {

				@Override
				public MethodVisitor visitMethod(int access,
                        String name,
                        String desc,
                        String signature,
                        String[] exceptions) {
					if ("<init>".equals(name)) {
						return new MethodVisitor(Opcodes.ASM5) {
							final List<Parameter> parameters = new ArrayList<>();
							Label firstLabel;
							Label lastLabel;
							@Override
							public void visitLabel(Label label) {
								if (firstLabel == null) {
									firstLabel = label;
								}
								lastLabel = label;
							}

							@Override
							public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
								if (start.equals(firstLabel) && end.equals(lastLabel) && ! "this".equals(name)) {
									parameters.add(createParameter(name, desc));
								}
							}

							private Parameter createParameter(String name,
									String desc) {
								try {
									return new Parameter(name, AsmUtils.toClass(desc));
								} catch (ClassNotFoundException e) {
									throw new Error("Unexpected error " + e, e);
								}
							}

							@Override
							public void visitEnd() {
								try {
									constructors.add(new ConstructorDefinition<>(target.getDeclaredConstructor(toTypeArray(parameters)), parameters.toArray(new Parameter[parameters.size()])));
								} catch(Exception e) {
									throw new Error("Unexpected error " + e, e);
								}
							}
							
							private Class<?>[] toTypeArray(List<Parameter> parameters) {
								Class<?>[] types = new Class<?>[parameters.size()];
								for(int i = 0; i < types.length; i++) {
									types[i] = parameters.get(i).getType();
								}
								return types;
							}
						};
					} else {
						return null;
					}
				}

	
				
			}, 0);
		} finally {
			try { is.close(); } catch(Exception e) {};
		}
		
		return constructors;
	}
	public boolean hasParam(Parameter param) {
		for (Parameter p : parameters) {
			if (p.equals(param)) {
				return true;
			}
		}
		return false;
	}
	public Parameter lookFor(PropertyNameMatcher propertyNameMatcher) {
		for (Parameter p : parameters) {
			if (propertyNameMatcher.matches(p.getName())) {
				return p;
			}
		}
		return null;
	}
}
