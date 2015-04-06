package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SingletonPropertyFinder<T> implements PropertyFinder<T> {
	private static final HashMap<Class<?>, Integer> coefficients = new HashMap<Class<?>, Integer>();
	static {
		coefficients.put(double.class, 128);
		coefficients.put(Double.class, 127);
		coefficients.put(float.class, 126);
		coefficients.put(Float.class, 125);
		coefficients.put(long.class, 124);
		coefficients.put(Long.class, 123);
		coefficients.put(int.class, 122);
		coefficients.put(Integer.class, 121);
		coefficients.put(short.class, 120);
		coefficients.put(Short.class, 119);
		coefficients.put(byte.class, 118);
		coefficients.put(Byte.class, 117);
		coefficients.put(char.class, 116);
		coefficients.put(Character.class, 115);
		coefficients.put(String.class, 114);
		coefficients.put(Date.class, 113);
	}

	private final PropertyFinder<T> propertyFinder;

	private final List<String> selectedParameters = new ArrayList<String>();

	public SingletonPropertyFinder(ClassMeta<T> classMeta) {
		this.propertyFinder = classMeta.newPropertyFinder();
	}


	@Override
	public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {

		PropertyMeta<T, E> property = propertyFinder.findProperty(propertyNameMatcher);

		if (property == null && selectedParameters.isEmpty()) {
			InstantiatorDefinition instantiatorDefinition = readerFriendlyConstructor( propertyFinder.getEligibleInstantiatorDefinitions());

			if (instantiatorDefinition != null) {
				Parameter param = instantiatorDefinition.getParameters()[selectedParameters.size()];
				selectedParameters.add(param.getName());
				return propertyFinder.findConstructor(instantiatorDefinition);
			}
		}
		return property;
	}

	protected InstantiatorDefinition readerFriendlyConstructor(List<InstantiatorDefinition> eligibleInstantiatorDefinitions) {
		InstantiatorDefinition selected = null;

		for(InstantiatorDefinition def : eligibleInstantiatorDefinitions) {
			if (def.getParameters().length == 1) {
				if (selected == null
						|| prefersFirstType(def.getParameters()[0].getType(), selected.getParameters()[0].getType()))
					selected = def;
			}
		}

		return selected;
	}

	private boolean prefersFirstType(Type type1, Type type2) {
		return getCoefficient(type1) > getCoefficient(type2);
	}

	private int getCoefficient(Type t) {
		if (coefficients.containsKey(TypeHelper.toClass(t))) {
			return coefficients.get(TypeHelper.toClass(t));
		} else {
			return -1;
		}
	}


	@Override
	public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
		return propertyFinder.getEligibleInstantiatorDefinitions();
	}

    @Override
    public <E> ConstructorPropertyMeta<T, E> findConstructor(InstantiatorDefinition instantiatorDefinition) {
        return propertyFinder.findConstructor(instantiatorDefinition);
    }

}
