package org.simpleflatmapper.core.utils;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class UUIDHelperTest {
    @Test
    public void test() throws Exception {
        UUID uuid = UUID.randomUUID();

        assertEquals(uuid, UUIDHelper.fromBytes(UUIDHelper.toBytes(uuid)));
    }


}