package org.simpleflatmapper.test.map.context.impl;

import org.junit.Test;
import org.simpleflatmapper.map.context.Key;
import org.simpleflatmapper.map.context.impl.KeyObjectStore;
import org.simpleflatmapper.map.context.impl.MultiValueKey;

import static org.junit.Assert.*;

public class KeyObjectStoreTest {


    @Test
    public void testSimpleGetPut() {
        KeyObjectStore keyObjectStore = new KeyObjectStore();

        Key key = new MultiValueKey(new Object[] {new Object()});
        Object value = new Object();
        keyObjectStore.put(key, value);

        try {
            keyObjectStore.put(key, value);

            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }

        assertSame(value, keyObjectStore.get(key));
    }


    @Test
    public void testRandomData() {
        KeyObjectStore keyObjectStore = new KeyObjectStore();

        Object[][] data = new Object[10000][];

        for(int i = 0; i < data.length; i++) {
            data[i] = new Object[] { newKey(), new Object()};
        }

        for(int i = 0; i < data.length; i++) {
            assertNull(keyObjectStore.get((Key) data[i][0]));
            keyObjectStore.put((Key) data[i][0], data[i][1]);
            assertEquals(data[i][1], keyObjectStore.get((Key) data[i][0]));
        }


        for(int i = 0; i < data.length; i++) {
            assertEquals(data[i][1], keyObjectStore.get((Key) data[i][0]));
        }

    }

    private Key newKey() {
        return new MultiValueKey(new Object[] {new Object()});
    }
}