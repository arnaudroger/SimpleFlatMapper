package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ConstantPredicate;

import static org.junit.Assert.*;

public class ConstantPredicateTest {
    @Test
    public void truePredicate() throws Exception {
        assertTrue(ConstantPredicate.<Object>truePredicate().test(null));
    }

    @Test
    public void falsePredicate() throws Exception {
        assertFalse(ConstantPredicate.<Object>falsePredicate().test(null));
    }

}