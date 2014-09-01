package org.sfm.utils;

public class PropertyNameMatcher {
	private final String name;
	
	public PropertyNameMatcher(final String column) {
		name = toPropertyName(column);
	}

	public boolean matches(final String property) {
		return property.equalsIgnoreCase(name);
	}
	public boolean couldBePropertyOf(final String property) {
		return startsWithIgnoreCase(name, property);
	}
	
	public static boolean startsWithIgnoreCase(String v1, String v2) {
		if (v2.length() > v1.length()) {
			return false;
		}
		for(int i = 0; i < v2.length(); i++) {
			char c2 = v2.charAt(i);
			char c1 = v1.charAt(i);
			if (Character.toUpperCase(c1) != Character.toUpperCase(c2)) {
				return false;
			}
		}
		return true;
	}

	public static String toPropertyName(final String column) {
		final StringBuilder sb = new StringBuilder(column.length());
		
		boolean upperCase = false;
		for(int i = 0; i < column.length(); i++) {
			final char c = column.charAt(i);
			if (c != '_') {
				if (upperCase) {
					sb.append(Character.toUpperCase(c));
					upperCase = false;
				} else {
					sb.append(Character.toLowerCase(c));
				}
			} else {
				upperCase = true;
			}
		
		}
		return sb.toString();
	}
}
