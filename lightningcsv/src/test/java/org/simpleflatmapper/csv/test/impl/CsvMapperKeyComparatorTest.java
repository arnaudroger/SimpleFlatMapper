package org.simpleflatmapper.csv.test.impl;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvColumnKeyMapperKeyComparator;
import org.simpleflatmapper.test.core.mapper.MapperKeyComparatorTest;
import org.simpleflatmapper.csv.CsvColumnKey;

public class CsvMapperKeyComparatorTest {

    @Test
    public void testCsvColumnKey() throws Exception {
        MapperKeyComparatorTest.testComparator(new CsvColumnKeyProducer(), CsvColumnKeyMapperKeyComparator.INSTANCE);
    }

    private static class CsvColumnKeyProducer extends MapperKeyComparatorTest.AbstractKeyProducer<CsvColumnKey> {
        private CsvColumnKeyProducer() {
            super(CsvColumnKey.class);
        }

        @Override
        protected CsvColumnKey newKey(String name, int i) {
            return new CsvColumnKey(name, i);
        }
    }

}