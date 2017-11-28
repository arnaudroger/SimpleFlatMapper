package org.simpleflatmapper.converter.impl;

import org.simpleflatmapper.converter.Converter;

import java.math.BigInteger;
import java.util.UUID;

public class CharSequenceBigInteger implements Converter<CharSequence, BigInteger> {
    @Override
    public BigInteger convert(CharSequence in) throws Exception {
        if (in == null) return null;
        return new BigInteger(in.toString());
    }

    public String toString() {
        return "CharSequenceBigInteger";
    }
}
