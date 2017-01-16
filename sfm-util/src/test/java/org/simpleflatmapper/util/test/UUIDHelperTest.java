package org.simpleflatmapper.util.test;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.util.UUIDHelper;

import java.util.UUID;

public class UUIDHelperTest {
    @Test
    public void test() throws Exception {
        UUID uuid = UUID.randomUUID();

        Assert.assertEquals(uuid, UUIDHelper.fromBytes(UUIDHelper.toBytes(uuid)));
    }


}