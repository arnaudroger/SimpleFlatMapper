package org.sfm.map;

public class RethrowFieldMapperErrorHandler implements FieldMapperErrorHandler {

	@Override
	public void errorGettingValue(String name, Object source, Object target,
			Exception error) throws Exception {
		throw error;
	}

	@Override
	public void errorSettingValue(String name, Object source, Object target,
			Exception error) throws Exception {
		throw error;
	}

}
