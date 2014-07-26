package org.sfm.reflect;

public interface SetterVisitor<T> {
	boolean visitSetter(String property, Setter<T, Object> setter);
}
