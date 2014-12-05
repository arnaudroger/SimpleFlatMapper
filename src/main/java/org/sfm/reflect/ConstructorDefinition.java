package org.sfm.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sfm.reflect.asm.AsmUtils;

public final class ConstructorDefinition<T> {
	private final Constructor<? extends T> constructor;
	private final ConstructorParameter[] parameters;
	public ConstructorDefinition(Constructor<? extends T> constructor,
			ConstructorParameter... parameters) {
		super();
		this.constructor = constructor;
		this.parameters = parameters;
	}
	public Constructor<? extends T> getConstructor() {
		return constructor;
	}
	public ConstructorParameter[] getParameters() {
		return parameters;
	}


	public boolean hasParam(ConstructorParameter param) {
		for (ConstructorParameter p : parameters) {
			if (p.equals(param)) {
				return true;
			}
		}
		return false;
	}
}
