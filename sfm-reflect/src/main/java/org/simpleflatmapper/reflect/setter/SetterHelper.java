package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.getter.GetterHelper;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

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
		if (name.startsWith("set") && name.length() > 3)  {
			return name.substring(3, 4).toLowerCase() + name.substring(4);
		} else {
			return name;
		}
	}
	public static String getPropertyNameFromBuilderMethodName(final String name) {
		if (name.startsWith("set") && name.length() > 3)  {
			return name.substring(3, 4).toLowerCase() + name.substring(4);
		} else if (name.startsWith("with") && name.length() > 4)  {
			return name.substring(4, 5).toLowerCase() + name.substring(5);
		} else if (name.startsWith("addAll") && name.length() > "addAll".length()){
			return name.substring(6, 7).toLowerCase() + name.substring(7);
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
		return "equals".equals(method.getName()) && method.getReturnType().equals(boolean.class)
				&& method.getParameterTypes() != null &&  method.getParameterTypes().length == 1;
	}

	public static boolean isCompatible(Type propertyType, Type settableType) {
		return TypeHelper.isAssignable(propertyType, settableType);
	}
}
