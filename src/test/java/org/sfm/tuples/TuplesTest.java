package org.sfm.tuples;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TuplesTest {
    @Test
    public void testIsTuple() {
        assertTrue(Tuples.isTuple(Tuple2.class));
        assertTrue(Tuples.isTuple(Tuple3.class));
        assertTrue(Tuples.isTuple(Tuple4.class));
        assertTrue(Tuples.isTuple(Tuple5.class));
        assertFalse(Tuples.isTuple(Tuples.class));
    }

    @Test
    public void typeDefRejectInvalidTuples() {
        try {
            Tuples.tupleImplementationTypeDef(Tuple2.class, String.class);
            fail("Expected Exception");
        } catch(Exception e) {
            // expected
        }
    }
}
