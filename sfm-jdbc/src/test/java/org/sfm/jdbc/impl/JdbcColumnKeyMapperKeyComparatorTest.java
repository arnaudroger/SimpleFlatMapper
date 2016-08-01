package org.sfm.jdbc.impl;

import org.junit.Test;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.mapper.MapperKeyComparatorTest;

public class JdbcColumnKeyMapperKeyComparatorTest {



    @Test
    public void testJdbcColumnKey() throws Exception {
        MapperKeyComparatorTest.testComparator(new JdbcColumnKeyProducer(), JdbcColumnKeyMapperKeyComparator.INSTANCE);
    }


    private static class JdbcColumnKeyProducer extends MapperKeyComparatorTest.AbstractKeyProducer<JdbcColumnKey> {

        private JdbcColumnKeyProducer() {
            super(JdbcColumnKey.class);
        }

        @Override
        protected JdbcColumnKey newKey(String name, int i) {
            return new JdbcColumnKey(name, i);
        }
    }
}