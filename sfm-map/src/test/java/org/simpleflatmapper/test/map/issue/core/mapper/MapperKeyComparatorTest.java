package org.simpleflatmapper.test.map.issue.core.mapper;

import org.junit.Assert;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.mapper.MapperKeyComparator;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.CheckedConsumer;

import java.lang.reflect.Array;
import java.util.Random;


public class MapperKeyComparatorTest {

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


    @SuppressWarnings("unchecked")
    public static <K extends FieldKey<K>> MapperKey<K>[] generateKeys(KeyProducer<K> producer) throws Exception {
        ListCollector<MapperKey<K>> collector = new ListCollector<MapperKey<K>>();

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
                    Assert.assertEquals(0, comparator.compare(key1, key2));
                    Assert.assertEquals(0, comparator.compare(key2, key1));
                } else {
                    int d = comparator.compare(key1, key2);
                    Assert.assertFalse(d == 0);
                    if (d < 0) {
                        Assert.assertTrue(comparator.compare(key2, key1) > 0);
                    } else {
                        Assert.assertTrue(comparator.compare(key2, key1) < 0);
                    }
                }
                Assert.assertEquals(0, comparator.compare(key2, key2));
                Assert.assertEquals(0, comparator.compare(key1, key1));


            }
        }

    }

    public interface KeyProducer<K extends FieldKey<K>> {
        void produces(CheckedConsumer<MapperKey<K>> consumer, String[] names) throws Exception;
    }

    public static abstract class AbstractKeyProducer<K extends FieldKey<K>> implements KeyProducer<K> {
        private final Class<K> keyClass;

        protected AbstractKeyProducer(Class<K> keyClass) {
            this.keyClass = keyClass;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void produces(CheckedConsumer<MapperKey<K>> consumer, String[] names) throws Exception {
            K[] columns1 = (K[]) Array.newInstance(keyClass, names.length);
            K[] columns2 = (K[]) Array.newInstance(keyClass, names.length);

            for(int i = 0; i < names.length; i++) {
                columns1[i] = newKey(names[i], i);
                columns2[i] = newKey(names[i], i+1);
            }
            consumer.accept(new MapperKey<K>(columns1));
            consumer.accept(new MapperKey<K>(columns2));
        }

        protected abstract K newKey(String name, int i);
    }


}