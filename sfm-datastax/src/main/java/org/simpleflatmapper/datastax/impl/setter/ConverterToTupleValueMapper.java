package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.converter.Converter;

public class ConverterToTupleValueMapper<I> implements Converter<I, TupleValue> {

    private final Mapper<I, TupleValue> mapper;
    private final TupleType tupleType;

    public ConverterToTupleValueMapper(Mapper<I, TupleValue> mapper, TupleType tupleType) {
        this.mapper = mapper;
        this.tupleType = tupleType;
    }

    @Override
    public TupleValue convert(I in) throws Exception {
        if (in == null) return null;
        TupleValue tv = tupleType.newValue();
        mapper.mapTo(in, tv, null);
        return tv;
    }
}
