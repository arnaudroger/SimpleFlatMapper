package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.converter.ContextualConverter;

public class ConverterToTupleValueMapper<I> implements ContextualConverter<I, TupleValue> {

    private final FieldMapper<I, TupleValue> mapper;
    private final TupleType tupleType;

    public ConverterToTupleValueMapper(FieldMapper<I, TupleValue> mapper, TupleType tupleType) {
        this.mapper = mapper;
        this.tupleType = tupleType;
    }

    @Override
    public TupleValue convert(I in, Context context) throws Exception {
        if (in == null) return null;
        TupleValue tv = tupleType.newValue();
        mapper.mapTo(in, tv, null);
        return tv;
    }
}
