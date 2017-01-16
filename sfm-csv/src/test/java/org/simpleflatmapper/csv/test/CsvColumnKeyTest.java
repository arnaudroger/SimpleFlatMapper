package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvColumnKey;

import static org.junit.Assert.*;

public class CsvColumnKeyTest {

    @Test
    public void testIsAlias() throws Exception {
        assertFalse(new CsvColumnKey("c" ,0).isAlias());
        assertTrue(new CsvColumnKey("c", 0).alias("b").isAlias());
    }

    @Test
    public void testGetParent() throws Exception {
        assertNull(new CsvColumnKey("c" ,0).getParent());
        assertNotNull(new CsvColumnKey("c", 0).alias("b").getParent());
    }

    @Test
    public void testHashCode() throws Exception {
        assertNotEquals(new CsvColumnKey("col", 2).hashCode(), new CsvColumnKey("col", 3).hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(new CsvColumnKey("col", 2), new CsvColumnKey("col", 2));
        assertEquals(new CsvColumnKey("col", 2).alias("h"), new CsvColumnKey("col", 2).alias("h"));
        // parent not int equals
        assertEquals(new CsvColumnKey("col2", 2).alias("h"), new CsvColumnKey("col3", 2).alias("h"));
        assertNotEquals(new CsvColumnKey("col", 2), new CsvColumnKey("col1", 2));
        assertNotEquals(new CsvColumnKey("col", 2), new CsvColumnKey("col", 4));
    }

    @Test
    public void testGetType() {
        assertEquals(CharSequence.class, new CsvColumnKey("col", 2).getType(null));
    }
    @Test
    public void testToString() throws Exception {
        assertEquals("CsvColumnKey{name='col2', index=2}", new CsvColumnKey("col2", 2).toString());
    }
}