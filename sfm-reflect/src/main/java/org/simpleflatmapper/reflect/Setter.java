package org.simpleflatmapper.reflect;


import org.simpleflatmapper.reflect.getter.FieldSetter;
import org.simpleflatmapper.reflect.setter.MethodSetter;

/**
 * Represent a setter of a property of type P on a object of type T.
 * <p>
 * use {@link ObjectSetterFactory} to instantiate.
 *
 * @see ObjectSetterFactory
 * @see FieldSetter
 * @see MethodSetter
 * @param <T> the target type
 * @param <P> the property type
 */
public interface Setter<T, P> {

	/**
	 * Set the properties on the target object to value.
	 * @param target the target to set the value on
	 * @param value the value to set
	 * @throws Exception if anything goes wrong
	 */
	void set(T target, P value) throws Exception;
}
