package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.TupleHelper;

public class JoolTupleTest {

    @Test
    public void testIsJoolTuple() {
        TupleHelper.isTuple(org.jooq.lambda.tuple.Tuple.tuple(1, 2).getClass());
        TupleHelper.isTuple(org.jooq.lambda.tuple.Tuple.tuple().getClass());
    }
}
