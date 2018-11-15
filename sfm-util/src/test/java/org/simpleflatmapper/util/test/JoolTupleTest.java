package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.TupleHelper;

public class JoolTupleTest {

    @Test
    public void testIsJoolTuple() {
        TupleHelper.isTuple(org.jooq.lambda.tuple.Tuple2.class);
        TupleHelper.isTuple(new org.jooq.lambda.tuple.Tuple0() {
        }.getClass());
    }
}
