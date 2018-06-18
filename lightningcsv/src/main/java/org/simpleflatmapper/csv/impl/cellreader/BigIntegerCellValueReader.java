package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

import java.math.BigInteger;

public class BigIntegerCellValueReader implements CellValueReader<BigInteger> {
    @Override
    public BigInteger read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        if (length <= 0) return null;
        return new BigInteger(new String(chars, offset, length));
    }
}
