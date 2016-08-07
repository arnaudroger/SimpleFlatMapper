package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberBigIntegerConverter implements Converter<Number, BigInteger> {
    @Override
    public BigInteger convert(Number in) {
        if (in == null) return null;
        if (in instanceof BigDecimal) {
            return ((BigDecimal) in).toBigInteger();
        }
        return BigInteger.valueOf(in.longValue());
    }

    public String toString() {
        return "NumberToBigInteger";
    }
}
