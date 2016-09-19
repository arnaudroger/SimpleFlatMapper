package org.simpleflatmapper.jdbc.property.time;

import org.junit.Test;

import java.time.ZoneOffset;

import static org.junit.Assert.*;

public class ZoneOffsetPropertyTest {

    @Test
    public void test() {
        ZoneOffset offset = ZoneOffset.ofHours(-3);
        assertEquals(offset, new ZoneOffsetProperty(offset).get());
    }
}