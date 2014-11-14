package org.sfm.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassVisitor {

	
	public static void visit(Class<?> target, FielAndMethodCallBack callback) {
		Class<?> currentClass = target;
		
		while(currentClass != null && !Object.class.equals(currentClass)) {
			
			for(Method method : currentClass.getDeclaredMethods()) {
				callback.method(method);
			}
			
			for(Field field : currentClass.getDeclaredFields()) {
				callback.field(field);
			}
			
			currentClass = currentClass.getSuperclass();
		}
		
	}
}
