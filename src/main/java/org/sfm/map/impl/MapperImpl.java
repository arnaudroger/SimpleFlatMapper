package org.sfm.map.impl;

import org.sfm.reflect.Instantiator;

import java.util.Arrays;

public class MapperImpl<S, T> extends AbstractMapperImpl<S, T> {
	
	private final FieldMapper<S, T>[] fieldMappers;
	
	public MapperImpl(final FieldMapper<S, T>[] mappers, final Instantiator<S, T> instantiator) {
		super(instantiator);
		this.fieldMappers = mappers;
	}

	protected final void mapFields(final S source, final T target) throws Exception {
		for(int i = 0; i < fieldMappers.length; i++) {
			fieldMappers[i].map(source, target);
		}
	}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName())
                .append("{");

        super.appendToStringBuilder(sb);
        sb.append(", fieldMappers=").append(Arrays.toString(fieldMappers));
        sb.append("}");

        return sb.toString();
    }
}
