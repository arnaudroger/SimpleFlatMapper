package org.simpleflatmapper.util;

import org.junit.Test;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.TupleHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TupleHelperTest {
    @Test
    public void testIsTuple() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Tuple2<?, ?> currentTuple = new Tuple2<Object, Object>("v1", "v2");
        for(int i = 3; i <= 32; i++) {
            Method m = currentTuple.getClass().getDeclaredMethod("tuple" + i, Object.class);

            Tuple2<?, ?> nextTuple = (Tuple2<?, ?>) m.invoke(currentTuple, "v" + i);
            assertTrue(TupleHelper.isTuple(nextTuple.getClass()));

            currentTuple = nextTuple;
        }

        assertFalse(TupleHelper.isTuple(String.class));
    }


    //IFJAVA8_START
    @Test
    public void testIsJoolTuple() {
        TupleHelper.isTuple(org.jooq.lambda.tuple.Tuple2.class);
        TupleHelper.isTuple(new org.jooq.lambda.tuple.Tuple0() {
        }.getClass());
    }
    //IFJAVA8_END


}
