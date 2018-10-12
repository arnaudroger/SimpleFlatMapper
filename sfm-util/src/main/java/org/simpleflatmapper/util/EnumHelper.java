package org.simpleflatmapper.util;

public class EnumHelper {

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E[] getValues(Class<E> enumType) {
		return enumType.getEnumConstants();
	}
}
