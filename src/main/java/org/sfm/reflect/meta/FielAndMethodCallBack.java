package org.sfm.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface FielAndMethodCallBack {

	void method(Method method);

	void field(Field field);

}
