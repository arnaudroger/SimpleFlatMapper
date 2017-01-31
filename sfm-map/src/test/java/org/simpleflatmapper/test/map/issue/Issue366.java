package org.simpleflatmapper.test.map.issue;

import org.junit.Test;
import org.simpleflatmapper.map.BreakDetector;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.KeysDefinition;
import org.simpleflatmapper.map.context.impl.BreakDetectorImpl;
import org.simpleflatmapper.test.map.SampleFieldKey;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Issue366 {

    @Test
    public void testBreakDetector() throws Exception {

        KeySourceGetter<SampleFieldKey, Object> keySourceGetter = mock(KeySourceGetter.class);

        Object object = new Object();
        SampleFieldKey key = new SampleFieldKey("k", 1);

        when(keySourceGetter.getValue(key, object)).thenReturn(new byte[] {1, 2}, new byte[] {1, 2}, new byte[] {1, 3});

        KeysDefinition<Object, SampleFieldKey> keyDefinition =
                new KeysDefinition<Object, SampleFieldKey>(Arrays.asList(key), keySourceGetter, 0, -1);
        BreakDetector<Object> breakDetector = new BreakDetectorImpl<Object, SampleFieldKey>(keyDefinition, null);

        breakDetector.handle(object);

        assertTrue(breakDetector.isBroken());
        breakDetector.handle(object);
        assertFalse(breakDetector.isBroken());
        breakDetector.handle(object);
        assertTrue(breakDetector.isBroken());

    }
}
