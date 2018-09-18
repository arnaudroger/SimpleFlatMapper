package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.math.BigDecimal;

public class CharSequenceBigDecimal implements ContextualConverter<CharSequence, BigDecimal> {
    @Override
    public BigDecimal convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return new BigDecimal(in.toString());
    }

    public String toString() {
        return "CharSequenceBigDecimal";
    }
}
