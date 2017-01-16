package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.TupleHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JoolTupleTest {

    @Test
    public void testIsJoolTuple() {
        TupleHelper.isTuple(org.jooq.lambda.tuple.Tuple2.class);
        TupleHelper.isTuple(new org.jooq.lambda.tuple.Tuple0() {
        }.getClass());
    }
}
