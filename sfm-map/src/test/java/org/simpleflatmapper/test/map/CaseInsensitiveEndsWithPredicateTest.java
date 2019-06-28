package org.simpleflatmapper.test.map;

import org.junit.Test;
import org.simpleflatmapper.map.CaseInsensitiveEndsWithPredicate;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.util.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CaseInsensitiveEndsWithPredicateTest {


    @Test
    public void testOne() {
        CaseInsensitiveEndsWithPredicate end = CaseInsensitiveEndsWithPredicate.of("end");
        assertFalse(end.test(SampleFieldKey.KEY_FACTORY.newKey("a", 0)));
        assertFalse(end.test(SampleFieldKey.KEY_FACTORY.newKey("aaa", 0)));
        assertTrue(end.test(SampleFieldKey.KEY_FACTORY.newKey("end", 0)));
        assertTrue(end.test(SampleFieldKey.KEY_FACTORY.newKey("a_end", 0)));
    }

    @Test
    public void testMany() {
        Predicate<FieldKey<?>> any = CaseInsensitiveEndsWithPredicate.any("end", "fin");
        assertFalse(any.test(SampleFieldKey.KEY_FACTORY.newKey("a", 0)));
        assertFalse(any.test(SampleFieldKey.KEY_FACTORY.newKey("aaa", 0)));
        assertTrue(any.test(SampleFieldKey.KEY_FACTORY.newKey("end", 0)));
        assertTrue(any.test(SampleFieldKey.KEY_FACTORY.newKey("a_end", 0)));
        assertTrue(any.test(SampleFieldKey.KEY_FACTORY.newKey("fin", 0)));
        assertTrue(any.test(SampleFieldKey.KEY_FACTORY.newKey("a_fin", 0)));
    }
}
