package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CharSequenceBigDecimal implements Converter<CharSequence, BigDecimal> {
    @Override
    public BigDecimal convert(CharSequence in) throws Exception {
        if (in == null) return null;
        return new BigDecimal(in.toString());
    }

    public String toString() {
        return "CharSequenceBigDecimal";
    }
}
