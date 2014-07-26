package org.sfm.utils;

public class PropertyNameMatcher {
	private final String name;
	
	public PropertyNameMatcher(String column) {
		name = toPropertyName(column);
	}

	public boolean matches(String property) {
		return property.equalsIgnoreCase(name);
	}
	
	public static String toPropertyName(String column) {
		StringBuilder sb = new StringBuilder(column.length());
		
		boolean upperCase = false;
		for(int i = 0; i < column.length(); i++) {
			char c = column.charAt(i);
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
