package org.sfm.utils;

public class PropertyNameMatcher {
	private final String name;
	
	public PropertyNameMatcher(final String column) {
		name = toPropertyName(column);
	}

	public boolean matches(final String property) {
		return property.equalsIgnoreCase(name);
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
