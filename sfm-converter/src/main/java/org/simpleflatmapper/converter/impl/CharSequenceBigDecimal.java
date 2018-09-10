package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.math.BigDecimal;

public class CharSequenceBigDecimal implements Converter<CharSequence, BigDecimal> {
    @Override
    public BigDecimal convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return new BigDecimal(in.toString());
    }

    public String toString() {
        return "CharSequenceBigDecimal";
    }
}
