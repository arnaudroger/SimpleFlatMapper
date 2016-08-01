package org.simpleflatmapper.csv.impl;

import org.junit.Test;
import org.sfm.map.mapper.MapperKeyComparatorTest;
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