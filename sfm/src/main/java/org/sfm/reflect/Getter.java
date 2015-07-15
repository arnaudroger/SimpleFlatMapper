package org.sfm.reflect;


/**
 * Interface representing a Getter of a property of type P on a object of type T.
 * <p>
 * use {@link ObjectGetterFactory} to instantiate.
 * @see ObjectGetterFactory
 * @see org.sfm.reflect.impl.MethodGetter
 * @see org.sfm.reflect.impl.FieldGetter
 * @param <T> the targeted type
 * @param <P> the property type
 */
public interface Getter<T, P> {
	/**
	 * Return the property from the specified object.
	 * @param target the object to get the property from
	 * @return the property
	 * @throws Exception if anything goes wrong
	 */
	P get(T target) throws Exception;
}
