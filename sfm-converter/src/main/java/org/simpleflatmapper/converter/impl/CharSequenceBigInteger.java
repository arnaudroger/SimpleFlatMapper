package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.math.BigInteger;

public class CharSequenceBigInteger implements Converter<CharSequence, BigInteger> {
    @Override
    public BigInteger convert(CharSequence in, Context context) throws Exception {
        if (in == null) return null;
        return new BigInteger(in.toString());
    }

    public String toString() {
        return "CharSequenceBigInteger";
    }
}
