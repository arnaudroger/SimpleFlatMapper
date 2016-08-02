package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.DataType;
import org.junit.Test;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.test.core.mapper.MapperKeyComparatorTest;

import java.util.Random;

public class DatastaxMapperKeyComparatorTest {


    private Random random = new Random();

    @Test
    public void testKeyComparator() throws Exception {
        MapperKeyComparatorTest.testComparator(new MapperKeyComparatorTest.AbstractKeyProducer<DatastaxColumnKey>(DatastaxColumnKey.class) {
            @Override
            protected DatastaxColumnKey newKey(String name, int i) {
                return new DatastaxColumnKey(name, i, getDataType());
            }
        }, new DatastaxMapperKeyComparator());
    }

    private DataType getDataType() {
        int i = random.nextInt(3);

        switch (i) {
            case 0: return DataType.ascii();
            case 1: return DataType.bigint();
        }
        return null;
    }
}