package org.simpleflatmapper.map.property;

import org.simpleflatmapper.map.FieldMapper;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class FieldMapperProperty {
    private final FieldMapper<?, ?> fieldMapper;

    public FieldMapperProperty(FieldMapper<?, ?> fieldMapper) {
        this.fieldMapper = requireNonNull("fieldMapper", fieldMapper);
    }

    public FieldMapper<?, ?> getFieldMapper() {
        return fieldMapper;
    }

    @Override
    public String toString() {
        return "FieldMapper{" + fieldMapper + "}";
    }
}
