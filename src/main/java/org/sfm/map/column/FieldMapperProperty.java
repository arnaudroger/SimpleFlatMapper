package org.sfm.map.column;

import org.sfm.map.FieldMapper;

import static org.sfm.utils.Asserts.requireNonNull;

public class FieldMapperProperty implements ColumnProperty {
    private final FieldMapper<?, ?> fieldMapper;

    public FieldMapperProperty(FieldMapper<?, ?> fieldMapper) {
        this.fieldMapper = requireNonNull("fieldMapper", fieldMapper);
    }

    public FieldMapper<?, ?> getFieldMapper() {
        return fieldMapper;
    }

    @Override
    public String toString() {
        return "FieldMapper{FieldMapper}";
    }
}
