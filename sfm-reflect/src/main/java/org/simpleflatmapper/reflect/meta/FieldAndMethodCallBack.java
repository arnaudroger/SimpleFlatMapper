package org.simpleflatmapper.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface FieldAndMethodCallBack {

	void method(Method method);

	void field(Field field);

}
