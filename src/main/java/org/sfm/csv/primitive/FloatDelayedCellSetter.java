package org.sfm.csv.primitive;

import org.sfm.csv.DelayedCellSetter;
import org.sfm.csv.cell.FloatCellValueReader;
import org.sfm.reflect.primitive.FloatSetter;

public class FloatDelayedCellSetter<T> implements DelayedCellSetter<T, Float> {

	private final FloatSetter<T> setter;
	private final FloatCellValueReader reader = new FloatCellValueReader();
	private float value;
	
	public FloatDelayedCellSetter(FloatSetter<T> setter) {
		this.setter = setter;
	}

	@Override
	public Float getValue() {
		return new Float(getFloat());
	}

	public float getFloat() {
		float v = value;
		value = 0;
		return v;
	}
	
	@Override
	public void set(T t) throws Exception {
		float v = value;
		value = 0;
		setter.setFloat(t, v);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}

	@Override
	public void set(byte[] bytes, int offset, int length) throws Exception {
		this.value = reader.parseFloat(bytes, offset, length);
	}

	@Override
	public void set(char[] chars, int offset, int length) throws Exception {
		this.value = FloatCellValueReader.parseFloat(chars, offset, length);
	}
}
