package org.sfm.reflect;

import java.util.List;

import org.sfm.jdbc.AsmHelper;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ListClassMeta;
import org.sfm.reflect.meta.ObjectClassMeta;

public class ReflectionService {
	private final SetterFactory setterFactory;
	private final InstantiatorFactory instantiatorFactory;
	private final AsmFactory asmFactory;
	private final boolean asmPresent;
	private final boolean asmActivated;

	public ReflectionService() {
		this(true);

	}
	public ReflectionService(final boolean asmActivated) {
		this(AsmHelper.isAsmPresent(), asmActivated);
	}
	
	public ReflectionService(final boolean asmPresent, final boolean asmActivated) {
		this.asmPresent = asmPresent;
		this.asmActivated = asmActivated && asmPresent;
		if (asmActivated) {
			asmFactory = new AsmFactory();
		} else {
			asmFactory = null;
		}
		this.setterFactory = new SetterFactory(asmFactory);
		this.instantiatorFactory = new InstantiatorFactory(asmFactory);
	}
	
	public ReflectionService(final boolean asmPresent, final boolean asmActivated, final AsmFactory asmFactory) {
		this.asmPresent = asmPresent;
		this.asmActivated = asmActivated && asmPresent;
		if (asmActivated) {
			this.asmFactory = asmFactory;
		} else {
			this.asmFactory = null;
		}
		this.setterFactory = new SetterFactory(asmFactory);
		this.instantiatorFactory = new InstantiatorFactory(asmFactory);
	}

	public SetterFactory getSetterFactory() {
		return setterFactory;
	}

	public InstantiatorFactory getInstantiatorFactory() {
		return instantiatorFactory;
	}

	public boolean isAsmPresent() {
		return asmPresent;
	}

	public boolean isAsmActivated() {
		return asmActivated;
	}
	public AsmFactory getAsmFactory() {
		return asmFactory;
	}
	public <T> ClassMeta<T> getClassMeta(Class<T> target) {
		return getClassMeta(null, target);
	}
	public <T> ClassMeta<T> getClassMeta(String prefix, Class<T> target) {
		if (List.class.isAssignableFrom(target)) {
			return new ListClassMeta(prefix, target, this);
		}
		return new ObjectClassMeta<T>(prefix, target, this);
	}
}
 