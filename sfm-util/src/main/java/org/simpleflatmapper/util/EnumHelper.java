package org.simpleflatmapper.util;

public class EnumHelper {

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E[] getValues(Class<E> enumType) {
		try {
			return (E[]) enumType.getDeclaredMethod("values").invoke(enumType);
		} catch (Exception e) {
			throw new Error("Unexpected error getting enum values " + e, e);
		}
	}
}
