package org.sfm.jdbc.impl;

import org.junit.Test;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.FieldKey;
import org.sfm.map.mapper.MapperKey;
import org.sfm.map.mapper.MapperKeyComparator;
import org.sfm.utils.ListCollectorHandler;
import org.sfm.utils.RowHandler;

import java.lang.reflect.Array;
import java.util.Random;

import static org.junit.Assert.*;

public class JdbcColumnKeyMapperKeyComparatorTest {

    private static final String[] COLUMNS = new String[] {
        "id",
        "firstname",
        "lastname",
        "age",
        "postcode",
        "address",
        "city"
    };
    private static final Random random = new Random();


    @Test
    public void testJdbcColumnKey() throws Exception {
        testComparator(new JdbcColumnKeyProducer(), JdbcColumnKeyMapperKeyComparator.INSTANCE);
    }

    public static <K extends FieldKey<K>> MapperKey<K>[] generateKeys(KeyProducer<K> producer) throws Exception {
        ListCollectorHandler<MapperKey<K>> collector = new ListCollectorHandler<MapperKey<K>>();

        for(int i = 0; i < 1000; i++) {
            producer.produces(collector, names(random));
        }

        return collector.getList().toArray(new MapperKey[0]);
    }

    private static String[] names(Random random) {
        int size = random.nextInt(COLUMNS.length);

        String[] names = new String[size];

        for(int i = 0; i < names.length; i++) {
            names[i] = COLUMNS[random.nextInt(COLUMNS.length)];
        }
        return names;
    }


    public static <K extends FieldKey<K>> void testComparator(KeyProducer<K> producer, MapperKeyComparator<K> comparator) throws Exception {
        MapperKey<K>[] keys = generateKeys(producer);

        for(int i = 0; i < keys.length; i++ ){
            for (int j = 0; j < keys.length; j++) {
                MapperKey<K> key1 = keys[i];
                MapperKey<K> key2 = keys[j];

                if (key1.equals(key2)) {
                    assertEquals(0, comparator.compare(key1, key2));
                    assertEquals(0, comparator.compare(key2, key1));
                } else {
                    int d = comparator.compare(key1, key2);
                    assertFalse(d == 0);
                    if (d < 0) {
                        assertTrue(comparator.compare(key2, key1) > 0);
                    } else {
                        assertTrue(comparator.compare(key2, key1) < 0);
                    }
                }
                assertEquals(0, comparator.compare(key2, key2));
                assertEquals(0, comparator.compare(key1, key1));


            }
        }

    }

    public interface KeyProducer<K extends FieldKey<K>> {
        void produces(RowHandler<MapperKey<K>> consumer, String[] names) throws Exception;
    }

    public static abstract class AbstractKeyProducer<K extends FieldKey<K>> implements KeyProducer<K> {
        private final Class<K> keyClass;

        protected AbstractKeyProducer(Class<K> keyClass) {
            this.keyClass = keyClass;
        }

        @Override
        public void produces(RowHandler<MapperKey<K>> consumer, String[] names) throws Exception {
            K[] columns1 = (K[]) Array.newInstance(keyClass, names.length);
            K[] columns2 = (K[]) Array.newInstance(keyClass, names.length);

            for(int i = 0; i < names.length; i++) {
                columns1[i] = newKey(names[i], i);
                columns2[i] = newKey(names[i], i+1);
            }
            consumer.handle(new MapperKey<K>(columns1));
            consumer.handle(new MapperKey<K>(columns2));
        }

        protected abstract K newKey(String name, int i);
    }


    private static class JdbcColumnKeyProducer extends AbstractKeyProducer<JdbcColumnKey> {

        private JdbcColumnKeyProducer() {
            super(JdbcColumnKey.class);
        }

        @Override
        protected JdbcColumnKey newKey(String name, int i) {
            return new JdbcColumnKey(name, i);
        }
    }
}