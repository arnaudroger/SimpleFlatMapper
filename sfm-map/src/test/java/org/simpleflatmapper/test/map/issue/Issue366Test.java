package org.simpleflatmapper.test.map.issue;

import org.junit.Test;
import org.simpleflatmapper.map.context.KeyAndPredicate;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.KeyDefinition;
import org.simpleflatmapper.map.context.impl.BreakDetector;
import org.simpleflatmapper.test.map.SampleFieldKey;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Issue366Test {

    @Test
    public void testBreakDetector() throws Exception {

        KeySourceGetter<SampleFieldKey, Object> keySourceGetter = mock(KeySourceGetter.class);

        Object object = new Object();
        SampleFieldKey key = new SampleFieldKey("k", 1);

        when(keySourceGetter.getValue(key, object)).thenReturn(new byte[] {1, 2}, new byte[] {1, 2}, new byte[] {1, 3});

        KeyDefinition<Object, SampleFieldKey> keyDefinition =
                new KeyDefinition<Object, SampleFieldKey>(
                        new KeyAndPredicate[] { new KeyAndPredicate<Object[], SampleFieldKey>(key, null) },
                        keySourceGetter, 0);
        BreakDetector<Object> breakDetector = new BreakDetector<Object>(keyDefinition);

        assertTrue(breakDetector.broke(object));
        assertFalse(breakDetector.broke(object));
        assertTrue(breakDetector.broke(object));

    }
}
