package org.sfm.map;

public interface FieldMapperErrorHandler {
	void errorGettingValue(String name, Object source, Object target, Exception error) throws Exception;
	void errorSettingValue(String name, Object source, Object target, Exception error) throws Exception;
}
