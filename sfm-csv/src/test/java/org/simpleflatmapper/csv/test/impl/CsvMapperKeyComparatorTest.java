package org.simpleflatmapper.csv.test.impl;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvColumnKeyMapperKeyComparator;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.test.map.issue.core.mapper.MapperKeyComparatorTest;

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