package org.simpleflatmapper.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * That class is a way to define generic type without implementing the ParameterizedType.<br>
 *
 * By defining an anonymous instance of it the new class has access to the actual type argument T that the method getType() returns;<br>
 * <code>
 *     new TypeReference&lt;Tuple2&lt;String,Long&gt;&gt;(){}
 * </code>
 *
 * @param <T> the targeted type
 */
public abstract class TypeReference<T>  {
	private final Type _type;

	protected TypeReference() {
		Type superClass = getClass().getGenericSuperclass();
		if (superClass instanceof Class<?>) {
			throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
		}

		_type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
	}

	public Type getType() {
		return _type;
	}

}
