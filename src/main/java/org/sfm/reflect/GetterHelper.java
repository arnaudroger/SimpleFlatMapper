package org.sfm.reflect;

import java.lang.reflect.Modifier;

public class GetterHelper {

	public static boolean methodModifiersMatches(final int modifiers) {
		return !Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers);
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
        } else {
            return name.substring(3, 4).toLowerCase() + name.substring(4);
        }
	}
}
