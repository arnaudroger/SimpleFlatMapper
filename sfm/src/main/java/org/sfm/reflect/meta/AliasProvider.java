package org.sfm.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface AliasProvider {

	String getAliasForMethod(Method method);

	String getAliasForField(Field field);

}
