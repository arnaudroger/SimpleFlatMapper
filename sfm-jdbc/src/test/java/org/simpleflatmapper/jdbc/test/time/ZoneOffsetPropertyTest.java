package org.simpleflatmapper.jdbc.test.time;

import org.junit.Test;
import org.simpleflatmapper.jdbc.property.time.ZoneOffsetProperty;

import java.time.ZoneOffset;

import static org.junit.Assert.*;

public class ZoneOffsetPropertyTest {

    @Test
    public void test() {
        ZoneOffset offset = ZoneOffset.ofHours(-3);
        assertEquals(offset, new ZoneOffsetProperty(offset).get());
    }
}