package org.sfm.reflect.meta;

import java.lang.reflect.Type;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

public class ArrayElementPropertyMeta<P> extends PropertyMeta<P[], P> {

	private final int index;
	private final ArrayClassMeta<P> arrayMetaData;
	public ArrayElementPropertyMeta(String name,  ReflectionService reflectService, int index, ArrayClassMeta<P> arrayMetaData) {
		super(name, reflectService);
		this.index = index;
		this.arrayMetaData = arrayMetaData;
	}

	@Override
	protected Setter<P[], P> newSetter() {
		return new Setter<P[], P>() {
			@Override
			public void set(P[] target, P value) throws Exception {
				target[index] = value;
			}
		};
	}

	@Override
	public Type getType() {
		return arrayMetaData.getElementTarget();
	}


	public int getIndex() {
		return index;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}


}
