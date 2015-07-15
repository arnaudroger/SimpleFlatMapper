package org.sfm.reflect;

import java.lang.reflect.Modifier;

public class SetterHelper {
	public static boolean methodModifiersMatches(final int modifiers) {
		return !Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers);
	}

	public static boolean methodNameMatchesProperty(final String name, final String property) {
		return (isSetter(name) && name.regionMatches(true, 3, property, 0, property.length())) 
				|| name.equalsIgnoreCase(property);
	}
	
	public static boolean fieldModifiersMatches(final int modifiers) {
		return !Modifier.isStatic(modifiers) &&  ! Modifier.isFinal(modifiers);
	}

	public static boolean fieldNameMatchesProperty(final String name, final String property) {
		return  name.equalsIgnoreCase(property);
	}
	
	public static String getPropertyNameFromMethodName(final String name) {
		return name.substring(3,4).toLowerCase() +  name.substring(4);
	}
	

	public static boolean isSetter(final String name) {
		return name.length() > 3 && name.startsWith("set");
	}
}
