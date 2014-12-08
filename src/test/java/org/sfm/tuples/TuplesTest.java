package org.sfm.tuples;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TuplesTest {
    @Test
    public void testIsTuple() {
        assertTrue(Tuples.isTuple(Tuple2.class));
        assertTrue(Tuples.isTuple(Tuple3.class));
        assertTrue(Tuples.isTuple(Tuple4.class));
        assertTrue(Tuples.isTuple(Tuple5.class));
        assertFalse(Tuples.isTuple(Tuples.class));
    }
}
