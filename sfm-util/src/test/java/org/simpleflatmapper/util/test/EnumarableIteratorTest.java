package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ArrayEnumarable;
import org.simpleflatmapper.util.EnumarableIterator;


import static org.junit.Assert.*;

public class EnumarableIteratorTest {

    public static final String[] STRINGS = {"str1", "str2", "str3"};
    @Test
    public void test() {
        ArrayEnumarable<String> e1 = new ArrayEnumarable<String>(STRINGS);
        EnumarableIterator<String> i1 = new EnumarableIterator<String>(new ArrayEnumarable<String>(STRINGS));

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