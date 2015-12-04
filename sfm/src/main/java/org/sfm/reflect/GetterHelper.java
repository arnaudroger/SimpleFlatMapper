package org.sfm.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class GetterHelper {

	public static final int SYNTHETIC = 0x00001000;

	public static boolean isPublicMember(final int modifiers) {
		return !Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)
				&& (modifiers & SYNTHETIC) == 0;
	}

	public static boolean methodNameMatchesProperty(final String name, final String property) {
        if (name.length() > 3 && (name.startsWith("get"))) {
            return name.regionMatches(true, 3, property, 0, property.length());
        } else if (name.length()> 2 && name.startsWith("is")) {
            return name.regionMatches(true, 2, property, 0, property.length());
        }
        return name.equalsIgnoreCase(property);
	}
	
	public static boolean fieldModifiersMatches(final int modifiers) {
		return !Modifier.isStatic(modifiers) &&  ! Modifier.isFinal(modifiers);
	}

	public static boolean fieldNameMatchesProperty(final String name, final String property) {
		return  name.equalsIgnoreCase(property);
	}
	
	public static String getPropertyNameFromMethodName(final String name) {
        if (name.startsWith("is")) {
            return name.substring(2, 3).toLowerCase() + name.substring(3);
        } else if (name.startsWith("get")){
            return name.substring(3, 4).toLowerCase() + name.substring(4);
        } else {
			return name;
		}
	}

	public static boolean isGetter(Method method) {
		return GetterHelper.isPublicMember(method.getModifiers())
				&& (method.getParameterTypes() == null ||  method.getParameterTypes().length == 0)
				&& !method.getReturnType().equals(void.class)
				&& !isToString(method)
				&& !isHashcode(method)
				&& method.getAnnotation(javax.persistence.Transient.class) == null;
	}

	private static boolean isToString(Method method) {
		return method.getName() == "toString" && method.getReturnType().equals(String.class)
				&& (method.getParameterTypes() == null ||  method.getParameterTypes().length == 0);
	}
	private static boolean isHashcode(Method method) {
		return method.getName() == "hashCode" && method.getReturnType().equals(int.class)
				&& (method.getParameterTypes() == null ||  method.getParameterTypes().length == 0);
	}

	public static boolean isCompatible(Type targetType, Type returnType) {
		return TypeHelper.isAssignable(targetType, returnType);
	}
}
