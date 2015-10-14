package org.sfm.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SetterHelper {

	public static boolean methodNameMatchesProperty(final String name, final String property) {
		return (name.startsWith("set") && name.regionMatches(true, 3, property, 0, property.length()))
				|| name.equalsIgnoreCase(property);
	}

	public static boolean fieldModifiersMatches(final int modifiers) {
		return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
	}

	public static boolean fieldNameMatchesProperty(final String name, final String property) {
		return name.equalsIgnoreCase(property);
	}

	public static String getPropertyNameFromMethodName(final String name) {
		if (name.startsWith("set") || name.startsWith("get")) {
			return name.substring(3, 4).toLowerCase() + name.substring(4);
		} else if (name.startsWith("is")) {
			return name.substring(2, 3).toLowerCase() + name.substring(3);
		} else {
			return name;
		}
	}


	public static boolean isSetter(Method method) {
		return (method.getReturnType().equals(void.class)
				|| method.getReturnType().equals(method.getDeclaringClass()))
				&& method.getParameterTypes() != null &&  method.getParameterTypes().length == 1
				&& GetterHelper.isPublicMember(method.getModifiers())
				&& !isEquals(method);
	}

	private static boolean isEquals(Method method) {
		return method.getName() == "equals" && method.getReturnType().equals(boolean.class)
				&& method.getParameterTypes() != null &&  method.getParameterTypes().length == 1;
	}
}
