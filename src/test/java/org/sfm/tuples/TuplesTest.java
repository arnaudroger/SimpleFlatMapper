package org.sfm.tuples;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

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

    Tuple2<String, String> aabb = new Tuple2<String, String>("aa", "bb");
    Tuple3<String, String, String> aabbcc = aabb.tuple3("cc");
    Tuple4<String, String, String, String> aabbccdd = aabbcc.tuple4("dd");
    Tuple5<String, String, String, String, String> aabbccddee = aabbccdd.tuple5("ee");
    @Test
    public void testTuple2() {
        assertEquals(new Tuple2<String, String>("aa", "bb"), aabb);
        assertEquals(new Tuple2<String, String>("aa", "bb").hashCode(), aabb.hashCode());
        assertNotEquals(new Tuple2<String, String>("aa", "bbb"), aabb);
        assertNotEquals(new Tuple2<String, String>("aa", "bbb").hashCode(), aabb.hashCode());
        assertNotEquals(new Tuple2<String, String>("aaa", "bb"), aabb);
        assertEquals("Tuple2{element0=aa, element1=bb}", aabb.toString());
    }

    @Test
    public void testTuple3() {
        assertEquals(aabb.tuple3("cc"), aabbcc);
        assertNotEquals(aabb.tuple3("ccc"), aabbcc);
        assertEquals(aabb.tuple3("cc").hashCode(), aabbcc.hashCode());
        assertNotEquals(aabb.tuple3("ccc").hashCode(), aabbcc.hashCode());
        assertNotEquals(aabb, aabbcc);
        assertEquals("Tuple3{element0=aa, element1=bb, element2=cc}", aabbcc.toString());
    }

    @Test
    public void testTuple4() {
        assertEquals(aabbcc.tuple4("dd"), aabbccdd);
        assertNotEquals(aabbcc.tuple4("ddd"), aabbccdd);
        assertEquals(aabbcc.tuple4("dd").hashCode(), aabbccdd.hashCode());
        assertNotEquals(aabbcc.tuple4("ddd").hashCode(), aabbccdd.hashCode());
        assertNotEquals(aabbcc, aabbccdd);
        assertEquals("Tuple4{element0=aa, element1=bb, element2=cc, element3=dd}", aabbccdd.toString());
    }

    @Test
    public void testTuple5() {
        assertEquals(aabbccdd.tuple5("ee"), aabbccddee);
        assertNotEquals(aabbccdd.tuple5("eee"), aabbccddee);
        assertEquals(aabbccdd.tuple5("ee").hashCode(), aabbccddee.hashCode());
        assertNotEquals(aabbccdd.tuple5("eee").hashCode(), aabbccddee.hashCode());
        assertNotEquals(aabbccdd, aabbccddee);
        assertEquals("Tuple5{element0=aa, element1=bb, element2=cc, element3=dd, element4=ee}", aabbccddee.toString());
    }

    @Test
    public void testTuples() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Tuple2<?, ?> currentTuple = new Tuple2<Object, Object>("v1", "v2");
        for(int i = 3; i <= 32; i++) {
            Method m = currentTuple.getClass().getDeclaredMethod("tuple" + i, Object.class);

            Tuple2<?, ?> nextTuple = (Tuple2<?, ?>) m.invoke(currentTuple, "v" + i);
            Tuple2<?, ?> sameTuple = (Tuple2<?, ?>) m.invoke(currentTuple, "v" + i);
            Tuple2<?, ?> diffTuple = (Tuple2<?, ?>) m.invoke(currentTuple, "d" + i);


            assertEquals(nextTuple, sameTuple);
            assertEquals(nextTuple.hashCode(), sameTuple.hashCode());
            assertEquals(nextTuple.toString(), sameTuple.toString());

            assertNotEquals(nextTuple, diffTuple);
            assertNotEquals(nextTuple.hashCode(), diffTuple.hashCode());
            assertNotEquals(nextTuple.toString(), diffTuple.hashCode());

            assertTrue(Tuples.isTuple(nextTuple.getClass()));

            currentTuple = nextTuple;
        }
    }
}
