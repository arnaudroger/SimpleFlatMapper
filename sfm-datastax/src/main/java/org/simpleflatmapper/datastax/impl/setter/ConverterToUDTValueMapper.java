package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.converter.Converter;

public class ConverterToUDTValueMapper<I> implements Converter<I, UDTValue> {

    private final Mapper<I, UDTValue> mapper;
    private final UserType userType;

    public ConverterToUDTValueMapper(Mapper<I, UDTValue> mapper, UserType userType) {
        this.mapper = mapper;
        this.userType = userType;
    }

    @Override
    public UDTValue convert(I in) throws Exception {
        if (in == null) return null;
        UDTValue udtValue = userType.newValue();
        mapper.mapTo(in, udtValue, null);
        return udtValue;
    }
}
