package org.simpleflatmapper.jdbc.test.impl;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.impl.JdbcColumnKeyMapperKeyComparator;
import org.simpleflatmapper.test.map.issue.core.mapper.MapperKeyComparatorTest;

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