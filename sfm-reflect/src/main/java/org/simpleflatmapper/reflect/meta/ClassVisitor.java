package org.simpleflatmapper.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassVisitor {

	
	public static void visit(Class<?> target, FieldAndMethodCallBack callback) {
		Class<?> currentClass = target;
		
		while(currentClass != null && !Object.class.equals(currentClass)) {

            for(Field field : currentClass.getDeclaredFields()) {
                callback.field(field);
            }

			for(Method method : currentClass.getDeclaredMethods()) {
				callback.method(method);
			}

			currentClass = currentClass.getSuperclass();
		}
		
	}
}
