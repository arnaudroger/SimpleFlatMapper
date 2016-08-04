package org.simpleflatmapper.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class UUIDHelperTest {
    @Test
    public void test() throws Exception {
        UUID uuid = UUID.randomUUID();

        Assert.assertEquals(uuid, UUIDHelper.fromBytes(UUIDHelper.toBytes(uuid)));
    }


}