package org.sfm.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.AsmHelper;
import org.sfm.reflect.meta.ArrayClassMeta;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ClassMeta<T> getClassMeta(Type target) {
		Class<T> clazz = TypeHelper.toClass(target);
		
		if (List.class.isAssignableFrom(clazz)) {
			ParameterizedType pt = (ParameterizedType) target;
			return new ListClassMeta(pt.getActualTypeArguments()[0], this);
		}else if (clazz.isArray()) {
			return new ArrayClassMeta(clazz, clazz.getComponentType(), this);
		}
		return new ObjectClassMeta<T>(clazz, this);
	}
}
 