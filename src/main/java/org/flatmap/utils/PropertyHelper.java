package org.flatmap.utils;

public class PropertyHelper {

	public static String toPropertyName(String column) {
		StringBuilder sb = new StringBuilder(column.length());
		
		for(int i = 0; i < column.length(); i++) {
			char c = column.charAt(i);
			if (c != '_') {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
}
