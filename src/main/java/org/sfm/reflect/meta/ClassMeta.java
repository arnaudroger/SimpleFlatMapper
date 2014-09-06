package org.sfm.reflect.meta;

import org.sfm.reflect.ReflectionService;

public interface ClassMeta<T> {

	public ReflectionService getReflectionService();

	public PropertyFinder<T> newPropertyFinder();

}