package org.sfm.reflect.meta;

import java.lang.reflect.Type;
import java.util.List;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

public class ElementPropertyMeta<P> extends PropertyMeta<List<P>, P> {

	private final int index;
	private final ListClassMeta<P> listMetaData;
	public ElementPropertyMeta(String name,  ReflectionService reflectService, int index, ListClassMeta<P> listMetaData) {
		super(name, reflectService);
		this.index = index;
		this.listMetaData = listMetaData;
	}

	@Override
	protected Setter<List<P>, P> newSetter() {
		return new Setter<List<P>, P>() {
			@Override
			public void set(List<P> target, P value) throws Exception {
				while(target.size() < index) {
					target.add(null);
				}
				target.add(index, value);
			}

			@Override
			public Type getPropertyType() {
				return listMetaData.getElementTarget();
			}
		};
	}

	@Override
	public Type getType() {
		return listMetaData.getElementTarget();
	}


	@Override
	public boolean isPrimitive() {
		return false;
	}


}
