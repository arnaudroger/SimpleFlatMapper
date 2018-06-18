package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalCellValueReader implements CellValueReader<BigDecimal> {
    @Override
    public BigDecimal read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        if (length <= 0) return null;
        return new BigDecimal(new String(chars, offset, length));
    }
}
