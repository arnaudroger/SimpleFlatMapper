package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberBigDecimalConverter implements ContextualConverter<Number, BigDecimal> {
    @Override
    public BigDecimal convert(Number in, Context context) {
        if (in == null) return null;
        if (in instanceof BigInteger) {
            return new BigDecimal((BigInteger) in);
        }
        return new BigDecimal(in.doubleValue());
    }

    public String toString() {
        return "NumberToBigDecimal";
    }
}
