package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ArrayEnumerable;
import org.simpleflatmapper.util.EnumerableIterator;


import static org.junit.Assert.*;

public class EnumerableIteratorTest {

    public static final String[] STRINGS = {"str1", "str2", "str3"};
    @Test
    public void test() {
        ArrayEnumerable<String> e1 = new ArrayEnumerable<String>(STRINGS);
        EnumerableIterator<String> i1 = new EnumerableIterator<String>(new ArrayEnumerable<String>(STRINGS));

        while(e1.next()) {
            assertTrue(i1.hasNext());
            assertEquals(e1.currentValue(), i1.next());

            try {
                i1.remove();
                fail();
            } catch (UnsupportedOperationException e) {

            }
        }

        assertFalse(i1.hasNext());
    }

}