package org.flatmap.reflect;

public interface SetterFactory {
	<T, P, C extends T> Setter<T, P> getSetter(Class<C> target, String property);
}